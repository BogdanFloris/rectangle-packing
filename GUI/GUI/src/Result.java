import java.io.*;

/**
 * Created by Job Savelsberg on 7-5-2017.
 */
public class Result {
    private File output;
    private Reader reader;

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

    public Result(String fileName){
        this(new File("../Algorithm/src/tests/"+fileName+".out"));
    }
    public Result(File file){
        output = file;
        reader = new Reader(output);

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

        //Calculate Bounding Box
        if(height == 0){
            for(PackingRectangle r: rectangles){
                if(r.y + r.height > height) height = r.y + r.height;
                if(r.x + r.width > width) width = r.x + r.width;
            }
        }

        //Calculate Area
        area = height*width;

        //Calculate filled & wasted space
        for(PackingRectangle r: rectangles){
            filledSpace += r.area;
        }
        wastedSpace = area-filledSpace;

        //Waste Percentage
        wastePercentage = (double)wastedSpace / (double)area * 100.0;
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
}
