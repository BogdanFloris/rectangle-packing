import java.util.Arrays;
import java.util.Comparator;

public class BasicSolver implements Solver {

    /**
     * The basic brute-force algorithm for small inputs
     * (to be proven that it is optimal)
     *
     * @param rectangles the provided rectangles
     * @return the coordinate of each rectangles, in the order
     *         they appeared in the provided array of rectangles
     */
    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        // sort rectangles in descending order of the height
        Arrays.sort(rectangles, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return o2.height - o1.height;
            }
        });

        // place each rectangle accordingly
        Rectangle[] placement = new Rectangle[rectangles.length];

        // initial height and width of the enclosing rectangle(container)
        int containerHeight = rectangles[0].height;
        int containerWidth = Integer.MAX_VALUE;

        // the best area of the enclosing rectangle found so far
        int bestArea = Integer.MAX_VALUE;
        return placement;
    }
}
