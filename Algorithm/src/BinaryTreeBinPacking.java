import java.util.Arrays;
import java.util.Comparator;

/**
 *  Implementation of the Binary Tree Bin Packing Algorithm
 *  based on this article: http://codeincomplete.com/posts/bin-packing/ by Jack Gordon
 *
 *  This is a very simple packing algorithm best used for a really high number of rectangles
 *  since it is a really fast algorithm.
 *
 *  The algorithm will try to fit each rectangle into the first node that it fits,
 *  then split that node into two parts(down and right) in order to track the remaining free space.
 *
 *  We start off with the width and height of the first rectangle and then start
 *  to grow the enclosing rectangle as necessary to accommodate each subsequent rectangle.
 *
 *  When growing, the algorithm can only accommodate to the right OR down. If a new rectangle
 *  is both wider and taller then the enclosing rectangle, then we cannot accommodate it.
 *  This problem is solved by sorting the input first(in decreasing order).
 *
 *  Best results occur when input is sorted by height, and even better when sorted by max(width, height)
 */
public class BinaryTreeBinPacking implements Solver {

    private Node root;          // the root of the binary tree

    public enum SortingHeuristic {
        WIDTH,      // sort by descending width
        HEIGHT,     // sort by descending height
        MAXSIDE,    // sort by the longer side first, then by the shorter side, descending
        AREA,       // sort by descending area
        NONE,       // don't sort
    }

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        // sort the rectangles
        sort(rectangles, SortingHeuristic.HEIGHT);

        // initialize the root with the width and the height of the first rectangle
        init(rectangles[0].width, rectangles[0].height);

        // arrange the rectangles
        for (int i = 0; i < rectangles.length; i++) {
            Node node = findNode(this.root, rectangles[i].width, rectangles[i].height);
            if (node != null) {
                rectangles[i].fit = splitNode(node, rectangles[i].width, rectangles[i].height);
            }
            else {
                rectangles[i].fit = growNode(rectangles[i].width, rectangles[i].height);
            }
        }

        Rectangle[] placement = new Rectangle[rectangles.length];
        for (int i = 0; i < rectangles.length; i++) {
            placement[rectangles[i].index] = new Rectangle(rectangles[i].fit.x, rectangles[i].fit.y);
        }

        return placement;
    }

    private void init(int width, int height) {
        root = new Node(0, 0, width, height);
    }

    /**
     * Searches for a node in the binary tree where we can place the rectangle with the given dimensions
     *
     * @param root the root of the binary tree
     * @param width of the rectangle
     * @param height of the rectangle
     * @return a node where the rectangle can be placed
     */
    private Node findNode(Node root, int width, int height) {
        if (root.used) {
            Node node = findNode(root.right, width, height);
            if (node != null) {
                return node;
            }
            else {
                return findNode(root.down, width, height);
            }
        }
        else if (width <= root.width && height <= root.height) {
            return root;
        }
        else {
            return null;
        }
    }

    /**
     * Splits the current node and assigns it to the rectangle it belongs to
     *
     * @param node the node that is split
     * @param width of the rectangle
     * @param height of the rectangle
     * @return the node to be assigned to the rectangle
     */
    private Node splitNode(Node node, int width, int height) {
        node.used = true;
        node.down = new Node(node.x, node.y + height, node.width, node.height - height);
        node.right = new Node(node.x + width, node.y, node.width - width, height);
        return node;
    }

    /**
     * Grows the root node either down or right depending on which one makes the area smaller
     *
     * @param width with which to grow
     * @param height with which to grow
     * @return the node where the rectangle is placed
     */
    private Node growNode(int width, int height) {
        boolean canGrowDown = (width <= this.root.width);
        boolean canGrowRight = (height <= this.root.height);

        // attempt to keep square-ish by growing right when height is much greater than width
        boolean shouldGrowRight = canGrowRight && (this.root.height >= (this.root.width + width));
        // attempt to keep square-ish by growing down  when width  is much greater than height
        boolean shouldGrowDown = canGrowDown && (this.root.width >= (this.root.height + height));

        if (shouldGrowRight) {
            return growRight(width, height);
        }
        else if (shouldGrowDown) {
            return growDown(width, height);
        }
        else if (canGrowRight) {
            return growRight(width, height);
        }
        else if (canGrowDown) {
            return growDown(width, height);
        }
        else {
            System.err.println("Invalid enclosing");
            return null; // this doesn't happen if input is sorted in descending order
        }
    }

    /**
     * Auxiliary function for growNode in order to grow right
     */
    private Node growRight(int width, int height) {
        Node auxNode = this.root;
        this.root = new Node(0,0, auxNode.width + width, auxNode.height);
        this.root.used = true;
        this.root.down = auxNode;
        this.root.right = new Node(auxNode.width, 0, width, auxNode.height);

        Node node = findNode(this.root, width, height);
        if (node != null) {
            return splitNode(node, width, height);
        }
        else {
            return null;
        }
    }

    /**
     * Auxiliary function for growNode in order to grow down
     */
    private Node growDown(int width, int height) {
        Node auxNode = this.root;
        this.root = new Node(0, 0, auxNode.width, auxNode.height + height);
        this.root.used = true;
        this.root.down = new Node(0, auxNode.height, auxNode.width, height);
        this.root.right = auxNode;

        Node node = findNode(this.root, width, height);
        if (node != null) {
            return splitNode(node, width, height);
        }
        else {
            return null;
        }
    }

    /**
     * Sorts the given array according to the given heuristic
     *
     * @param rectangles the array of rectangles
     * @param heuristic sort according to this heuristic
     */
    private void sort(Rectangle[] rectangles, SortingHeuristic heuristic) {
        Arrays.sort(rectangles, new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                int longSideOne = Math.max(o1.width, o1.height);
                int longSideTwo = Math.max(o2.width, o2.height);
                switch (heuristic) {
                    case HEIGHT:
                        return o2.height - o1.height;
                    case WIDTH:
                        return o2.width - o1.width;
                    case AREA:
                        int area1 = o1.width * o1.height;
                        int area2 = o2.width * o1.height;
                        return area2 - area1;
                    case MAXSIDE:
                        return longSideTwo - longSideOne;
                }
                return 0;
            }
        });
    }
}

class Node {

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
