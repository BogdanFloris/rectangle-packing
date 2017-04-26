import java.io.*;
import java.util.*;

public class Main {
    /** CONSTANTS */
    private static final String IN_STD_FILE = "src/tests/test2.in";         // standard stream input
    private static final String OUT_STD_FILE = "src/tests/out.out";         // standard stream output
    private static final String OUT_DEBUG_FILE = "src/tests/debug.out";     // error    stream output

    /** INSTANCE VARIABLES */
    private String      variant;                            // free or fixed
    private int         height;                             // height of the enclosing rectangle
    private boolean     rotations;                          // whether we can rotate rectangles or not
    private int         n;                                  // the number of rectangles
    private Rectangle[] rectangles;                         // array containing the rectangles

    /**
     * Main method that provides the final algorithm.
     */
    public void solve() {
        readInput();

        debugOutput();
    }

    /**
     * Method through which the input is read in standard format
     * (as specified in the Problem-Description document).
     */
    private void readInput() {
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
            rectangles[i] = new Rectangle(in.nextInt(), in.nextInt());
        }
    }

    /**
     * Method used for debugging the algorithm.
     */
    private void debugOutput() {
        debug.println(variant);
        if (variant.equals("fixed")) {
            debug.println(height);
        }
        debug.println(rotations);
        debug.println(n);
        for (int i = 0; i < n; i++) {
            debug.println(rectangles[i]);
        }
    }

    /**
     * Main class that represents a rectangle.
     */
    private class Rectangle {
         private int width;
         private int height;

         public Rectangle(int width, int height) {
             this.width = width;
             this.height = height;
         }

         public int getWidth() {
             return width;
         }

         public int getHeight() {
             return height;
         }
         @Override
         public String toString() {
            return width + " " + height;
         }
    }

    /** ---------------------- */
    /** START OF TEMPLATE CODE */
    private InputReader in;
    private PrintWriter out;
    private PrintWriter debug;

    public void runIO() {
        in = new InputReader(System.in);
        out = new PrintWriter(System.out);
        debug = new PrintWriter(System.out);

        solve();

        out.close();
        debug.close();
    }

    public void run() {
        try {
            in = new InputReader(new File(IN_STD_FILE));
            out = new PrintWriter(new File(OUT_STD_FILE));
            debug = new PrintWriter(new File(OUT_DEBUG_FILE));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        solve();

        out.close();
        debug.close();
    }

    private class InputReader {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream));
            tokenizer = null;
        }

        public InputReader(File f) {
            try {
                reader = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public String nextLine() {
            tokenizer = null;
            try {
                return reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /** -------------------- */
    /** END OF TEMPLATE CODE */

    public static void main(String[] args) {
        new Main().run();
    }
}
