import java.text.DecimalFormat;

public class Tester {

    private static int CONSEQUTIVE_SQUARE = 1;
    private static int OR_EQU_PERIMITER = 2;
    private static int OR_DOUBLE_PERIMITER = 3;
    private static int UNOR_DOUBLE_PERIMITER = 4;

    private static int OPTIMAL = 1;
    private static int MAX_RECT = 2;
    private static int BIN_TREE = 3;

    public void test(int benchmark, int solver, int numRects) {

        int height = 0;
        boolean rotations = false;
        Rectangle[] rectangles = new Rectangle[numRects];


        // create test case
        if (benchmark == CONSEQUTIVE_SQUARE) {

            for (int i = 0; i < numRects; i++) {
                rectangles[i] = new Rectangle(i+1, i+1, i);
            }

        } else if (benchmark == OR_EQU_PERIMITER) {

            for (int i = 0; i < numRects; i++) {
                rectangles[i] = new Rectangle(i+1, numRects - i, i);
            }

        } else if (benchmark == OR_DOUBLE_PERIMITER) {

            for (int i = 0; i < numRects; i++) {
                rectangles[i] = new Rectangle(i+1, 2*numRects - i, i);
            }

        } else if (benchmark == UNOR_DOUBLE_PERIMITER) {

            for (int i = 0; i < numRects; i++) {
                rectangles[i] = new Rectangle(i+1, 2*numRects - i, i);
            }
            rotations = true;

        }

        // calculate rectangle area
        long areaRects = 0;
        for (Rectangle r : rectangles) {
            areaRects += (long) r.width * (long) r.height;
        }

        // start timer
        long startTime = System.nanoTime();

        long areaBox = 0;
        // run test
        if (solver == OPTIMAL) {

            Solver algorithm = new OptimalRectanglePacking2(rotations, height);
            algorithm.solver(rectangles);
            areaBox = ((OptimalRectanglePacking2) algorithm).GetAreaSmallestBoundingBox();

        } else if (solver == MAX_RECT) {

            Solver algorithm = new MaximalRectanglesAlgorithm(rotations, height);
            algorithm.solver(rectangles);
            areaBox = ((MaximalRectanglesAlgorithm) algorithm).getEnclosingRectangle().width *
                    ((MaximalRectanglesAlgorithm) algorithm).getEnclosingRectangle().height;

        } else if (solver == BIN_TREE) {

            Solver algorithm = new BinaryTreeBinPacking(rotations, height);
            algorithm.solver(rectangles);
            areaBox = ((BinaryTreeBinPacking) algorithm).getEnclosingRectangle().width *
                    ((BinaryTreeBinPacking) algorithm).getEnclosingRectangle().height;

        }

        // end timer
        long endTime = System.nanoTime();

        // print info test
        String output = "TEST\n";
        if (benchmark == CONSEQUTIVE_SQUARE) {
            output += "benchmark: consequtive square\n";
        } else if (benchmark == OR_EQU_PERIMITER) {
            output += "benchmark: oriented equal perimiter\n";
        } else if (benchmark == UNOR_DOUBLE_PERIMITER) {
            output += "benchmark: unoriented double perimiter\n";
        }
        output += "N = " + numRects + ", heigth = " + height + ", rotations = " + rotations + "\n";

        if (solver == OPTIMAL) {
            output += "algorithm: optimal\n";
        }
        // print the running time
        output += ("running time: " +
                new DecimalFormat("#0.00000000").format((double) (endTime - startTime) * 1e-9)
                + " seconds\n");

        // print percentage of wasted space
        output += "wasted space: " + (areaBox - areaRects) / (areaBox *1.0) + "\n\n";

        System.out.println(output);
    }





    public static void main(String[] args) {
        Tester myTester = new Tester();

        for (int i = 1; i <= 13; i++) {
            myTester.test(UNOR_DOUBLE_PERIMITER, OPTIMAL, i);
        }
    }
}
