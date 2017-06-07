/**
 * An improvement to the Binary Tree Bin Packing algorithm
 * that uses multiple instance of BinaryTreeBinPacking
 * with different heuristics and picks the best one of them
 */
public class BinaryTreePackingAllHeuristics implements Solver {

    private boolean rotations;      // if rotations are allowed
    private int fixedHeight;        // the fixed height

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        // computer the area made up by all the rectangles that need to be placed
        int areaRects = 0;
        for (Rectangle rectangle: rectangles) {
            areaRects = areaRects + (rectangle.width * rectangle.height);
        }

        // make copies of the rectangles array
        Rectangle[] copyWidth = new Rectangle[rectangles.length];
        Rectangle[] copyHeight = new Rectangle[rectangles.length];
        Rectangle[] copyMaxside = new Rectangle[rectangles.length];

        for (int i = 0; i < rectangles.length; i++) {
            copyWidth[i] = new Rectangle(rectangles[i].width, rectangles[i].height, rectangles[i].index);
            copyHeight[i] = new Rectangle(rectangles[i].width, rectangles[i].height, rectangles[i].index);
            copyMaxside[i] = new Rectangle(rectangles[i].width, rectangles[i].height, rectangles[i].index);
        }

        // compute the placement for WIDTH heuristic
        Solver solverWidth = new BinaryTreeBinPacking(
                this.rotations, this.fixedHeight, BinaryTreeBinPacking.SortingHeuristic.WIDTH);
        Rectangle[] placementWidth = solverWidth.solver(copyWidth);
        int wastedSpaceWidth = (((BinaryTreeBinPacking) solverWidth).getEnclosingRectangle().width *
                ((BinaryTreeBinPacking) solverWidth).getEnclosingRectangle().height) - areaRects;

        // compute the placement for HEIGHT heuristic
        Solver solverHeight = new BinaryTreeBinPacking(
                this.rotations, this.fixedHeight, BinaryTreeBinPacking.SortingHeuristic.HEIGHT);
        Rectangle[] placementHeight = solverHeight.solver(copyHeight);
        int wastedSpaceHeight = (((BinaryTreeBinPacking) solverHeight).getEnclosingRectangle().width *
                ((BinaryTreeBinPacking) solverHeight).getEnclosingRectangle().height) - areaRects;

        // compute the placement for MAXSIDE heuristic
        Solver solverMaxside = new BinaryTreeBinPacking(
                this.rotations, this.fixedHeight, BinaryTreeBinPacking.SortingHeuristic.MAXSIDE);
        Rectangle[] placementMaxside = solverMaxside.solver(copyMaxside);
        int wastedSpaceMaxside = (((BinaryTreeBinPacking) solverMaxside).getEnclosingRectangle().width *
                ((BinaryTreeBinPacking) solverMaxside).getEnclosingRectangle().height) - areaRects;

        // compute the least wasted space
        int leastWastedSpace = Math.min(wastedSpaceWidth, Math.min(wastedSpaceHeight, wastedSpaceMaxside));

        //return the best placement
        if (leastWastedSpace == wastedSpaceWidth) {
            return placementWidth;
        }
        else if (leastWastedSpace == wastedSpaceHeight) {
            return placementHeight;
        }
        else {
            return placementMaxside;
        }
    }

    public BinaryTreePackingAllHeuristics(boolean rotations, int fixedHeight) {
        this.rotations = rotations;
        this.fixedHeight = fixedHeight;
    }
}
