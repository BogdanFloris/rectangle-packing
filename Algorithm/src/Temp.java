import java.util.Arrays;
import java.util.Comparator;

/**
 * Currently only supports oriented rectangles.
 */

public class Temp implements Solver {
    private Rectangle[] rectangles;
    private int binWidth;
    private int binHeight;
    private int[][] bin;

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        this.rectangles = new Rectangle[rectangles.length];
        for (int i = 0; i < rectangles.length; i++) {
            this.rectangles[i] = new Rectangle(rectangles[i]);
        }

//        // sort (oriented) rectangles in decreasing order of height
//        Arrays.sort(this.rectangles, new Comparator<Rectangle>() {
//            @Override
//            public int compare(Rectangle o1, Rectangle o2) {
//                return o2.height - o1.height;
//            }
//        });
//
//        binHeight = this.rectangles[0].height;
//
//        // greedily place the rectangles in the leftmost and lowest position possible
//        // to determine an initial bin width
//        for (Rectangle rect : this.rectangles) {
//
//        }
        binWidth = 10; binHeight = 10;
        bin = new int[binWidth][binHeight];
        for (int i = 0; i < binWidth; i++) {
            for (int j = 0; j < binHeight; j++) {
                bin[i][j] = 0;
            }
        }

        backtracking(0);

        System.out.println(count);
        System.out.flush();

        return new Rectangle[0];
    }

    private int count = 0;

    private void backtracking(int index) {
        if (index == rectangles.length) {
            return;
        }

        for (int x = 0; x < binWidth; x++) {
            for (int y = 0; y < binHeight; y++) {
                if (bin[x][y] > 0) {
                    y += rectangles[bin[x][y] - 1].width - 1;
                    continue;
                }
                if (canPlaceAt(x, y, rectangles[index])) {
                    placeRectangle(x, y, rectangles[index]);
                    backtracking(index + 1);
                    unplaceRectangle(x, y, rectangles[index]);
                }
            }
        }
    }

    private void placeRectangle(int x, int y, Rectangle rectangle) {
        for (int i = x; i < x + rectangle.width; i++) {
            for (int j = y; j < y + rectangle.height; j++) {
                bin[i][j] = rectangle.index + 1;
            }
        }
    }

    private void unplaceRectangle(int x, int y, Rectangle rectangle) {
        for (int i = x; i < x + rectangle.width; i++) {
            for (int j = y; j < y + rectangle.height; j++) {
                bin[i][j] = 0;
            }
        }
    }

    private void print() {
        for (int i = 0; i < binWidth; i++) {
            for (int j = 0; j < binHeight; j++) {
                System.out.print(bin[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        System.out.flush();
    }

    private boolean canPlaceAt(int x, int y, Rectangle rectangle) {
        if (x + rectangle.width - 1 >= binWidth) return false;
        if (y + rectangle.height - 1 >= binHeight) return false;

        for (int i = x; i < x + rectangle.width; i++) {
            if (bin[i][y] > 0 || bin[i][y + rectangle.height - 1] > 0) return false;
        }

        for (int j = y; j < y + rectangle.height; j++) {
            if (bin[x][j] > 0 || bin[x + rectangle.width - 1][j] > 0) return false;
        }

        return true;
    }

    public static void main(String[] args) {
        OptimalRectanglePacking opt = new OptimalRectanglePacking();
        Rectangle[] test = new Rectangle[3];
        test[0] = new Rectangle(6, 6, 0);
        test[1] = new Rectangle(2, 2, 1);
        test[2] = new Rectangle(2, 2, 2);
        opt.solver(test);
    }
}