import java.awt.*;
import java.io.*;

/**
 * Created by Job Savelsberg on 7-5-2017.
 */
public class Result {
    private File output;
    private PrintWriter out;                                // the standard output stream of the program
    private Reader reader;
    private String text;

    public int height = 0;
    public boolean heightFixed = false;
    public boolean rotations;
    public int n;

    public PackingRectangle[] rectangles;

    public int width = 0;
    public int area;
    public int filledSpace = 0;
    public int wastedSpace;
    public double wastePercentage;
    public boolean hasOverlap = false;
    public boolean exceedsHeight = false;

    public Result(File file){
        output = file;
        reader = new Reader(output);
        read(reader);
        text = write();
    }

    public void read(Reader r){
        reader = r;

        //Container Height
        reader.skipWord(2);
        String containerHeightString = reader.next();
        if(containerHeightString.equals("fixed")){
            heightFixed = true;
            height = reader.nextInt();
        }

        //Rotations allowed
        reader.skipWord(2);
        rotations = reader.next().equals("no")?false:true;

        //Number of Rectangles
        reader.skipWord(3);
        n = reader.nextInt();

        rectangles = new PackingRectangle[n];

        //Store all the rectangle dimensions
        for (int i = 0; i < n; i++) {
            rectangles[i] = new PackingRectangle(reader.nextInt(), reader.nextInt(), i);
        }

        //Store the placement values
        reader.skipWord(3);
        for (int i = 0; i < n; i++) {
            boolean rotated = rotations?reader.next().equals("yes")?true:false:false;
            rectangles[i].place(rotated, reader.nextInt(), reader.nextInt());
        }
        text = write();
    }

    public Result(boolean heightFixed, boolean rotationsAllowed, int width, int height, int n) {
        this(heightFixed,rotationsAllowed,width,height);
        this.n = n;
        this.rectangles = new PackingRectangle[n];
    }

    public Result(boolean heightFixed, boolean rotationsAllowed, int width, int height) {
        this.heightFixed = heightFixed;
        this.rotations = rotationsAllowed;
        this.width = width;
        this.height = height;
    }

        //Every file that has container as the first word is assumed to be a valid output file
    public static boolean isResult(File f){
        Reader reader = new Reader(f);
        if(reader.next().equals("container")){
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return output.getName().replace(".out","");
    }

    public String getOriginal() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(output.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String original = "";
        while(true){
            try {
                String line = reader.readLine();
                if(line != null){
                    original += line+"\n";
                }else{
                    break;
                }
            } catch (IOException e) {
                break;
            }
        }
        return original;
    }

    public void writeFile(String name){
        output = new File("../Algorithm/src/tests/"+name+".out");
        try {
            out = new PrintWriter(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /** Write the Output */
        out.print(write());
        out.close();
    }

    public String write(){
        StringBuilder sb = new StringBuilder();
        // write the initial part (identical with the input)
        sb.append("container height: " + (heightFixed?"fixed "+height:"free")+"\n");


        sb.append("rotations allowed: " + (rotations ? "yes" : "no")+"\n");

        sb.append("number of rectangles: " + n + "\n");

        for (PackingRectangle rectangle : rectangles) {
            sb.append(rectangle+ "\n");
        }
        // output the placement of the rectangles
        sb.append("placement of rectangles" + "\n");


        // output the position of each rectangle
        // if required, also output whether the rectangle is rotated
        for (PackingRectangle rectangle : rectangles) {
            sb.append((rotations ? (rectangle.rotated ? "yes " : "no ") : "")
                    + rectangle.x + " " + rectangle.y +"\n");
        }

        text = sb.toString();
        return text;
    }

    public void calculateEfficiency(){
        hasOverlap = hasOverlap();

        //Calculate Bounding Box
        int calculatedHeight = 0;
        width = 0;
       for(PackingRectangle r: rectangles){
            if(r.y + r.getHeight() > calculatedHeight) calculatedHeight = r.y + r.getHeight();
            if(r.x + r.getWidth() > width) width = r.x + r.getWidth();
       }
       if(heightFixed){
           exceedsHeight = calculatedHeight > height? true: false;
       }else{
           height = calculatedHeight;
       }


        //Calculate Area
        area = height*width;

       filledSpace = 0;
        //Calculate filled & wasted space
        for(PackingRectangle r: rectangles){
            filledSpace += r.area;
        }
        wastedSpace = area-filledSpace;

        //Waste Percentage
        wastePercentage = (double)wastedSpace / (double)area * 100.0;


    }

    public boolean hasOverlap(){
        for(PackingRectangle r1: rectangles){
            for(PackingRectangle r2: rectangles){
                if(!r1.equals(r2) && r1.overlaps(r2)){
                    r1.setColor(Color.BLACK);
                    r2.setColor(Color.WHITE);
                    System.out.println(toString()+" has overlap: \n" +
                            r1.toString() + " @ " + r1.x + ", "+r1.y+" & "+r2.toString()+ " @ " + r2.x + ", "+r2.y+
                            "\nthey are colored black & white");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValid(){
        return !hasOverlap && !exceedsHeight;
    }


    public void reload(String text) {
        read(new Reader(text));
        calculateEfficiency();

    }

    public String getText() {
        return text;
    }

    public Result cpy(){
        Result copy = new Result(output);
        copy.reload(write());
        return copy;
    }
}
