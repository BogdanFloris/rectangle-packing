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
    private boolean anytimeSolution;

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        this.rectangles = new Rectangle[rectangles.length];
        for (int i = 0; i < this.rectangles.length; i++) {
            this.rectangles[i] = new Rectangle(rectangles[i]);
        }

        init();
        initialGreedyPlacement(this.rectangles);

        return this.rectangles;
    }

    /**
     * Generate an anytime solution - i.e. generate an initial solution that keeps on improving.
     * Can be stopped at anytime to get a pretty good enclosing bin.
     *
     * @param rectangles the given array of rectangles
     * @return the list of rectangles with their (x, y) coordinates updated.
     */
    private Rectangle[] generateAnytimeSolution(Rectangle[] rectangles) {
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
     * Initialization function.
     */
    private void init() {
        this.enclosingBin = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, -1);
        this.binWidth = Integer.MAX_VALUE;
        this.binHeight = Integer.MAX_VALUE;

        // we can either generate a rectangle through an anytime solution (true) or through
        // an iterative solution (false)
        this.anytimeSolution = true;
    }

    /**
     * Initially, rectangles are greedily placed in an enclosing rectangle
     * with height equal to the tallest rectangle in order to determine an initial width.
     *
     * @param rectangles the list of rectangles
     */
    private void initialGreedyPlacement(Rectangle[] rectangles) {

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

    public static void main(String[] args) {
        OptimalRectanglePacking test = new OptimalRectanglePacking();
    }
}