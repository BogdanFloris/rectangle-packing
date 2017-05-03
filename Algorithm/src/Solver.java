/**
 * This is the main interface for the solver
 * which should be instantiated every time an
 * actual solver is implemented
 */
public interface Solver {
    /**
     * The main solver method which should be overwritten
     * in order to computer the result
     *
     * @param rectangles the provided rectangles
     * @return the coordinate of each rectangles, in the order
     *         they appeared in the provided array of rectangles
     */
    Rectangle[] solver(Rectangle[] rectangles);
}
