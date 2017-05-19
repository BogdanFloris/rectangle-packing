import java.util.Arrays;
import java.util.Comparator;

/**
 * An optimal packing algorithm that can either produce a pretty good approximation of an enclosing bin,
 * or an optimal one (given enough time).
 *
 * Based on the research paper: Optimal Rectangle Packing by Richard M. Korf
 *
 * @author Sergiu Marin
 *
 * TODO also deal with rotations and fixed bin height
 */
public class OptimalRectanglePacking implements Solver {
    private Rectangle enclosingBin;                         // the optimal enclosing rectangle
    private int binWidth;                                   // the width of the current enclosing rectangle
    private int binHeight;                                  // the height of the current enclosing rectangle
    private Rectangle[] rectangles;                         // the (global) list of rectangles
    private boolean anytimeSolution;                        // wheter to generate an anytime or iterative solution
    private int fixedHeight;                                // whether the height of the enclosing bin is fixed or not
    private boolean rotationsAllowed;                       // whether rotations are allowed or not
    private int[][] placement;                              // matrix in which rectangle positions are stored

    public OptimalRectanglePacking() {}

    public OptimalRectanglePacking(boolean anytimeSolution) {
        this.anytimeSolution = anytimeSolution;
    }

    public OptimalRectanglePacking(int fixedHeight, boolean rotationsAllowed) {
        this.fixedHeight = fixedHeight;
        this.rotationsAllowed = rotationsAllowed;
    }

    public OptimalRectanglePacking(boolean anytimeSolution, int fixedHeight, boolean rotationsAllowed) {
        this.anytimeSolution = anytimeSolution;
        this.fixedHeight = fixedHeight;
        this.rotationsAllowed = rotationsAllowed;
    }

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        this.rectangles = new Rectangle[rectangles.length];
        for (int i = 0; i < this.rectangles.length; i++) {
            this.rectangles[i] = new Rectangle(rectangles[i]);
        }

        init();

