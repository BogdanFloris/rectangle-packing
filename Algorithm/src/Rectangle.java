/**
 * Main class that represents a rectangle.
 */
public class Rectangle {
    public int width;
    public int height;
    public int index;

    public Rectangle(int width, int height, int index) {
        this.width = width;
        this.height = height;
        this.index = index;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() { return height; }

    @Override
    public String toString() {
        return width + " " + height;
    }
}
