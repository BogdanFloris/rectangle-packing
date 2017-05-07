import java.io.*;
import java.util.*;

public class Reader {
    private BufferedReader reader;
    private StringTokenizer tokenizer;

    public Reader(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
        tokenizer = null;
    }

    public Reader(File f) {
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

    public void skipWord(int amount){
        for(int i = 0; i < amount; i++){
            next();
        }
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
