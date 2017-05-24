import java.util.Arrays;
import java.util.Comparator;

public class OptimalRectanglePacking implements Solver {
    // array in which we save the optimal placement of rectangles
    private Rectangle[] optimalRectanglePlacement;

    private Rectangle optimalEnclosingRectangle;

    private int binWidth;               // the current width of the enclosing rectangle
    private int binHeight;              // the current height of the enclosing rectangle

    private boolean solution;           // true == anytime solution, false == iterative solution

    private int[][] placementMatrix;    // matrix in which rectangle positions are stored (TODO transform in boolean)

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        this.optimalRectanglePlacement = new Rectangle[rectangles.length];
        for (int i = 0; i < rectangles.length; i++) {
            this.optimalRectanglePlacement[i] = new Rectangle(rectangles[i]);
        }

        this.optimalEnclosingRectangle = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, -1);

        this.solution = true;

        this.binWidth = 1000;
        this.binHeight = 1000;

        this.placementMatrix = new int[this.binHeight][this.binWidth];
        for (int i = 0; i < this.binHeight; i++) {
            for (int j = 0; j < this.binWidth; j++) {
                this.placementMatrix[i][j] = -1;
            }
        }

        if (solution) anytimeSolution(rectangles);
        else iterativeSolution(rectangles);

        return optimalRectanglePlacement;
    }

    /**
     * Generate an anytime solution - i.e. generate an initial solution that keeps on improving.
     * Can be stopped at anytime to get a pretty good enclosing bin.
     *
     * @param rectangles the given array of rectangles
     * @modifies the {@code optimalRectanglePlacement} array by updating the (x, y) coordinates of the rectangles
     */
    private void anytimeSolution(Rectangle[] rectangles) {
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

        // determine the width of the widest rectangle
        int maxWidth = Integer.MIN_VALUE;
        for (int i = 0; i < rectangles.length; i++) {
            maxWidth = Math.max(maxWidth, rectangles[i].width);
        }

        // find the best bounding box
        while (binWidth > maxWidth) {
            // clear the placement matrix
            for (int i = 0; i < this.placementMatrix.length; i++) {
                for (int j = 0; j < this.placementMatrix[i].length; j++) {
                    this.placementMatrix[i][j] = -1;
                }
            }

            // determine infeasibility
            boolean infeasible = false;

            // the total area of the rectangles cannot exceed the area of the bounding rectangle
            int totalArea = 0;
            for (int i = 0; i < rectangles.length; i++) {
                totalArea += rectangles[i].width * rectangles[i].height;
            }

            infeasible |= ((binWidth * binHeight) < totalArea);

            // the bounding rectangle must be at least as wide as the widest rectangle
            infeasible |= (binWidth < maxWidth);

            // TODO also implement the third test for infeasibility

            // now call the containment algorithm and see if we can fit the rectangles in the current bin
            infeasible |= (!containmentAlgorithm(binWidth, binHeight, rectangles));

            // now check if the solution is infeasible
            if (infeasible) {
                binHeight++;
            } else {
                binWidth--;
            }
        }
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
                    if (placementMatrix[y][x] >= 0) { // skip spaces that are already occupied by other rectangles
                        x += rectangles[placementMatrix[y][x]].width - 1;
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

        // set an initial value for the enclosing rectangle width
        binWidth = width;

        // store the height and width as the best solution found so far
        this.optimalEnclosingRectangle.width = binWidth;
        this.optimalEnclosingRectangle.height = binHeight;
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

    /**
     * A function that solves the containment problem - i.e. given a list of rectangles,
     * can they be placed in an enclosing bin of given width and height ?
     *
     * @param width the width of the enclosing bin
     * @param height the height of the enclosing bin
     * @param rectangles the given list of rectangles
     * @return true if the problem can be solved; false otherwise
     */
    private boolean containmentAlgorithm(int width, int height, Rectangle[] rectangles) {
        return containmentAlgorithm(width, height, rectangles, 0);
    }

    private boolean containmentAlgorithm(int width, int height, Rectangle[] rectangles, int index) {
        if (index == rectangles.length) { // a solution of packing the rectangles into the bin has been found
            if (binWidth * binHeight < optimalEnclosingRectangle.width * optimalEnclosingRectangle.height) {
                optimalEnclosingRectangle.width = binWidth;
                optimalEnclosingRectangle.height = binHeight;

                // update the optimal solution
                for (int i = 0; i < optimalRectanglePlacement.length; i++) {
                    optimalRectanglePlacement[i].x = rectangles[i].x;
                    optimalRectanglePlacement[i].y = rectangles[i].y;
                }
            }
            return true;
        }

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (placementMatrix[y][x] >= 0) { // skip spaces that are already occupied by other rectangles
                    x += rectangles[placementMatrix[y][x]].width - 1;
                    continue;
                }

                // check if we have enough space to place it
                if (canPlaceAt(x, y, rectangles[index])) {
                    placeRectangle(x, y, rectangles[index]);
                    if (containmentAlgorithm(width, height, rectangles, index + 1)) {
                        return true;
                    }
                    clearRectangle(x, y, rectangles[index]);
                }
            }
        }

        return false;
    }

    private void iterativeSolution(Rectangle[] rectangles) {

    }

    private void print(Rectangle[] rectangles) {
        for (int i = 0; i < rectangles.length; i++) {
            System.out.println("rectangle " + rectangles[i].index +
                    " (x, y) = " + rectangles[i].x + " " + rectangles[i].y);
        }
        System.out.println();
        for (int i = 0; i < binHeight; i++) {
            for (int j = 0; j < binWidth; j++) {
                if (placementMatrix[i][j] == -1) System.out.print(".");
                else System.out.print(placementMatrix[i][j]);
            }
            System.out.println();
            System.out.flush();
        }
        System.out.println(binHeight * binWidth);
        System.out.println("bin width = " + this.binWidth);
        System.out.println("bin height = " + this.binHeight);
        System.out.println();
        System.out.flush();
    }

    public static void main(String[] args) {
        OptimalRectanglePacking opt = new OptimalRectanglePacking();
        Rectangle[] test = new Rectangle[6];
        test[0] = new Rectangle(12, 8, 0);
        test[1] = new Rectangle(10, 9, 1);
        test[2] = new Rectangle(8, 12, 2);
        test[3] = new Rectangle(16, 3, 3);
        test[4] = new Rectangle(4, 16, 4);
        test[5] = new Rectangle(10, 6, 5);
//        Rectangle[] test = new Rectangle[3];
//        test[0] = new Rectangle(4, 13, 0);
//        test[1] = new Rectangle(4, 8, 1);
//        test[2] = new Rectangle(7, 1, 2);
        Rectangle[] optimal = opt.solver(test);
        System.out.println("OPTIMAL PLACEMENT");
        for (int i = 0; i < optimal.length; i++) {
            System.out.println(optimal[i].x + " " + optimal[i].y);
        }
        System.out.println();
        opt.print(optimal);
    }
}
