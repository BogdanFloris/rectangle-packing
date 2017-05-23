import java.awt.*;

/**
 * Main class that represents a rectangle.
 */
public class PackingRectangle {
    //Values before placement (width and height not affected by rotation)
    public int width;
    public int height;
    public int index;
    public int area;

    private Color color;

    //Values after placement
    public int x;
    public int y;
    public boolean rotated;

    public PackingRectangle(int width, int height, int index) {
        this.width = width;
        this.height = height;
        this.index = index;
        this.rotated = false;
        this.area = width*height;

        //Generates a random color
        this.color = new Color((int) (Math.random()*180+35),(int) (Math.random()*180+35),(int) (Math.random()*180+35));
    }

    public void place(boolean rotated, int x, int y) {
        this.rotated = rotated;
        this.x = x;
        this.y = y;
    }

    public boolean overlaps(PackingRectangle placedRect){
        if(this.right() < placedRect.left()+1 ||
                placedRect.right() < this.left()+1 ||
                this.top() < placedRect.bottom()+1 ||
                placedRect.top() < this.bottom()+1){
            return false;
        }else{
            return true;
        }
    }


    @Override
    public String toString() {
        return width + " " + height;
    }

    public Color getColor(){
        return color;
    }

    public int right(){
        return x+getWidth();
    }
    public int left(){
        return x;
    }
    public int bottom(){
        return y;
    }
    public int top(){
        return y+getHeight();
    }

    //Returns the width after optional rotation
    public int getWidth(){
        return rotated? height: width;
    }

    //Returns the height after optional rotation
    public int getHeight(){
        return rotated? width: height;
    }



    public void setColor(Color color) {
        this.color = color;
    }
}
