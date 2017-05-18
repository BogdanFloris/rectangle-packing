import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s153640 on 10-5-2017.
 */
public class Generator {



    public Generator(){

    }

    //Generates a random set of rectangles
    public Test generateRandom(boolean heightFixed, boolean rotationsAllowed, int n, int containerHeight, int rectMin, int rectMax){
        Test test = new Test(n);
        test.heightFixed = heightFixed;
        test.height = containerHeight;
        test.rotations = rotationsAllowed;


        for(int r = 0; r < n; r++){
            test.rectangles[r] = generateRectangle(r, rectMin, rectMax);
        }

        return test;
    }

    private PackingRectangle generateRectangle(int index, int rectMin, int rectMax) {
        int width = (int) Math.round((Math.random()*(rectMax-rectMin)+rectMin));
        int height = (int) Math.round((Math.random()*(rectMax-rectMin)+rectMin));

        return new PackingRectangle(width,height,index);
    }

    public Result fillRandom(boolean heightFixed, boolean rotationsAllowed, int width, int height, int rectMin, int rectMax, int iterations){
        Result result = new Result(heightFixed,rotationsAllowed,width,height);
        ArrayList<PackingRectangle> list = new ArrayList<>();
        int i = 0;
        boolean filled = false;
        int tries = 0;
        while(!filled){
            tries++;
            //System.out.println("Trying "+tries);
            PackingRectangle rect = generateRectangle(i, rectMin, rectMax);

            rect.x = (int) Math.floor(Math.random()*(width-rect.width+1));
            rect.y = (int) Math.floor(Math.random()*(height-rect.height+1));

            //System.out.println(rect.x +","+rect.y);
            boolean canPlace = true;
            for(PackingRectangle placedRect: list){
                if(rect.right()<placedRect.left()+1 || placedRect.right()<rect.left()+1 || rect.top()<placedRect.bottom()+1 || placedRect.top()<rect.bottom()+1){
                }else{
                    canPlace = false;
                }
            }

            if(canPlace){
                System.out.println("Placed rect "+i);
                list.add(rect);
                i++;
            }

            if(tries == iterations){
                break;
            }

        }
        result.rectangles = new PackingRectangle[list.size()];
        result.n = list.size();
        result.rectangles = list.toArray(result.rectangles);
        return result;
    }

    public Test generateIncreasingSquare(boolean heightFixed, boolean rotationsAllowed, int n, int minSize){
        Test test = new Test(n);
        test.heightFixed = heightFixed;
        test.rotations = rotationsAllowed;
        for(int i = 0; i < n; i++){
            PackingRectangle pr = new PackingRectangle(i+minSize,i+minSize, i);
            test.rectangles[i] = pr;
        }
        return test;
    }
    //Generate Optimal?
    /*public Result generateOptimal(boolean heightFixed, boolean rotationsAllowed, int width, int height, int n){
        Result result = new Result(heightFixed, rotationsAllowed, width, height, n);

        int xIncrement = 0;
        int yIncrement = 0;

        int index = 0;
        for(int y = 0; y < height; y += yIncrement){
            for(int x = 0; x <  width;  x += xIncrement){
                //result.rectangles[index]  = new PackingRectangle();
                //i++;
            }
        }

    }*/

    /*
    A harder benchmark. oriented equal-perimeter rectangle benchmark
     from page 52 https://jair.org/media/3735/live-3735-6794-jair.pdf
     page 53 contains an optimal solution for N = 23

    each instance is a set of rectangles of sizes 1×N, 2×(N −1), ..., (N −1)×2, N ×1,
    and rectangles may not be rotated
     */
    public Test generateOrientedEqualPerimeter(boolean heightFixed, int n) {
        Test test = new Test(n);
        test.heightFixed = heightFixed;
        test.rotations = false;

        for(int i = 0; i < n; i++){
            PackingRectangle pr = new PackingRectangle(i + 1,n - i, i);
            test.rectangles[i] = pr;
        }
        return test;
    }

    /*
    An even harder benchmark. unoriented double-perimeter rectangle benchmark
     from page 52 https://jair.org/media/3735/live-3735-6794-jair.pdf

     where instances
    are described as a set of rectangles 1×(2N −1), 2×(2N −2), ..., (N −1)×(N + 1),
    N × N, and rectangles may be rotated by 90-degrees
     */
    public Test generateUnOrientedDoublePerimeter(boolean heightFixed, int n) {
        Test test = new Test(n);
        test.heightFixed = heightFixed;
        test.rotations = true;

        for(int i = 0; i < n; i++){
            PackingRectangle pr = new PackingRectangle(i + 1,2 * n - 1 - i, i);
            test.rectangles[i] = pr;
        }
        return test;
    }



}
