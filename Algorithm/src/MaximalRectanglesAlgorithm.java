import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation of the Maximal Rectangles Algorithm.
 * Based on the research paper and code by Jukka Jylanki.
 *
 */

/**
 * --------------------------- SCORES LOG ---------------------------
 * Scores for the sample (test1.in - no rotations - no fixed height) with each heuristic:
 * SHORT SIDE FIT       = 14.02% wasted space
 * LONG SIDE FIT        = 29.94% wasted space
 * BEST AREA FIT        = 9.92% wasted space
 * BOTTOM LEFT RULE     = 24.08% wasted space
 * CONTACT POINT RULE   = 14.02% wasted space
 *
 * Scores for 03_01_hf_rn.txt
 * BEST AREA FIT        = 36.36% wasted space
 *
 * Scores for 05_04_hf_rn.txt
 * BEST AREA FIT        = 27.94% wasted space
 *
 * Scores for 10_04_hf_rn.txt
 * BEST AREA FIT        = 23.97% wasted space
 *
 * Scores for 25_03_hf_rn.txt
 * BEST AREA FIT        = 17.82% wasted space
 *
 * Scores for 10000_03_hf_rn.txt
 *      NO scores recorded since the execution takes too much time.
 */
public class MaximalRectanglesAlgorithm implements Solver {
    /** DEBUGGING ONLY */
    private PrintWriter debug = new PrintWriter(System.err);
    /** DEBUGGING ONLY */

    private boolean rotationsAllowed;                   // whether rectangles can be rotated or not
    private int binWidth;                               // the width of the enclosing rectangle
    private int binHeight;                              // the height of the enclosing rectangle
    private ArrayList<Rectangle> usedRectangles;        // list of used rectangles
    private ArrayList<Rectangle> freeRectangles;        // list of free rectangles

    /**
     * List of heuristics to be used when choosing what free rectangle to currently fill.
     */
    public enum FreeRectangleHeuristic {
        BestShortSideFit, // positions the rectangle against the short side of a free rectangle into which it fits best
        BestLongSideFit, // positions the rectangle against the long side of a free rectangle into which it fits best
        BestAreaFit, // positions the rectangles into the smallest free rectangle into which it fits
        BottomLeftRule, // basic placement (kinda like Tetris)
        ContactPointRule // chooses the placement where the rectangle touches other rectangles as much as possible
    }

    @Override
    public Rectangle[] solver(Rectangle[] rectangles) {
        // the initial values of the bin
        int width, height;

        // the array in which the rectangles have updated (x, y) coordinates
        Rectangle[] orderedRectangles;

        // the initial width/height of the bin
        // will be equal to the length of the smallest
        // side of the biggest rectangle
        int startValue = Integer.MIN_VALUE;
        for (int i = 0; i < rectangles.length; i++) {
            int value = Math.min(rectangles[i].width, rectangles[i].height);
            if (startValue < value) {
                startValue = value;
            }
        }

        width = height = startValue;

        ArrayList<Rectangle> arr = new ArrayList<>();
        arr.addAll(Arrays.asList(rectangles));

        // each iteration only one of: width, height will be updated
        boolean turn = true;

        // try bins until we find one in which all the rectangles fit
        do {
            init(width, height);
            if (turn) width++;
            else height++;
            turn = !turn;
        } while ((orderedRectangles = insertRectangles(new ArrayList<>(arr),
                FreeRectangleHeuristic.BestAreaFit)) == null);

        return orderedRectangles;
    }

    public MaximalRectanglesAlgorithm(boolean rotationsAllowed) {
        this.rotationsAllowed = rotationsAllowed;
        init(0, 0);
    }

    /**
     * Initialization of instance variables and of the width and height of the bin.
     *
     * @param width the width of the bin
     * @param height the height of the bin
     */
    private void init(int width, int height) {
        this.binWidth = width;
        this.binHeight = height;

        Rectangle enclosingRect = new Rectangle();
        enclosingRect.x = 0;
        enclosingRect.y = 0;
        enclosingRect.width = width;
        enclosingRect.height = height;

        usedRectangles = new ArrayList<>();
        freeRectangles = new ArrayList<>();
        freeRectangles.add(enclosingRect);
    }

