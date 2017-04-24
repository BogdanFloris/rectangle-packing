import com.sun.org.glassfish.gmbal.ManagedObject;

import java.io.*;
import java.util.*;

/**
 * Created by bogdanfloris on 24/04/2017.
 */
public class Main {
    private String variant;
    private int height;
    private boolean rotations;
    private int n; // number of rectangles
    private Rectangle[] rectangles;
    private static final String INFILE = "src/test2.in";
    private static final String OUTFILE = "src/out.out";

    public void solve() {
        readInput();
        test();
    }

    private void readInput() {
        in.next();
        in.next();
        variant = in.next();
        if (variant.equals("fixed")) {
            height = in.nextInt();
        }

        in.next();
        in.next();
        rotations = (in.next().equals("no")) ? false : true;

        in.next();
        in.next();
        in.next();
        n = in.nextInt();
        rectangles = new Rectangle[n];

        for (int i = 0; i < n; i++) {
            rectangles[i] = new Rectangle(in.nextInt(), in.nextInt());
        }
    }

    private void test() {
        out.println(variant);
        if (variant.equals("fixed")) {
            out.println(height);
        }
        out.println(rotations);
        out.println(n);
        for (int i = 0; i < n; i++) {
            out.println(rectangles[i]);
        }

    }

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

    private InputReader in;
    private PrintWriter out;

    public void runIO() {
        in = new InputReader(System.in);
        out = new PrintWriter(System.out);

        solve();

        out.close();
    }

    public void run() {
        try {
            in = new InputReader(new File(INFILE));
            out = new PrintWriter(new File(OUTFILE));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        solve();

        out.close();
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

    public static void main(String[] args) {
        new Main().run();
    }
}
