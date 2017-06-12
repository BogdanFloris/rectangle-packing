/**
 * Class that represents a Node in the Binary Tree
 */
public class Node {

    public int x;           // the x-coordinate of the node
    public int y;           // the y-coordinate of the node
    public int width;       // the width of the node
    public int height;      // the height of the node
    public boolean used;    // whether the node is used or not

    public Node down;       // the down child
    public Node right;      // the right child

    public Node(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.used = false;
        this.down = null;
        this.right = null;
    }

    @Override
    public String toString() {
        return "Position: " + x + ", " + y + " Dimensions: " + width + " " + height;
    }
}
