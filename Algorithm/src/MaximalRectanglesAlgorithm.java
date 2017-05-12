import java.io.*;
import java.util.*;

/**
 * Implementation of the MaximalRectanglesAlgorithm;
 * based on the research paper by Jukka Jylanki.
 */
public class MaximalRectanglesAlgorithm implements Solver {
    private int binWidth;                           // enclosing bin width
    private int binHeight;                          // enclosing bin height
    private ArrayList<Rectangle> usedRectangles;    // list of used rectangles
    private ArrayList<Rectangle> freeRectangles;    // list of free rectangles
    private boolean allowRotations;                 // whether to rotate the rectangles or not
    private FreeRectangleHeuristic heuristic;       // the heuristic to be used (default: best area)

    /**
     * Specifies the different heuristic to be used when placing a new rectangle into a free space.
     */
    public enum FreeRectangleHeuristic {
        BestShortSideFit,   // place against the short side of a rectangle into which it fits best
        BestLongSideFit,    // place against the long side of a rectangle into which it fits best
        BestAreaFit,        // place into the free rectangle with the smallest area
        BottomLeftRule,     // the Tetris placement
        ContactPointRule    // place such that the rectangle touches other rectangles as much as possible
    }

    /**
     * Instantiate a bin of zero area.
     */
    public MaximalRectanglesAlgorithm(boolean allowRotations) {
        this.binWidth = 0;
        this.binHeight = 0;
        this.allowRotations = allowRotations;
        this.heuristic = FreeRectangleHeuristic.BestAreaFit;
    }

    /**
     * Instantiate a bin of the given size.
     *
     * @param width the width of the bin
     * @param height the height of the bin
     */
    public MaximalRectanglesAlgorithm(int width, int height, boolean allowRotations) {
        Rectangle initBin = new Rectangle();

        this.binWidth = width;
        this.binHeight = height;
        this.allowRotations = allowRotations;
        this.heuristic = FreeRectangleHeuristic.BestAreaFit;

        initBin.width = width;
        initBin.height = height;
        initBin.x = 0;
        initBin.y = 0;

        usedRectangles = new ArrayList<>();
        freeRectangles = new ArrayList<>();

        freeRectangles.add(initBin);
    }

    /**
     * Set the heuristic to be used when placing a rectangle into a free rectangle.
     *
     * @param heuristic the heuristic to be used (taken from the {@code enum})
     */
    public void setHeuristic(FreeRectangleHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Insert a single rectangle into the bin and then return it with
     * updated (x, y) coordinates.
     *
     * @param rectangle the rectangle to insert
     * @param heuristic the heuristic used for insertion into a free space
     * @return the rectangle with updated (x, y) coordinates
     */
    private Rectangle insert(Rectangle rectangle, FreeRectangleHeuristic heuristic) {
        return null;
    }

    /**
     * Method used when inserting a new rectangle into a free one.
     * If the free rectangle is split this returns true.
     *
     * @param freeRect the free rectangle
     * @param usedRect the rectangle to insert in the free space
     * @return true if the free rectangle is split and false otherwise
     */
    private boolean splitFreeRectangle(Rectangle freeRect, final Rectangle usedRect) {
        return false;
    }

    /**
     * Cleanup the free list of rectangles - i.e. eliminate duplicate spaces.
     */
    private void pruneFreeList() {

    }

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        return null;
    }

    /**
     * Method used to place a rectangle according to the best area fit heuristic.
     * The method returns the rectangle with its (x, y) coordinates updated.
     *
     * @param rectangle the rectangle to be place in the free space
     * @return the same rectangle with updated coordinates
     */
    Rectangle FindPositionBestAreaFit(Rectangle rectangle) {
       return null;
    }

    /**
     * Method used to place a rectangle according to the best short side fit heuristic.
     * The method returns the rectangle with its (x, y) coordinates updated.
     *
     * @param rectangle the rectangle to be place in the free space
     * @return the same rectangle with updated coordinates
     */
    Rectangle FindPositionBestShortSideFit(Rectangle rectangle) {
        return null;
    }

    /**
     * Method used to place a rectangle according to the best long side fit heuristic.
     * The method returns the rectangle with its (x, y) coordinates updated.
     *
     * @param rectangle the rectangle to be place in the free space
     * @return the same rectangle with updated coordinates
     */
    Rectangle FindPositionBestLongSideFit(Rectangle rectangle) {
        return null;
    }

    /**
     * Method used to place a rectangle according to the bottom left rule.
     * The method returns the rectangle with its (x, y) coordinates updated.
     *
     * @param rectangle the rectangle to be place in the free space
     * @return the same rectangle with updated coordinates
     */
    Rectangle FindPositionBottomLeftRule(Rectangle rectangle) {
        return null;
    }

    /**
     * Method used to place a rectangle according to the contact point rule.
     * The method returns the rectangle with its (x, y) coordinates updated.
     *
     * @param rectangle the rectangle to be place in the free space
     * @return the same rectangle with updated coordinates
     */
    Rectangle FindPositionContactPointRule(Rectangle rectangle) {
        return null;
    }

    /**
     * Return the ratio of used surface area to the size of the bin.
     * @return
     */
    public double occupancy() {
        long used = 0;
        for (Rectangle rectangle : usedRectangles) {
            used += rectangle.width * rectangle.height;
        }

        return ((double) used) / ((double) binWidth * (double) binHeight);
    }
}