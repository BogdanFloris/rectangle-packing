import java.util.Arrays;
import java.util.Comparator;

public class SimpleSolver implements Solver{

    /**
     * Simple solver that places all the rectangles one one line,
     * starting from the biggest one and ending with the smallest one
     * (area-wise). Note that no rotations are done.
     *
     * @param rectangles the provided rectangles
     * @return the coordinate of each rectangles, in the order
     *          they appeared in the provided array of rectangles
     */
    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        // sort rectangles in descending order
        Arrays.sort(rectangles, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                int area1 = o1.width * o1.height;
                int area2 = o2.width * o2.height;
                return area2 - area1;
            }
        });

        // place each rectangle accordingly
        Rectangle[] placement = new Rectangle[rectangles.length];

        int offset = 0; // x-offset of the rectangle (y will always be 0)

        for (int i = 0; i < rectangles.length; i++) {
            placement[rectangles[i].index] = new Rectangle(offset, 0);
            offset += rectangles[i].width;
        }

        return placement;
    }
}