        return anytimeSolution ?
                generateAnytimeSolution(this.rectangles) :
                generateIterativeSolution(this.rectangles);
    }

    /**
     * Initialization function.
     */
    private void init() {
        this.enclosingBin = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, -1);

        // we can either generate a rectangle through an anytime solution (true) or through
        // an iterative solution (false)
        this.anytimeSolution = true;

        this.placement = new int[1000][1000];
        for (int i = 0; i < this.placement.length; i++) {
            for (int j = 0; j < this.placement[i].length; j++) {
                this.placement[i][j] = -1; // no rectangles placed initially
            }
        }

        this.binWidth = this.placement[0].length;
        this.binHeight = this.placement.length;
    }

    /**
     * Generate an anytime solution - i.e. generate an initial solution that keeps on improving.
     * Can be stopped at anytime to get a pretty good enclosing bin.
     *
     * @param rectangles the given array of rectangles
     * @return the list of rectangles with their (x, y) coordinates updated.
     */
    private Rectangle[] generateAnytimeSolution(Rectangle[] rectangles) {
        // sort the rectangles by height (descending)
        Arrays.sort(rectangles, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return o2.height - o1.height;
            }
        });

        // set the initial height of the enclosing bin
        this.binHeight = rectangles[0].height;

        // greedily place the rectangles in order to determine an initial width for the enclosing bin
        initialGreedyPlacement(rectangles);

        // determine the maximum width of the set of rectangles
        int maxWidth = Integer.MIN_VALUE;
        for (int i = 0; i < rectangles.length; i++) {
            maxWidth = Math.max(maxWidth, rectangles[i].width);
        }

        int counter = 1;
        // find the best bounding box
        while (binWidth > maxWidth) {
            // clear the placement matrix
            for (int i = 0; i < this.placement.length; i++) {
                for (int j = 0; j < this.placement[i].length; j++) {
                    this.placement[i][j] = -1;
                }
            }

            // if feasibility tests passed
            if (!isFeasible(binWidth, binHeight, rectangles)) {
                binHeight++;
            } else {
                binWidth--;
            }
        }

        return new Rectangle[0];
    }

    /**
     * Generate a first optimal solution.
     *
     * @param rectangles the given array of rectangles
     * @return the list of rectangles with their (x, y) coordinates updated, placed in an optimal packing rectangle
     */
    private Rectangle[] generateIterativeSolution(Rectangle[] rectangles) {
        return new Rectangle[0];
    }

    /**
     * Initially, rectangles are greedily placed in an enclosing rectangle
     * with height equal to the tallest rectangle in order to determine an initial width.
     * The rectangles are placed in the leftmost and lowest position possible.
     *
     * @param rectangles the list of rectangles
     */
    private void initialGreedyPlacement(Rectangle[] rectangles) {
        int bestX, bestY; // best coordinates to place the rectangle (leftmost and lowest)

        for (int i = 0; i < rectangles.length; i++) {
            bestX = Integer.MAX_VALUE;
            bestY = Integer.MIN_VALUE;
            for (int y = binHeight - 1; y >= 0; y--) {
                for (int x = 0; x < binWidth; x++) {
                    if (placement[y][x] >= 0) { // skip spaces that are already occupied by other rectangles
                        x += rectangles[placement[y][x]].width - 1;
                        continue;
                    }
                    // check if we have enough space to place it
                    if (canPlaceAt(x, y, rectangles[i])) {
                        // update the possible placement of the current rectangle
                        if (x < bestX || (x == bestX && y > bestY)) {
                            bestX = x;
                            bestY = y;
                        }
                    }
                }
            }
            // place the current rectangle in the leftmost, lowest position possible
            placeRectangle(bestX, bestY, rectangles[i]);
        }

        // after greedily placing all the rectangles, get the enclosing bin width
        int width = Integer.MIN_VALUE;
        for (int i = 0; i < rectangles.length; i++) {
            width = Math.max(width, rectangles[i].x + rectangles[i].width);
        }

        binWidth = width;

        // store the height and widht as the best solution found so far
        this.enclosingBin.width = binWidth;
        this.enclosingBin.height = binHeight;
    }

    /**
     * Check if we can place a rectangle at (x, y).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rectangle the rectangle to be placed
     * @return true if the rectangle can be placed at (x, y); false otherwise
     */
    private boolean canPlaceAt(int x, int y, Rectangle rectangle) {
        if (y + rectangle.height - 1 >= binHeight ||
                x + rectangle.width - 1 >= binWidth) {
            return false;
        }

        for (int i = y; i < y + rectangle.height; i++) {
            if (placement[i][x] >= 0 || placement[i][x + rectangle.width - 1] >= 0) {
                return false;
            }
        }

        for (int j = x; j < x + rectangle.width; j++) {
            if (placement[y][j] >= 0 || placement[y + rectangle.height - 1][j] >= 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Place the rectangle at (x, y), by marking the space occupied by it with its index
     * in the placement matrix.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rectangle the rectangle to be placed
     */
    private void placeRectangle(int x, int y, Rectangle rectangle) {
        for (int i = y; i < y + rectangle.height; i++) {
            for (int j = x; j < x + rectangle.width; j++) {
                placement[i][j] = rectangle.index;
            }
        }

        rectangle.x = x;
        rectangle.y = y;
    }

    /**
     * Unplace the rectangle at (x, y), by marking the space occupied by it with -1
     * in the placement matrix.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param rectangle the rectangle to be unplaced
     */
    private void unplaceRectangle(int x, int y, Rectangle rectangle) {
        for (int i = y; i < y + rectangle.height; i++) {
            for (int j = x; j < x + rectangle.width; j++) {
                placement[i][j] = 0;
            }
        }
    }

    /**
     * Determine if a bounding box of a certain width and height constitutes a feasible solution for an array of
     * rectangles by calling the containment algorithm that checks whether it is possible to place the given rectangles
     * in a bounding box of fixed dimensions.
     *
     * To determine feasibility - call the containment algorithm.
     * To determine infeasibility - the 3 tests are explained inside the function.
     *
     * If all the solution passes all infeasibility tests, the containment algorithm is called.
     *
     * @param width the width of the bounding box
     * @param height the height of the bounding box
     * @param rectangles the given list of rectangles
     * @return true if the bounding box constitutes a feasible solution; false otherwise
     */
    private boolean isFeasible(int width, int height, Rectangle[] rectangles) {
        return isFeasible(width, height, rectangles, 0);
    }

    private boolean isFeasible(int width, int height, Rectangle[] rectangles, int index) {
        if (index == rectangles.length) {
            print();
            return true;
        }

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (placement[y][x] >= 0) { // skip spaces that are already occupied by other rectangles
                    x += rectangles[placement[y][x]].width - 1;
                    continue;
                }
                // check if we have enough space to place it
                if (canPlaceAt(x, y, rectangles[index])) {
                    placeRectangle(x, y, rectangles[index]);
                    if (isFeasible(width, height, rectangles, index + 1)) {
                        return true;
                    }
                    unplaceRectangle(x, y, rectangles[index]);
                }
            }
        }

        return false;
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
    public boolean containmentProblem(int width, int height, Rectangle[] rectangles) {
        return false;
    }

    public Rectangle getEnclosingBin() { return enclosingBin; }

    /* ------------------------------------------------- DEBUGGING ------------------------------------------------- */

    private void print() {
//        for (int i = 0; i < rectangles.length; i++) {
//            System.out.println("rectangle " + rectangles[i].index +
//                    " (x, y) = " + rectangles[i].x + " " + rectangles[i].y);
//        }
        for (int i = 0; i < binHeight; i++) {
            for (int j = 0; j < binWidth; j++) {
                if (placement[i][j] == -1) System.out.print(".");
                else System.out.print(placement[i][j]);
            }
            System.out.println();
            System.out.flush();
        }
        System.out.println();
//        System.out.println("bin width = " + this.binWidth);
//        System.out.println("bin height = " + this.binHeight);
        System.out.flush();
    }

    public static void main(String[] args) {
        OptimalRectanglePacking opt = new OptimalRectanglePacking();
        Rectangle[] test = new Rectangle[3];
        test[0] = new Rectangle(6, 6, 0);
        test[1] = new Rectangle(4, 4, 1);
        test[2] = new Rectangle(2, 2, 2);
        opt.solver(test);
    }
}