    /**
     * Insert a given array of rectangles in a bin of fixed dimensions.
     *
     * @param rectangles the given array of rectangles
     * @param heuristic the heuristic used in placing rectangles into free bins
     * @return an array in which the rectangles now have updated (x, y) coordinates
     */
    private Rectangle[] insertRectangles(ArrayList<Rectangle> rectangles, FreeRectangleHeuristic heuristic) {
        ArrayList<Rectangle> orderedRectangles = new ArrayList<>();

        while (rectangles.size() > 0) {
            int bestScore1 = Integer.MAX_VALUE;
            int bestScore2 = Integer.MAX_VALUE;
            int bestRectIndex = -1;
            Rectangle bestRect = null;

            // choose the best rectangle to currently place into an empty bin
            // i.e. try to maximise the placement of the next rectangle
            // by choosing the best rectangle to place
            for (int i = 0; i < rectangles.size(); i++) {
                RectangleAndScoreReturn ret = null;

                switch (heuristic) {
                    case BestShortSideFit:
                        ret = FindPositionBestShortSideFit(rectangles.get(i));
                        break;
                    case BestLongSideFit:
                        ret = FindPositionBestLongSideFit(rectangles.get(i));
                        break;
                    case BestAreaFit:
                        ret = FindPositionBestAreaFit(rectangles.get(i));
                        break;
                    case BottomLeftRule:
                        ret = FindPositionBottomLeftRule(rectangles.get(i));
                        break;
                    case ContactPointRule:
                        ret = FindPositionContactPointRule(rectangles.get(i));
                        break;
                }

                if (heuristic.equals(FreeRectangleHeuristic.ContactPointRule)) { // bigger is better in this case
                    ret.score1 = -ret.score1;
                }

                // cannot fit the new rectangle
                if (ret.rectangle.height == 0) {
                    ret.score1 = Integer.MAX_VALUE;
                    ret.score2 = Integer.MAX_VALUE;
                    return null;
                }

                // can fit the current rectangle
                if (ret.score1 < bestScore1 || (ret.score1 == bestScore1 && ret.score2 < bestScore2)) {
                    bestScore1 = ret.score1;
                    bestScore2 = ret.score2;
                    bestRect = ret.rectangle;
                    bestRectIndex = i;
                }
            }

            if (bestRectIndex == -1) {
                return null;
            }

            // place the current rectangle
            PlaceRectangle(bestRect);
            orderedRectangles.add(bestRect);
            rectangles.remove(bestRectIndex);
        }

        Rectangle[] answer = new Rectangle[orderedRectangles.size()];
        for (int i = 0; i < answer.length; i++) {
            answer[orderedRectangles.get(i).index] = orderedRectangles.get(i);
        }

        return answer;
    }

    /**
     * Place the current rectangle in a free space.
     *
     * @param rectangle the rectangle to place.
     */
    private void PlaceRectangle(Rectangle rectangle) {
        int limit = freeRectangles.size();

        for (int i = 0; i < limit; i++) {
            if (splitFreeRectangle(freeRectangles.get(i), rectangle)) {
                freeRectangles.remove(i);
                i--;
                limit--;
            }
        }

        pruneFreeRectanglesList();
        usedRectangles.add(rectangle);
    }

    /**
     * Determine whether we can place the used rectangle in the free space.
     * Return true in that case; false otherwise.
     *
     * If the intersection is possible, split the free space and add the newly generated
     * free rectangles in the free rectangles list.
     *
     * @param freeRect the free space
     * @param usedRect the rectangle to place
     */
    private boolean splitFreeRectangle(Rectangle freeRect, Rectangle usedRect) {
        // test if they even intersect each other
        if (usedRect.x >= freeRect.x + freeRect.width ||
                usedRect.x + usedRect.width <= freeRect.x ||
                usedRect.y >= freeRect.y + freeRect.height ||
                usedRect.y + usedRect.height <= freeRect.y) {
            return false;
        }

        // x-axis intersection
        if (usedRect.x < freeRect.x + freeRect.width &&
                usedRect.x + usedRect.width > freeRect.x) {
            // new free space at the bottom
            if (usedRect.y > freeRect.y &&
                    usedRect.y < freeRect.y + freeRect.height) {
                Rectangle newRect = new Rectangle(freeRect);
                newRect.height = usedRect.y - freeRect.y;
                freeRectangles.add(newRect);
            }

            //new free space at the top
            if (usedRect.y + usedRect.height < freeRect.y + freeRect.height) {
                Rectangle newRect = new Rectangle(freeRect);
                newRect.y = usedRect.y + usedRect.height;
                newRect.height = freeRect.y + freeRect.height - (usedRect.y + usedRect.height);
                freeRectangles.add(newRect);
            }
        }

        // y-axis intersection
        if (usedRect.y < freeRect.y + freeRect.height ||
                usedRect.y + usedRect.height > freeRect.y) {
            // new free space to the left
            if (usedRect.x > freeRect.x && usedRect.x < freeRect.x + freeRect.width) {
                Rectangle newRect = new Rectangle(freeRect);
                newRect.width = usedRect.x - freeRect.x;
                freeRectangles.add(newRect);
            }

            // new free space to the right
            if (usedRect.x + usedRect.width < freeRect.x + freeRect.width) {
                Rectangle newRect = new Rectangle(freeRect);
                newRect.x = usedRect.x + usedRect.width;
                newRect.width = freeRect.x + freeRect.width - (usedRect.x + usedRect.width);
                freeRectangles.add(newRect);
            }
        }

        return true;
    }

