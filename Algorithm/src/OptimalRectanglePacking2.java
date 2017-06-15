import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Improvement on previous optimal rectangle packer, created as a separate class as not to
 * destroy code previous class.
 *
 */

public class OptimalRectanglePacking2 implements Solver {


    // prints out the placement matrix after each rectangle placed (for debugging purposes)
    private static boolean showEachPlacement = false;
    private static boolean showFeasibleSolutions = false;

    private static boolean anytime;                       // true if anytime; false if iterative

    // dependent on current problem
    private static boolean rotationsAllowed;
    private static int fixedHeight;                        // 0 if height is free; value of the fixed height otherwise

    // dependent on given set of rectangles
    private long totalRectArea;      // total area of all rectangles combined. Computed at the start of the solve method

    // dependent on given set of rectangles and current orientation
    private HashMap<Integer, Integer> mapWidth;     // map each rectangle to its width using its index as the key
    private HashMap<Integer, Integer> mapHeight;    // map each rectangle to its height using its index as the key

    // dependent on given bounding box
    private int[][] placementMatrix;                    // matrix that holds the positions of rectangles
    private int[][] emptyRowMatrix;
    private int[][] emptyColumnMatrix;

    // dependent on search depth containment algorithm
    private long[] widthUnplacedHistogram;
    private long[] heightUnplacedHistogram;

    private int[] histogram;


    public OptimalRectanglePacking2(boolean rotations, int height) {
        this.anytime = true;
        this.rotationsAllowed = rotations;
        this.fixedHeight = height;
    }

