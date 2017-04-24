import java.io.*;
import java.util.*;

/**
 * Created by bogdanfloris on 24/04/2017.
 */
public class Main {
    private InputReader in;
    private PrintWriter out;

    public void solve() {
        
    }

    public void runIO() {
        in = new InputReader(System.in);
        out = new PrintWriter(System.out);

        solve();

        out.close();
    }

    public void run() {
        try {
            in = new InputReader(new File("in"));
            out = new PrintWriter(new File("out"));
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
        new Main().runIO();
    }
}
