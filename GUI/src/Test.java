import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by s153640 on 10-5-2017.
 */
public class Test {
    //private static final String OUTPUT_FILE = "\"../Algorithm/src/tests/";         // standard stream input
    private static final String OUTPUT_FILE = "../Algorithm/src/tests/";

    public int height = 0;
    public boolean heightFixed = false;
    public boolean rotations;
    public int n;

    private PrintWriter out;                                // the standard output stream of the program

    public PackingRectangle[] rectangles;

    public Test(int n){
        this.n = n;
        rectangles = new PackingRectangle[n];
    }

    public void writeFile(String name){
        File file = new File(OUTPUT_FILE+name+".in");
        try {
            out = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        out.print("container height: " + (heightFixed?"fixed "+height:"free"));

        out.println();

        out.println("rotations allowed: " + (rotations ? "yes" : "no"));

        out.println("number of rectangles: " + n);

        for (PackingRectangle rectangle : rectangles) {
            out.println(rectangle);
        }

        out.close();
    }
}
