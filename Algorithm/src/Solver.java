import java.util.Arrays;
import java.util.Comparator;

public class Solver {

    /**
     * Simple solver that places all the rectangles one one line,
     * starting from the biggest one and ending with the smallest one
     * (area-wise).
     *
     * @param rectangles the provided rectangles
     * @return the coordinate of each rectangles, in the order
     *          they appeared in the provided array of rectangles
     */
    public static Point[] simpleSolver(Rectangle[] rectangles) {
        // sort rectangles in descending order
        Arrays.sort(rectangles, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                int area1 = o1.width * o1.getHeight();
                int area2 = o2.width * o2.getHeight();
                return area2 - area1;
            }
        });

        // place each rectangle accordingly
        Point[] placement = new Point[rectangles.length];

        int offset = 0; // x-offset of the rectangle (y will always be 0)

        for (int i = 0; i < rectangles.length; i++) {
            placement[rectangles[i].index] = new Point(offset, 0);
            offset += rectangles[i].width;
        }

        return placement;
    }
}
