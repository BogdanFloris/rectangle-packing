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
    public Node fit;

    public Rectangle() {
        this.width = 0;
        this.height = 0;
        this.index = -1;
        this.x = 0;
        this.y = 0;
        this.rotated = false;
        this.fit = null;
    }

    public Rectangle(int width, int height, int index) {
        this.width = width;
        this.height = height;
        this.index = index;
        this.rotated = false;
        this.fit = null;
    }

    public Rectangle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle(Rectangle other) {
        this.width = other.width;
        this.height = other.height;
        this.index = other.index;
        this.x = other.x;
        this.y = other.y;
        this.rotated = other.rotated;
        this.fit = other.fit;
    }

    @Override
    public String toString() {
        return width + " " + height;
    }
}
