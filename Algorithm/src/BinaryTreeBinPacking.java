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

    public enum SotingHeuristic {
        WIDTH,      // sort by descending width
        HEIGHT,     // sort by descending height
        MAXSIDE,    // sort by the longer side first, then by the shorter side, descending
        AREA,       // sort by descending area
        NONE,       // don't sort
        RANDOM,     // choose one of the three sorts
    }

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        return new Rectangle[0];
    }
}
