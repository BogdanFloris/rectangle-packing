import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class OptimalRectanglePacking implements Solver {
    private final static int PLACEMENT_MATRIX_SIZE = 1234;

    private boolean solution;                       // true if anytime; false if iterative
    private boolean rotationsAllowed;

    private int fixedHeight;                        // 0 if height is free; value of the fixed height otherwise

    private int[][] placementMatrix;                // matrix that holds the positions of rectangles

    private HashMap<Integer, Integer> mapWidth;     // map each rectangle to its width using its index as the key

    public OptimalRectanglePacking() {
        this.solution = true;
    }

    public OptimalRectanglePacking(boolean rotations, int height) {
        this.solution = true;
        this.rotationsAllowed = rotations;
        this.fixedHeight = height;
    }

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        if (solution) {
            if (rotationsAllowed) {
                int n = rectangles.length;

                Rectangle optimalBinRotations = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, -1);
                Rectangle[] optimalSolutionRotations = null;

                for (int combination = 0; combination < (1 << n); ++combination) {
                    Rectangle[] arr = copyRectangles(rectangles);

                    for (int bit = 0; bit < n; ++bit) {
                        if ((combination & (1 << bit)) > 0) {
                            arr[bit].rotate();
                        }
                    }

                    Pair<Rectangle[], Rectangle> solution = anytimeSolution(arr);

                    // check if this rotation combination is better
                    if ((long) optimalBinRotations.width * (long) optimalBinRotations.height >
                            (long) solution.second.width * (long) solution.second.height) {
                        optimalSolutionRotations = copyRectangles(solution.first);

                        optimalBinRotations.width = solution.second.width;
                        optimalBinRotations.height = solution.second.height;
                    }
                }

                return optimalSolutionRotations;
            } else {
                return anytimeSolution(rectangles).first;
            }
        } else {
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
        Rectangle optimalBin;
        Rectangle[] optimalPlacement;

        // the argument must not be modified
        Rectangle[] arr;

        // sort the rectangles by height
        Rectangle[] temp = copyRectangles(rectangles);
        Arrays.sort(temp, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return o2.height - o1.height;
            }
        });

        // greedily place the rectangles to determine an initial width and an initial height for the enclosing bin
        placementMatrix = new int[PLACEMENT_MATRIX_SIZE][PLACEMENT_MATRIX_SIZE];
        for (int i = 0; i < PLACEMENT_MATRIX_SIZE; i++) {
            for (int j = 0; j < PLACEMENT_MATRIX_SIZE; j++) {
                placementMatrix[i][j] = -1;
            }
        }

        int width = PLACEMENT_MATRIX_SIZE;
        int height = temp[0].height;            // height of the tallest rectangle

        mapWidth = new HashMap<>();

        // map width to specific rectangle indices (used to not traverse the whole placement matrix when placing
        // a rectangle).
        for (int i = 0; i < temp.length; i++) {
            mapWidth.put(temp[i].index, temp[i].width);
        }

        for (int i = 0; i < temp.length; i++) {
            int bestX = Integer.MAX_VALUE;
            int bestY = Integer.MIN_VALUE;

            for (int y = height - 1; y >= 0; y--) {
                for (int x = 0; x < width; x++) {
                    if (placementMatrix[y][x] >= 0) { // skip spaces that are already occupied by other rectangles
                        x += mapWidth.get(placementMatrix[y][x]) - 1;
                        continue;
                    }
                    // check if we have enough space to place it
                    if (canPlaceAt(x, y, temp[i], width, height)) {
                        // update the possible placement of the current rectangle
                        if (x < bestX || (x == bestX && y > bestY)) {
                            bestX = x;
                            bestY = y;
                        }
                    }
                }
            }

            // place the current rectangle in the leftmost, lowest position possible
            placeRectangle(bestX, bestY, temp[i], width, height);
        }

        // determine the width of the current enclosing bin
        int tempWidth = Integer.MIN_VALUE;
        for (Rectangle rectangle : temp) {
            tempWidth = Math.max(tempWidth, rectangle.x + rectangle.width);
        }
        width = tempWidth;

        // reset the indices and setup the array on which we will work on
        arr = new Rectangle[temp.length];
        for (int i = 0; i < temp.length; i++) {
            arr[temp[i].index] = copyRectangle(temp[i]);
        }

        // set an initial value for the optimal bin
        optimalBin = new Rectangle(width, height, -1);

        // save the greedy placement as an initial placement of the optimal solution (to be improved further)
        optimalPlacement = copyRectangles(arr);

        // determine when to stop shrinking the rectangle
        int stop = Integer.MIN_VALUE;
        for (Rectangle rectangle : arr) {
            stop = Math.max(stop, rectangle.width);
        }

        while (width >= stop) {
            // clear the placement matrix
            for (int i = 0; i < PLACEMENT_MATRIX_SIZE; i++) {
                for (int j = 0; j < PLACEMENT_MATRIX_SIZE; j++) {
                    this.placementMatrix[i][j] = -1;
                }
            }

            // DEBUG
            System.out.printf("considering: (w: %d; h: %d)\n", width, height);
            System.out.flush();

            // determine infeasibility
            boolean infeasible = false;

            // the total area of the rectangles cannot exceed the area of the bounding rectangle
            int totalArea = 0;
            for (Rectangle rectangle : arr) {
                totalArea += rectangle.width * rectangle.height;
            }

            infeasible |= ((width * height) < totalArea);

            // now call the containment algorithm and see if we can fit the rectangles in the current bin
            infeasible |= (!containmentAlgorithm(width, height, arr, 0));

            if (infeasible) {
                height++;

                // if the area is already bigger than the currently best area
                // don't increase the height any further (TODO verify that this is indeed correct)
                if ((long) optimalBin.width * (long) optimalBin.height <
                        (long) width * (long) height) {
                    width--;
                }
            } else {
                // solution is feasible - save it if it's better than the last solution
                if ((long) optimalBin.width * (long) optimalBin.height >
                        (long) width * (long) height) {
                    optimalBin.width = width;
                    optimalBin.height = height;

                    optimalPlacement = copyRectangles(arr);
                }

                // DEBUG
                System.out.printf("width: %d; height: %d\n", width, height);
                System.out.printf("area: %d\n", width * height);
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        System.out.print((placementMatrix[i][j] == -1) ? "." : placementMatrix[i][j]);
                    }
                    System.out.println();
                }
                System.out.println();
                System.out.flush();

                width--;
            }
        }

        System.out.printf("optimal width: %d; optimal height: %d\n", optimalBin.width, optimalBin.height);
        System.out.printf("optimal area: %d\n", optimalBin.width * optimalBin.height);
        System.out.flush();

        return new Pair<>(optimalPlacement, optimalBin);
    }

    /**
     * A function that solves the containment problem - i.e. given a list of rectangles,
     * can they be placed in an enclosing bin of given width and height ?
     *
     * @param width the width of the enclosing bin
     * @param height the height of the enclosing bin
     * @param rectangles the given list of rectangles
     * @return true if the problem can be solved; false otherwise
     */
    private boolean containmentAlgorithm(int width, int height, Rectangle[] rectangles, int index) {
        if (index == rectangles.length) { // a solution of packing the rectangles into the bin has been found
            return true;
        }

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (placementMatrix[y][x] >= 0) { // skip spaces that are already occupied by other rectangles
                    x += mapWidth.get(placementMatrix[y][x]) - 1;
                    continue;
                }

                // check if we have enough space to place it
                if (canPlaceAt(x, y, rectangles[index], width, height)) {
                    placeRectangle(x, y, rectangles[index], width, height);
                    if (containmentAlgorithm(width, height, rectangles, index + 1)) {
                        return true;
                    }
                    clearRectangle(x, y, rectangles[index]);
                }
            }
        }

        return false;
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
        if (y + rectangle.height - 1 >= binHeight ||
                x + rectangle.width - 1 >= binWidth) {
            return false;
        }

        for (int i = y; i < y + rectangle.height; i++) {
            if (placementMatrix[i][x] >= 0 || placementMatrix[i][x + rectangle.width - 1] >= 0) {
                return false;
            }
        }

        for (int j = x; j < x + rectangle.width; j++) {
            if (placementMatrix[y][j] >= 0 || placementMatrix[y + rectangle.height - 1][j] >= 0) {
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
     * @param binWidth the width of the current enclosing bin
     * @param binHeight the height of the current enclosing bin
     */
    private void placeRectangle(int x, int y, Rectangle rectangle, int binWidth, int binHeight) {
        for (int i = y; i < y + rectangle.height; i++) {
            for (int j = x; j < x + rectangle.width; j++) {
                placementMatrix[i][j] = rectangle.index;
            }
        }

        // array x coordinate = euclidean x coordinate
        rectangle.x = x;

        // array y coordinate = flipped euclidean y coordinate => transform it to euclidean
        // the array y coordinate starts from the top, not the bottom
        rectangle.y = binHeight - (y + rectangle.height);
    }

    /**
     * Clear the rectangle at (x, y), by marking the space occupied by it with -1
     * in the placement matrix.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rectangle the rectangle to be cleared
     */
    private void clearRectangle(int x, int y, Rectangle rectangle) {
        for (int i = y; i < y + rectangle.height; i++) {
            for (int j = x; j < x + rectangle.width; j++) {
                placementMatrix[i][j] = -1;
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
            ans[i] = new Rectangle();
            ans[i].x = rectangles[i].x;
            ans[i].y = rectangles[i].y;
            ans[i].width = rectangles[i].width;
            ans[i].height = rectangles[i].height;
            ans[i].rotated = rectangles[i].rotated;
            ans[i].index = rectangles[i].index;
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

    private class Pair<T, U> {
        public T first;
        public U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }
}