    /**
     * Make the space occupied by the free rectangles disjoint.
     * i.e. no two rectangles can share space on the plane.
     */
    private void pruneFreeRectanglesList() {
        for (int i = 0; i < freeRectangles.size(); i++) {
            for (int j = i + 1; j < freeRectangles.size(); j++) {
                if (isContainedIn(freeRectangles.get(i), freeRectangles.get(j))) {
                    // i is contained in j
                    freeRectangles.remove(i);
                    i--;
                    break;
                }

                if (isContainedIn(freeRectangles.get(j), freeRectangles.get(i))) {
                    // j is contained in i
                    freeRectangles.remove(j);
                    j--;
                }
            }
        }
    }

    /**
     * Check if a is contained in b.
     *
     * @param a the first rectangle
     * @param b the second rectangle
     * @return true if a is contained in b; false otherwise
     */
    private boolean isContainedIn(Rectangle a, Rectangle b) {
        return (a.x >= b.x && a.y >= b.y &&
                a.x + a.width <= b.x + b.width &&
                a.y + a.height <= b.y + b.height);
    }

    /**
     * Position the rectangle against the short side of a free rectangle into which it fits best.
     *
     * @param rectangle the given rectangle
     * @return a copy of the argument with updated (x, y) coordinates and two scores
     *          based on the short side fit and on the long side fit (in case there are ties in the short fit placement)
     */
    @SuppressWarnings("Duplicates")
    private RectangleAndScoreReturn FindPositionBestShortSideFit(Rectangle rectangle) {
        RectangleAndScoreReturn answer = new RectangleAndScoreReturn();

        Rectangle bestRect = new Rectangle();
        int bestShortSideFit = Integer.MAX_VALUE;
        int bestLongSideFit = Integer.MAX_VALUE;

        for (int i = 0; i < freeRectangles.size(); i++) {
            // check to see if the rectangle fits into the empty space
            if (freeRectangles.get(i).width >= rectangle.width &&
                    freeRectangles.get(i).height >= rectangle.height) {
                int leftoverHorizontal = Math.abs(freeRectangles.get(i).width - rectangle.width);
                int leftoverVertical = Math.abs(freeRectangles.get(i).height - rectangle.height);
                int shortSideFit = Math.min(leftoverHorizontal, leftoverVertical);
                int longSideFit = Math.max(leftoverHorizontal, leftoverVertical);

                if (shortSideFit < bestShortSideFit ||
                        (shortSideFit == bestShortSideFit && longSideFit < bestLongSideFit)) {
                    bestRect.x = freeRectangles.get(i).x;
                    bestRect.y = freeRectangles.get(i).y;
                    bestRect.width = rectangle.width;
                    bestRect.height = rectangle.height;
                    bestRect.index = rectangle.index;
                    bestShortSideFit = shortSideFit;
                    bestLongSideFit = longSideFit;
                }
            }

            if (rotationsAllowed) {
                // check to see if the rotated rectangle fits into the empty space
                int leftoverHorizontal = Math.abs(freeRectangles.get(i).width - rectangle.height);
                int leftoverVertical = Math.abs(freeRectangles.get(i).height - rectangle.width);
                int shortSideFit = Math.min(leftoverHorizontal, leftoverVertical);
                int longSideFit = Math.max(leftoverHorizontal, leftoverVertical);

                if (shortSideFit < bestShortSideFit ||
                        (shortSideFit == bestShortSideFit && longSideFit < bestLongSideFit)) {
                    bestRect.x = freeRectangles.get(i).x;
                    bestRect.y = freeRectangles.get(i).y;
                    bestRect.width = rectangle.width;
                    bestRect.height = rectangle.height;
                    bestRect.index = rectangle.index;
                    bestShortSideFit = shortSideFit;
                    bestLongSideFit = longSideFit;
                }
            }
        }

        // update the return value with the rectangle position and dimension + rectangle placement scores
        answer.rectangle = bestRect;
        answer.score1 = bestShortSideFit;
        answer.score2 = bestLongSideFit;

        return answer;
    }

