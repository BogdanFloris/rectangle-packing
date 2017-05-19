import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s153640 on 10-5-2017.
 */
public class Generator {



    public Generator(){

    }

    //Generates a random test set of n rectangles
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
    /*
    For a number of iterations it tries to place a random sized rectangle at a random location and tests if it fits,
    if so, it places it.
     */
    public Result fillRandom(boolean heightFixed, boolean rotationsAllowed, int width, int height, int rectMin, int rectMax, int iterations){
        Result result = new Result(heightFixed,rotationsAllowed,width,height);
        ArrayList<PackingRectangle> list = new ArrayList<>();
        int i = 0;
        boolean filled = false;
        int tries = 0;
        while(!filled){
            tries++;
            PackingRectangle rect = generateRectangle(i, rectMin, rectMax);

            if(rotationsAllowed){
                rect.rotated = Math.random() < 0.5? true:false;
            }

            rect.x = (int) Math.floor(Math.random()*(width-rect.getWidth()+1));
            rect.y = (int) Math.floor(Math.random()*(height-rect.getHeight()+1));

            boolean canPlace = true;
            for(PackingRectangle placedRect: list){
               if(rect.overlaps(placedRect)) canPlace = false;
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

    /*
    Generates a test set of squares starting from a minimum size and increase by 1 n times.
     */
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
