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

        // placement of the rectangles
        Rectangle[] placement = new Rectangle[rectangles.length];

        // initial height and width of the enclosing rectangle(container)
        int containerHeight = rectangles[0].height; // we start of with the height of the tallest rectangle
        // the width is the sum of all the widths
        int containerWidth = 0;
        for (int i = 0; i < rectangles.length; i++) {
            containerWidth += rectangles[i].width;
        }

        // the best area of the enclosing rectangle found so far
        int bestArea = Integer.MAX_VALUE;

        // the offsets
        int x_offset = 0;
        int y_offset = 0; //the amount of free space left until hitting the max height

        for (int i = 0; i < rectangles.length; i++) {

        }
        return rectangles;
    }
}