    /**
     * Position the rectangle against the long side of a free rectangle into which it fits best.
     *
     * @param rectangle the given rectangle
     * @return a copy of the argument with updated (x, y) coordinates and two scores
     *          based on the long side fit and on the short side fit (in case there are ties in the long fit placement)
     */
    @SuppressWarnings("Duplicates")
    private RectangleAndScoreReturn FindPositionBestLongSideFit(Rectangle rectangle) {
        RectangleAndScoreReturn answer = new RectangleAndScoreReturn();

        Rectangle bestRect = new Rectangle();
        int bestShortSideFit = Integer.MAX_VALUE;
        int bestLongSideFit = Integer.MAX_VALUE;

        for (int i = 0; i < freeRectangles.size(); i++) {
            // check to see if the rectangle fits into the empty space
            if (freeRectangles.get(i).width >= rectangle.width &&
                    freeRectangles.get(i).height >= rectangle.height) {
                int leftoverHorizontal = Math.abs(freeRectangles.get(i).width - rectangle.width);
                int leftoverVertical = Math.abs(freeRectangles.get(i).height - rectangle.height);
                int shortSideFit = Math.min(leftoverHorizontal, leftoverVertical);
                int longSideFit = Math.max(leftoverHorizontal, leftoverVertical);

                if (longSideFit < bestLongSideFit ||
                        (longSideFit == bestLongSideFit && shortSideFit < bestShortSideFit)) {
                    bestRect.x = freeRectangles.get(i).x;
                    bestRect.y = freeRectangles.get(i).y;
                    bestRect.width = rectangle.width;
                    bestRect.height = rectangle.height;
                    bestRect.index = rectangle.index;
                    bestShortSideFit = shortSideFit;
                    bestLongSideFit = longSideFit;
                }
            }

            if (rotationsAllowed) {
                // check to see if the rotated rectangle fits into the empty space
                int leftoverHorizontal = Math.abs(freeRectangles.get(i).width - rectangle.height);
                int leftoverVertical = Math.abs(freeRectangles.get(i).height - rectangle.width);
                int shortSideFit = Math.min(leftoverHorizontal, leftoverVertical);
                int longSideFit = Math.max(leftoverHorizontal, leftoverVertical);

                if (longSideFit < bestLongSideFit ||
                        (longSideFit == bestLongSideFit && shortSideFit < bestShortSideFit)) {
                    bestRect.x = freeRectangles.get(i).x;
                    bestRect.y = freeRectangles.get(i).y;
                    bestRect.width = rectangle.width;
                    bestRect.height = rectangle.height;
                    bestRect.index = rectangle.index;
                    bestShortSideFit = shortSideFit;
                    bestLongSideFit = longSideFit;
                }
            }
        }

        // update the return value with the rectangle position and dimension + rectangle placement scores
        answer.rectangle = bestRect;
        answer.score1 = bestLongSideFit;
        answer.score2 = bestShortSideFit;

        return answer;
    }

