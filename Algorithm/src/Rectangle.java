/**
 * Main class that represents a rectangle.
 */
public class Rectangle {
    public int width;
    public int height;
    public int index;
    public int x;
    public int y;
    public boolean rotated;

    public Rectangle(int width, int height, int index) {
        this.width = width;
        this.height = height;
        this.index = index;
        this.rotated = false;
    }

    public Rectangle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return width + " " + height;
    }
}
