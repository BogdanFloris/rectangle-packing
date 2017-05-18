import java.awt.*;

/**
 * Main class that represents a rectangle.
 */
public class PackingRectangle {
    public int width;
    public int height;
    public int index;
    public int x;
    public int y;
    public boolean rotated;
    public int area;
    private Color color;

    public PackingRectangle(int width, int height, int index) {
        this.width = width;
        this.height = height;
        this.index = index;
        this.rotated = false;
        this.area = width*height;
        this.color = new Color((int) (Math.random()*180+35),(int) (Math.random()*180+35),(int) (Math.random()*180+35));
    }

    public void place(boolean rotated, int x, int y) {
        this.rotated = rotated;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return width + " " + height;
    }

    public Color getColor(){
        return color;
    }

    public int right(){
        return x+width;
    }
    public int left(){
        return x;
    }
    public int bottom(){
        return y;
    }
    public int top(){
        return y+height;
    }
}