    /**
     * Position the rectangle in the smallest bin in which it fits.
     *
     * @param rectangle the given rectangle
     * @return a copy of the argument with updated (x, y) coordinates and two scores
     *          based on the area fit and on the short side fit (in case there are ties in the area placement)
     */
    @SuppressWarnings("Duplicates")
    private RectangleAndScoreReturn FindPositionBestAreaFit(Rectangle rectangle) {
        RectangleAndScoreReturn answer = new RectangleAndScoreReturn();

        Rectangle bestRect = new Rectangle();
        int bestAreaFit = Integer.MAX_VALUE;
        int bestShortSideFit = Integer.MAX_VALUE;

        for (int i = 0; i < freeRectangles.size(); i++) {
            int areaFit = freeRectangles.get(i).width * freeRectangles.get(i).height -
                    rectangle.width * rectangle.height;

            // check to see if the rectangle fits into the empty space
            if (freeRectangles.get(i).width >= rectangle.width &&
                    freeRectangles.get(i).height >= rectangle.height) {
                int leftoverHorizontal = Math.abs(freeRectangles.get(i).width - rectangle.width);
                int leftoverVertical = Math.abs(freeRectangles.get(i).height - rectangle.height);
                int shortSideFit = Math.min(leftoverHorizontal, leftoverVertical);

                if (areaFit < bestAreaFit || (areaFit == bestAreaFit && shortSideFit < bestShortSideFit)) {
                    bestRect.x = freeRectangles.get(i).x;
                    bestRect.y = freeRectangles.get(i).y;
                    bestRect.width = rectangle.width;
                    bestRect.height = rectangle.height;
                    bestRect.index = rectangle.index;
                    bestAreaFit = areaFit;
                    bestShortSideFit = shortSideFit;
                }
            }

            if (rotationsAllowed) {
                // check to see if the rotated rectangle fits into the empty space
                if (freeRectangles.get(i).width >= rectangle.height &&
                        freeRectangles.get(i).height >= rectangle.width) {
                    int leftoverHorizontal = Math.abs(freeRectangles.get(i).width - rectangle.height);
                    int leftoverVertical = Math.abs(freeRectangles.get(i).height - rectangle.width);
                    int shortSideFit = Math.min(leftoverHorizontal, leftoverVertical);

                    if (areaFit < bestAreaFit || (areaFit == bestAreaFit && shortSideFit < bestShortSideFit)) {
                        bestRect.x = freeRectangles.get(i).x;
                        bestRect.y = freeRectangles.get(i).y;
                        bestRect.width = rectangle.height;
                        bestRect.height = rectangle.width;
                        bestRect.index = rectangle.index;
                        bestAreaFit = areaFit;
                        bestShortSideFit = shortSideFit;
                    }
                }
            }
        }

        // update the return value with the rectangle position and dimension + rectangle placement scores
        answer.rectangle = bestRect;
        answer.score1 = bestAreaFit;
        answer.score2 = bestShortSideFit;

        return answer;
    }

    /**
     * Position the rectangle Tetris style - i.e. find the bin for which the gap between the top of the rectangle
     * and the top of the bin is the smallest possible.
     *
     * @param rectangle the given rectangle
     * @return a copy of the argument with updated (x, y) coordinates and two scores
     *          based on the x and, respectively, they y position
     */
    @SuppressWarnings("Duplicates")
    private RectangleAndScoreReturn FindPositionBottomLeftRule(Rectangle rectangle) {
        RectangleAndScoreReturn answer = new RectangleAndScoreReturn();

        Rectangle bestRect = new Rectangle();
        int bestY = Integer.MAX_VALUE;
        int bestX = Integer.MAX_VALUE;

        for (int i = 0; i < freeRectangles.size(); i++) {
            // check to see if the rectangle fits into the empty space
            if (freeRectangles.get(i).width >= rectangle.width &&
                    freeRectangles.get(i).height >= rectangle.height) {
                int topSideY = freeRectangles.get(i).y + rectangle.height;
                if (topSideY < bestY || (topSideY == bestY && freeRectangles.get(i).x < bestX)) {
                    bestRect.x = freeRectangles.get(i).x;
                    bestRect.y = freeRectangles.get(i).y;
                    bestRect.width = rectangle.width;
                    bestRect.height = rectangle.height;
                    bestRect.index = rectangle.index;
                    bestY = topSideY;
                    bestX = freeRectangles.get(i).x;
                }
            }

            if (rotationsAllowed) {
                // check to see if the rotated rectangle fits into the empty space
                if (freeRectangles.get(i).width >= rectangle.height &&
                        freeRectangles.get(i).height >= rectangle.width) {
                    int topSideY = freeRectangles.get(i).y + rectangle.width;
                    if (topSideY < bestY || (topSideY == bestY && freeRectangles.get(i).x < bestX)) {
                        bestRect.x = freeRectangles.get(i).x;
                        bestRect.y = freeRectangles.get(i).y;
                        bestRect.width = rectangle.height;
                        bestRect.height = rectangle.width;
                        bestRect.index = rectangle.index;
                        bestY = topSideY;
                        bestX = freeRectangles.get(i).x;
                    }
                }
            }
        }

        // update the return value with the rectangle position and dimension + rectangle placement scores
        answer.rectangle = bestRect;
        answer.score1 = bestY;
        answer.score2 = bestX;

        return answer;
    }

