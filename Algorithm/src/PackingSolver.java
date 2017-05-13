import java.io.*;
import java.text.DecimalFormat;

public class PackingSolver {
    /** CONSTANTS */
    private static final String IN_STD_FILE = "src/tests/test1.in";         // standard stream input
    private static final String OUT_STD_FILE = "src/tests/out.out";         // standard stream output
    private static final String OUT_DEBUG_FILE = "src/tests/debug.out";     // error    stream output

    /** INSTANCE VARIABLES */
    private String      variant;                            // free or fixed
    private int         height;                             // height of the enclosing rectangle
    private boolean     rotations;                          // whether we can rotate rectangles or not
    private int         n;                                  // the number of rectangles
    private Rectangle[] rectangles;                         // array containing the rectangles
    private Solver      solver;                             // the solver used in computing the result

    private InputReader in;                                 // the standard input stream of the program
    private PrintWriter out;                                // the standard output stream of the program
    private PrintWriter debug;                              // the standard error stream of the program

    /**
     * solver method that provides the final algorithm.
     */
    public void solve() {
        /** Read the Input */
        // skip container height
        in.next();
        in.next();

        // read variant ("free" or "fixed")
        variant = in.next();
        if (variant.equals("fixed")) {
            height = in.nextInt();
        }

        // skip "rotations allowed"
        in.next();
        in.next();

        // read version ("yes" or "no")
        rotations = (in.next().equals("no")) ? false : true;

        // skip "number of rectangles:"
        in.next();
        in.next();
        in.next();

        // read number of rectangles ("n")
        n = in.nextInt();

        rectangles = new Rectangle[n];

        for (int i = 0; i < n; i++) {
            rectangles[i] = new Rectangle(in.nextInt(), in.nextInt(), i);
        }

        /** Write the Output */
        // write the initial part (identical with the input)
        out.print("container height: " + variant);
        if (variant.equals("fixed")) {
            out.print(height);
        }
        out.println();

        out.println("rotations allowed: " + (rotations ? "yes" : "no"));

        out.println("number of rectangles: " + n);

        for (Rectangle rectangle : rectangles) {
            out.println(rectangle);
        }

        // output the placement of the rectangles
        out.println("placement of rectangles");

        /** Solve the packing problem */
        // solve the problem with a certain algorithm
        long startTime = System.nanoTime();
        solver = new MaximalRectanglesAlgorithm(rotations);
        long endTime = System.nanoTime();

        // display running time
        debug.println("runtime = " +
                new DecimalFormat("#0.000000").format((double) (endTime - startTime) * (10e-9)) +
                "s");
        debug.flush();

        Rectangle[] result = solver.solver(rectangles);

        // output the position of each rectangle
        // if required, also output whether the rectangle is rotated
        for (Rectangle rectangle : result) {
            out.println((rotations ? (rectangle.rotated ? "yes " : "no ") : "")
                        + rectangle.x + " " + rectangle.y);
        }
    }

    /**
     * Run the algorithm on the input received in the console.
     * Output the result in the console.
     */
    public void runIO() {
        in = new InputReader(System.in);
        out = new PrintWriter(System.out);
        debug = new PrintWriter(System.err);

        solve();

        out.close();
        debug.close();
    }

    /**
     * Run the algorithm on the input received in the file {@code IN_STD_FILE}.
     * Output the result in the file {@code OUT_STD_FILE}.
     * One can also output the results in the file used for debugging,
     * namely {@code OUT_DEBUG_FILE}.
     */
    public void run() {
        try {
            in = new InputReader(new File(IN_STD_FILE));
            out = new PrintWriter(new File(OUT_STD_FILE));
            debug = new PrintWriter(new File(OUT_DEBUG_FILE));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        debug.println("test file: " + IN_STD_FILE);

        solve();

        out.close();
        debug.close();
    }

    public static void main(String[] args) {
        new PackingSolver().run();
    }
}