    /**
     *
     *      Illustration placement matrix and coordinate system
     *
     *       y
     *       |   |   |   |   |
     *     4 ----+---+---+---+---
     *       |   |   |   |   |
     *     3 ----+---+---+---+---
     *       |   |   |   |   |
     *     2 ----+---+---+---+---
     *       |   |   |   |   |
     *     1 ----+---+---+---+---
     *       |   |   |   |   |
     *       O---|---|---|---|--- x
     *           1   2   3   4
     *
     *      The position of a rectangle is given in terms of its coordinates,
     *          the grid point of its left bottom corner.
     *      The placement matrix specifies which unit squares are occupied
     *          and by what rectangle. The unit squares are indexed by the
     *          coordinates of their left bottom corners.
     *      For example, the rectangle that occupies the set [2,3]X[1,3]
     *          (thus with position (2,1)) fills the cells [2][1] and
     *          [2][2] of the placement matrix
     *
     */
    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {

        totalRectArea = 0;
        for (Rectangle rectangle : rectangles) {
            totalRectArea += rectangle.width * rectangle.height;
        }

        if (anytime) {
            if (rotationsAllowed) {

                int n = rectangles.length;

                // set up local variables that store the best solution
                Rectangle optimalBin = (fixedHeight == 0) ?
                        new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, -1) :
                        new Rectangle(Integer.MAX_VALUE, fixedHeight, -1);
                Rectangle[] optimalSolution = null;

                // find the optimal solution for each combination of rotations
                outer_combination:
                for (int combination = 0; combination < (1 << n); ++combination) {
                    Rectangle[] arr = copyRectangles(rectangles);

                    // rotate the rectangles that belong to this combination
                    for (int bit = 0; bit < n; ++bit) {
                        if ((combination & (1 << bit)) > 0) {
                            arr[bit].rotate();

                            // discard this combination if the height of the rectangle
                            // is larger than the fixed height
                            if (fixedHeight > 0 && arr[bit].height > fixedHeight) {
                                continue outer_combination;
                            }
                        }
                    }

                    // comupte optimal packing
                    Pair<Rectangle[], Rectangle> solution = anytimeSolution(arr);

                    // check if this rotation combination is better
                    if ((long) optimalBin.width * (long) optimalBin.height >
                            (long) solution.second.width * (long) solution.second.height) {
                        optimalSolution = copyRectangles(solution.first);

                        optimalBin.width = solution.second.width;
                        optimalBin.height = solution.second.height;
                    }
                }

                return optimalSolution;

            } else {
                // compute optimal packing
                return anytimeSolution(rectangles).first;
            }
        } else {
            // TODO (Maybe) Implement the iterative solution
            return iterativeSolution(rectangles).first;
        }
    }



    /**
     * Generate an anytime solution - i.e. generate an initial solution that keeps on improving.
     * Can be stopped at anytime to get a pretty good enclosing bin.
     *
     * @param rectangles the given array of rectangles
     * @return an array in which rectangles are placed optimally along with the enclosing bin
     */
    private Pair<Rectangle[], Rectangle> anytimeSolution(Rectangle[] rectangles) {

        // set up the local variables that store the optimal solution
        Rectangle optimalBin;
        Rectangle[] optimalPlacement;
        // variables to store the size of the bounding box being tested currently
        int height;
        int width;

        // set up mapWidth and mapHeight mapping
        // (used to not traverse the entire placement matrix when placing a rectangle)
        mapWidth = new HashMap<>();
        mapHeight = new HashMap<>();
        for (int i = 0; i < rectangles.length; i++) {
            mapWidth.put(rectangles[i].index, rectangles[i].width);
            mapHeight.put(rectangles[i].index, rectangles[i].height);
        }

        // generate a greedy solution to start with
        Pair<Rectangle[], Rectangle> greedySolution = getGreedySolution(rectangles);
        // store results (best solution so far)
        optimalPlacement = greedySolution.first;
        optimalBin = greedySolution.second;
        // store current bounding box size
        height = optimalBin.height;
        width = optimalBin.width;

        if (showFeasibleSolutions) {
            printPlacementMatrix(placementMatrix);
        }

        // sort rectangles on area (descending)
        Rectangle[] sortedRects = copyRectangles(rectangles);
        Arrays.sort( sortedRects, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return o2.height * o2.width - o1.height * o1.width;
            }
        });


        // start trying smaller and smaller bounding boxes

        // determine when to stop shrinking the rectangle
        int minWidth = 0; // the minimal width the bounding box should have
        for (Rectangle rectangle : sortedRects) {
            minWidth = Math.max(minWidth, rectangle.width); // every rectangle needs to fit in the bounding box
        }

        // whether the previous bounding box tried can pack all rectangles without overlap
        boolean feasible = true;   // true because of the greedy solution tried before

        change_bin:
        while (true) {

            // change dimensions bounding box
            if (feasible) {
                // decrease width until area is smaller than the area of the optimal bounding box
                while ((long) width * (long) height >= (long) optimalBin.width * (long) optimalBin.height) {
                    width--;    // shrink bounding box
                }
                if (width < minWidth) {
                    break;  // no smaller bounding box possible
                }
            } else {
                // increase height by one (only if height is not fixed)
                if (fixedHeight == 0) {
                    height++;   // enlarge bounding box
                    // decrease width until area is smaller than the area of the optimal bounding box
                    while ((long) width * (long) height >= (long) optimalBin.width * (long) optimalBin.height) {
                        width--;    // shrink bounding box
                    }
                    if (width < minWidth) {
                        break;  // no smaller bounding box possible
                    }

                } else {
                    // if the bounding box is infeasible and the height is fixed,
                    // we cannot enlarge the bounding box, thus we've already found
                    // the smallest possible bounding box.
                    break;
                }
            }

            // determine whether the bounding box is feasible

            // check if the area of the bounding box is not smaller than the
            // total area of the rectangles
            if ((long) width * (long) height < totalRectArea) {
                feasible = false;
                continue change_bin;    // we can skip calling the containment algorithm
            }

            //TODO: perform more tests

            // create a new placement matrix for the new bounding box
            generateNewMatrices(width, height, false);

            // create new histograms empty space
            long[] rowHistogram = new long[width];
            rowHistogram[width - 1] += (long) width * (long) height;
            long[] columnHistogram = new long[height];
            columnHistogram[height - 1] += (long) width * (long) height;
            // set up other stuff for wasted space pruning
            widthUnplacedHistogram = new long[width];
            heightUnplacedHistogram = new long[height];
            for (Rectangle rectangle : sortedRects) {
                long area = (long) rectangle.width *(long) rectangle.height;
                widthUnplacedHistogram[rectangle.width - 1] += area;
                heightUnplacedHistogram[rectangle.height - 1] += area;
            }

            // call the containment algorithm (!!! if solution is found, the solution is stored in sortedRects)
            feasible = containmentAlgorithm(width, height, sortedRects, 0, rowHistogram, columnHistogram);

            // if the bounding box is feasible, store the new solution as the optimal solution
            // (it is optimal because we only test bounding boxes with a smaller area)
            if (feasible) {
                // store new bounding box
                optimalBin.width = width;
                optimalBin.height = height;
                // store solution (unsorted)
                for (int i = 0; i < sortedRects.length; i++) {
                    optimalPlacement[sortedRects[i].index] = copyRectangle(sortedRects[i]);
                }

                if (showFeasibleSolutions) {
                    printPlacementMatrix(placementMatrix);
                }
            }
        }



        // return optimal solution
        return new Pair<> (optimalPlacement, optimalBin);
    }

    /**
     * Generates a greedy solution.
     *
     * @param rectangles
     * @modifies    rectangles, placementMatrix
     * @return
     */
    private Pair<Rectangle[],Rectangle> getGreedySolution (Rectangle[] rectangles) {

        Rectangle[] sortedRects = copyRectangles(rectangles);

        // sort the rectangles by height (descending)
        Arrays.sort(sortedRects, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return o2.height - o1.height;
            }
        });

        int height = (fixedHeight == 0) ? sortedRects[0].height : fixedHeight;
        int maxWidth = 0;
        for (Rectangle rectangle : sortedRects) {
            maxWidth += rectangle.width;     // rectangles have a width of at most 10^4, so if
                                            // the number of rectangles is less than 214748, an integer for maxWidth suffices
        }

        // create a new placement matrix
        generateNewMatrices(maxWidth, height, true);

        // greedily place rectangles, from bottom to top, from left to right
        rectangle_loop:
        for (int i = 0; i < sortedRects.length; i++) {
            for (int x = 0; x < maxWidth; x++) {
                for (int y = 0; y < height; y++) {

                    // skip spaces that are already occupied by other rectangles
                    if (placementMatrix[x][y] >= 0) {
                        y += mapHeight.get(placementMatrix[x][y]) - 1;
                        continue;                   // try next coordinates
                    }

                    // check if it can be placed
                    if (canPlaceAt(x, y, sortedRects[i], maxWidth, height)) {
                        // place the rectangle!
                        placeRectangle(x, y, sortedRects[i], maxWidth, height, false, null, null);
                        continue rectangle_loop;    // place the next rectangle
                    }

                }
            }
        }

        // determine the width of the current bounding box
        int width = 0;
        for (Rectangle rectangle : sortedRects) {
            width = Math.max(width, rectangle.x + rectangle.width);
        }

        // unsort the rectangles
        for (int i = 0; i < rectangles.length; i++) {
            rectangles[sortedRects[i].index] = copyRectangle(sortedRects[i]);
        }

        // create the bounding box
        Rectangle boundingBox = new Rectangle(width, height, -1);

        // return optimal solution
        return new Pair<Rectangle[], Rectangle> (rectangles, boundingBox);
    }


    /**
     * A function that solves the containment problem - i.e. given a list of rectangles,
     * can they be placed in an enclosing bin of given width and height ?
     *
     * The placement of rectangles is stored in the parameter rectangles.
     *
     * @param width the width of the enclosing bin
     * @param height the height of the enclosing bin
     * @param rectangles the given list of rectangles
     * @modifies rectangles
     * @return true if the problem can be solved; false otherwise
     */
    private boolean containmentAlgorithm(int width, int height, Rectangle[] rectangles,
                                         int iteration, long[] rowHistogram, long[] columnHistogram) {

        if (iteration == rectangles.length) { // a solution of packing the rectangles into the bin has been found
            return true;
        }

        // Prune the current subtree if the partial solution cannot be
        // extended to a complete solution
        if (canPruneWastedSpace(width, height, rectangles, rowHistogram, columnHistogram, iteration)) {
            if (showEachPlacement) {
                System.out.println("pruned by wasted space");
            }
            return false;
        }

        // Place the next rectangle (from left to right, from bottom to top)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (placementMatrix[x][y] >= 0) { // skip spaces that are already occupied by other rectangles
                    x += mapWidth.get(placementMatrix[x][y]) - 1;
                    continue;
                }

                // try to place the rectangle
                if (canPlaceAt(x, y, rectangles[iteration], width, height)) {

                    // create new histograms for next iteration
                    long[] newRowHistogram = copyHistogram(rowHistogram);
                    long[] newColumnHistogram = copyHistogram(columnHistogram);
                    placeRectangle(x, y, rectangles[iteration], width, height, true,
                            newRowHistogram, newColumnHistogram);
                    long area = (long) rectangles[iteration].width * (long) rectangles[iteration].height;
                    widthUnplacedHistogram[rectangles[iteration].width - 1] -= area;
                    heightUnplacedHistogram[rectangles[iteration].height - 1] -= area;

                    // call for next iteration
                    if (containmentAlgorithm(width, height, rectangles, iteration + 1,
                            newRowHistogram, newColumnHistogram)) {
                        // if this partial solution can be extended to a complete iteration,
                        // send this message up the iteration path
                        return true;
                    } else {
                        // if this partial solution cannot be extended to a complete iteration,
                        // clear the rectangle and continue the loop to try different positions.
                        clearRectangle(x, y, rectangles[iteration], width, height, true);
                    }
                }
            }
        }

        // the rectangle could not be placed anywhere, so the partial solution could
        // not be extended to a complete solution
        return false;
    }

    private boolean canPruneWastedSpace(int width, int height, Rectangle[] rectangles,
                                        long[] rowHistogram, long[] columnHistogram, int iteration) {

        long wastedArea;
        long carriedArea;

        // look at vertically wasted space
        wastedArea = 0;
        carriedArea = 0;
        for (int i = 0; i < width; i++) {
            if (rowHistogram[i] >= carriedArea + widthUnplacedHistogram[i]) {
                wastedArea += rowHistogram[i] - carriedArea - widthUnplacedHistogram[i];
                carriedArea = 0;
            } else {
                carriedArea = carriedArea + widthUnplacedHistogram[i] - rowHistogram[i];
            }
        }
        if (wastedArea + totalRectArea > (long) width * (long) height) {
            return true;
        }

        // look at horizontally wasted space
        wastedArea = 0;
        carriedArea = 0;
        for (int i = 0; i < height; i++) {
            if (columnHistogram[i] >= carriedArea + heightUnplacedHistogram[i]) {
                wastedArea += columnHistogram[i] - carriedArea - heightUnplacedHistogram[i];
                carriedArea = 0;
            } else {
                carriedArea = carriedArea + heightUnplacedHistogram[i] - columnHistogram[i];
            }
        }
        if (wastedArea + totalRectArea > (long) width * (long) height) {
            return true;
        }

        return false;
    }


    /**
     * Creates new matrices that keep information on each cell of the bounding box of given size.
     *
     * @param width
     * @param height
     * @param placementMatrixOnly
     */
    private void generateNewMatrices (int width, int height, boolean placementMatrixOnly) {

        // create new matrices
        placementMatrix = new int[width][height];
        if (!placementMatrixOnly) {
            emptyRowMatrix = new int[width][height];
            emptyColumnMatrix = new int[width][height];
        }

        // fill em up
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                placementMatrix[x][y] = -1;
                if (!placementMatrixOnly) {
                    emptyRowMatrix[x][y] = width;
                    emptyColumnMatrix[x][y] = height;
                }
            }
        }
    }


    /**
     * Check if we can place a rectangle at (x, y) in a bin of given width and height.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rectangle the rectangle to be placed
     * @param binWidth the width of the current enclosing bin
     * @param binHeight the height of the current enclosing bin
     * @return true if the rectangle can be placed at (x, y); false otherwise
     */
    private boolean canPlaceAt(int x, int y, Rectangle rectangle, int binWidth, int binHeight) {

        // exceeds the boundary of the bin
        if (y + rectangle.height > binHeight ||
                x + rectangle.width > binWidth) {
            return false;
        }

        // check left and right edges of the rectangle (including corners)
        for (int j = y; j < y + rectangle.height; j++) {
            if (placementMatrix[x][j] >= 0 || placementMatrix[x + rectangle.width - 1][j] >= 0) {
                return false;
            }
        }

        // check bottom and top edges (excluding edges)
        for (int i = x + 1; i < x + rectangle.width - 1; i++) {
            if (placementMatrix[i][y] >= 0 || placementMatrix[i][y + rectangle.height - 1] >= 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Place the rectangle at (x, y) in a bin of given width and height,
     * by marking the space occupied by it with its index
     * in the placement matrix.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rectangle the rectangle to be placed
     */
    private void placeRectangle(int x, int y, Rectangle rectangle, int width, int height,
                                boolean updateEmptyMatrices, long[] rowHistogram, long[] columnHistogram) {

        // fill the placement matrix
        for (int i = x; i < x + rectangle.width; i++) {
            for (int j = y; j < y + rectangle.height; j++) {
                placementMatrix[i][j] = rectangle.index;
            }
        }

        if (updateEmptyMatrices) {
            // update empty rows
            for (int j = y; j < y + rectangle.height; j++) {
                // check left of the rectangle
                int freeCellsLeft = 0;
                int i = x - 1;
                while (i >= 0 && placementMatrix[i][j] == -1) {
                    freeCellsLeft++;
                    i--;
                }
                // update left of the rectangle
                for (int k = i + 1; k < x; k++) {
                    emptyRowMatrix[k][j] = freeCellsLeft;
                }
                // update right of the rectangle
                int freeCellsRight = emptyRowMatrix[x][j] - freeCellsLeft - rectangle.width;
                for (int k = x + rectangle.width; k < x + rectangle.width + freeCellsRight; k++) {
                    emptyRowMatrix[k][j] = freeCellsRight;
                }

                // update row histogram
                rowHistogram[emptyRowMatrix[x][j] - 1] -= emptyRowMatrix[x][j];
                if (freeCellsLeft > 0) {
                    rowHistogram[freeCellsLeft - 1] += freeCellsLeft;
                }
                if (freeCellsRight > 0) {
                    rowHistogram[freeCellsRight - 1] += freeCellsRight;
                }
            }

            // update empty columns
            for (int i = x; i < x + rectangle.width; i++) {
                // check below the rectangle
                int freeCellsBelow = 0;
                int j = y - 1;
                while (j >= 0 && placementMatrix[i][j] == -1) {
                    freeCellsBelow++;
                    j--;
                }
                // update below the rectangle
                for (int l = j + 1; l < y; l++) {
                    emptyColumnMatrix[i][l] = freeCellsBelow;
                }
                // update above the rectangle
                int freeCellsAbove = emptyColumnMatrix[i][y] - freeCellsBelow - rectangle.height;
                for (int l = y + rectangle.height; l < y + rectangle.height + freeCellsAbove; l++) {
                    emptyColumnMatrix[i][l] = freeCellsAbove;
                }

                // update column histogram
                columnHistogram[emptyColumnMatrix[i][y] - 1] -= emptyColumnMatrix[i][y];
                if (freeCellsBelow > 0) {
                    columnHistogram[freeCellsBelow - 1] += freeCellsBelow;
                }
                if (freeCellsAbove > 0) {
                    columnHistogram[freeCellsAbove - 1] += freeCellsAbove;
                }
            }

        }

        // set the coordinates of the rectangle
        rectangle.x = x;
        rectangle.y = y;

        // for debug purposes (Gives a nice insight into how the algorithm works!)
        if (showEachPlacement) {
            printPlacementMatrix(placementMatrix);
            if (updateEmptyMatrices) {
                //printPlacementMatrix(emptyRowMatrix);
                //printPlacementMatrix(emptyColumnMatrix);
                //printHistogram(rowHistogram);
                //printHistogram(columnHistogram);
            }
        }
    }

    /**
     * Clear the rectangle at (x, y), by marking the space occupied by it with -1
     * in the placement matrix.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rectangle the rectangle to be cleared
     */
    private void clearRectangle(int x, int y, Rectangle rectangle, int width, int height, boolean updateEmptyMatrices) {
        for (int i = x; i < x + rectangle.width; i++) {
            for (int j = y; j < y + rectangle.height; j++) {
                placementMatrix[i][j] = -1;
            }
        }

        if (updateEmptyMatrices) {
            // update empty rows
            for (int j = y; j < y + rectangle.height; j++) {
                int cellsEmpty = rectangle.width;
                int leftMostEmpty = x;
                if (x > 0 && placementMatrix[x - 1][j] == -1) {
                    cellsEmpty += emptyRowMatrix[x - 1][j];
                    leftMostEmpty -= emptyRowMatrix[x - 1][j];
                }
                if (x + rectangle.width < width && placementMatrix[x + rectangle.width][j] == -1) {
                    cellsEmpty += emptyRowMatrix[x + rectangle.width][j];
                }

                // fill up cells
                for (int i = leftMostEmpty; i < leftMostEmpty + cellsEmpty; i++) {
                    emptyRowMatrix[i][j] = cellsEmpty;
                }
            }

            // update empty columns
            for (int i = x; i < x + rectangle.width; i++) {
                int cellsEmpty = rectangle.height;
                int bottomMostEmpty = y;
                if (y > 0 && placementMatrix[i][y - 1] == -1) {
                    cellsEmpty += emptyColumnMatrix[i][y - 1];
                    bottomMostEmpty -= emptyColumnMatrix[i][y - 1];
                }
                if (y + rectangle.height < height && placementMatrix[i][y + rectangle.height] == -1) {
                    cellsEmpty += emptyColumnMatrix[i][y + rectangle.height];
                }

                // fill up cells
                for (int j = bottomMostEmpty; j < bottomMostEmpty + cellsEmpty; j++) {
                    emptyColumnMatrix[i][j] = cellsEmpty;
                }
            }
        }
    }

    private Pair<Rectangle[], Rectangle> iterativeSolution(Rectangle[] rectangles) {
        return null;
    }

    /**
     * Create a copy of the supplied array of rectangles.
     *
     * @param rectangles the supplied array of rectangles
     * @return a copy of the supplied array
     */
    private Rectangle[] copyRectangles(Rectangle[] rectangles) {
        Rectangle[] ans = new Rectangle[rectangles.length];

        for (int i = 0; i < ans.length; i++) {
            ans[i] = copyRectangle(rectangles[i]);
        }

        return ans;
    }

    /**
     * Create a copy of the given rectangle.
     *
     * @param rectangle the given rectangle
     * @return a copy of the given rectangle
     */
    private Rectangle copyRectangle(Rectangle rectangle) {
        Rectangle ret = new Rectangle();

        ret.x = rectangle.x;
        ret.y = rectangle.y;
        ret.width = rectangle.width;
        ret.height = rectangle.height;
        ret.rotated = rectangle.rotated;
        ret.index = rectangle.index;

        return ret;
    }

    private void printPlacementMatrix(int[][] matrix) {
            //(print the placement matrix)

            System.out.println();
            System.out.printf("width: %d; height: %d\n", matrix.length, matrix[0].length);
            System.out.printf("area: %d\n", (long) matrix.length * (long) matrix[0].length);
            for (int j = matrix[0].length - 1; j >= 0; j--) {
                for (int i = 0; i < matrix.length; i++) {
                    System.out.print((matrix[i][j] == -1) ? "." : matrix[i][j]);
                }
                System.out.println();
            }
            System.out.flush();
    }

    private long[] copyHistogram (long[] histogram) {
        if (histogram == null) {
            return null;
        }

        long[] copy = new long[histogram.length];
        for (int i = 0; i < histogram.length; i++) {
            copy[i] = histogram[i];
        }
        return copy;
    }

    private void printHistogram(long[] histogram) {
        System.out.print("(");
        for (int i = 0; i < histogram.length - 1; i++) {
            System.out.print(histogram[i] + ",");
        }
        System.out.println(histogram[histogram.length - 1] + ")");
    }

    private class Pair<T, U> {
        public T first;
        public U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }
}