    /**
     * Choose the placement where the rectangle touches other rectangles as much as possible.
     *
     * @param rectangle the given rectangle
     * @return a copy of the argument with updated (x, y) coordinates and one score based on the "contact" the given
     *          rectangle has (based on how many other rectangles it touches).
     */
    @SuppressWarnings("Duplicates")
    private RectangleAndScoreReturn FindPositionContactPointRule(Rectangle rectangle) {
        RectangleAndScoreReturn answer = new RectangleAndScoreReturn();

        Rectangle bestRect = new Rectangle();
        int bestScore = -1;

        for (int i = 0; i < freeRectangles.size(); i++) {
            // check to see if the rectangle fits into the empty space
            if (freeRectangles.get(i).width >= rectangle.width &&
                    freeRectangles.get(i).height >= rectangle.height) {
                int score = ContactPointRectScore(freeRectangles.get(i).x, freeRectangles.get(i).y,
                        rectangle.width, rectangle.height);

                if (score > bestScore) {
                    bestRect.x = freeRectangles.get(i).x;
                    bestRect.y = freeRectangles.get(i).y;
                    bestRect.width = rectangle.width;
                    bestRect.height = rectangle.height;
                    bestRect.index = rectangle.index;
                    bestScore = score;
                }
            }

            if (rotationsAllowed) {
                // check to see if the rotated rectangle fits into the empty space
                if (freeRectangles.get(i).width >= rectangle.height &&
                        freeRectangles.get(i).height >= rectangle.width) {
                    int score = ContactPointRectScore(freeRectangles.get(i).x, freeRectangles.get(i).y,
                            rectangle.height, rectangle.width);

                    if (score > bestScore) {
                        bestRect.x = freeRectangles.get(i).x;
                        bestRect.y = freeRectangles.get(i).y;
                        bestRect.width = rectangle.height;
                        bestRect.height = rectangle.width;
                        bestRect.index = rectangle.index;
                        bestScore = score;
                    }
                }
            }
        }

        // update the return value with the rectangle position and dimension + rectangle placement scores
        answer.rectangle = bestRect;
        answer.score1 = bestScore;

        return answer;
    }

    /**
     * Compute the contact score of a rectangle.
     *
     * @param x the x-coordinate of the rectangle to consider
     * @param y the y-coordinate of the rectangle to consider
     * @param width the width of the rectangle to consider
     * @param height the height of the rectangle to consider
     * @return the contact score of the rectangle
     */
    private int ContactPointRectScore(int x, int y, int width, int height) {
        int score = 0;

        if (x == 0 || x + width == binWidth) {
            score += height;
        }

        if (y == 0 || y + height == binHeight) {
            score += width;
        }

        // TODO review and understand things here
        for (int i = 0; i < usedRectangles.size(); i++) {
            if (usedRectangles.get(i).x == x + width || usedRectangles.get(i).x + usedRectangles.get(i).width == x) {
                score += CommonIntervalLength(usedRectangles.get(i).y,
                        usedRectangles.get(i).y + usedRectangles.get(i).height,
                        y,
                        y + height);
            }

            if (usedRectangles.get(i).y == y + height || usedRectangles.get(i).y + usedRectangles.get(i).height == y) {
                score += CommonIntervalLength(usedRectangles.get(i).x,
                        usedRectangles.get(i).x + usedRectangles.get(i).width,
                        x,
                        x + width);
            }
        }

        return score;
    }

    /**
     * Compute the length of the overlapping interval for two given intervals.
     *
     * @param interval1Start the start index of the first interval
     * @param interval1End the end index of the first interval
     * @param interval2Start the start index of the second interval
     * @param interval2End the end index of the second interval
     * @return the length of the overlap between the two given intervals
     */
    private int CommonIntervalLength(int interval1Start, int interval1End, int interval2Start, int interval2End) {
        if (interval1End < interval2Start || interval2End < interval1Start) {
            return 0;
        }

        return Math.min(interval1End, interval2End) - Math.max(interval1Start, interval2Start);
    }

    /**
     * Class used in order to get the two heuristic scores
     * as well as the placed rectangle from the heuristic functions.
     * (bypass since Java does not support passing integers by reference)
     *
     * scores used as follows:
     *
     *      if (first.score1 < second.score1 || (first.score1 == second.score1 && first.score2 < second.score2) {
     *          ....
     *      }
     */
    private class RectangleAndScoreReturn {
        public Rectangle rectangle;
        public int score1;
        public int score2;

        public RectangleAndScoreReturn() {
            this.rectangle = null;
            this.score1 = Integer.MAX_VALUE;
            this.score2 = Integer.MAX_VALUE;
        }
    }
}