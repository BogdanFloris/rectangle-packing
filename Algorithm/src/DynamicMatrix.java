/**
 * A class made to keep track of the occupied space in the enclosing rectangle.
 * It is used to search for a space where you can place the given rectangle
 * in the enclosing rectangle
 */
public class DynamicMatrix {

    /**
     * Class Cell representing a cell in the dynamic matrix
     * with size of the cell and whether or not the cell is occupied
     */
    public class Cell {

        private int width;
        private int height;
        private boolean occupied;

        public Cell (int width, int height) {
            this.width = width;
            this.height = height;
            occupied = false;
        }

        public void occupy() {
            this.occupied = true;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean isOccupied() {
            return occupied;
        }
    }

    private int nrOfRows = 1; // initial number of rows
    private int nrOfColumns = 1; // initial number of columns
    private Cell[][] matrix;

    /**
     * Construct the Dynamic Matrix with only a single Cell
     * with the dimensions of the enclosing rectangle
     */
    public DynamicMatrix(int width, int height) {
        matrix = new Cell[nrOfRows][nrOfColumns];
        matrix[0][0] = new Cell(width, height);
    }

    public void addRectangle(int width, int height) {
        int[] foundPosition = findPosition(width, height);
        int rowIndex = foundPosition[0];
        int colIndex = foundPosition[1];
        if (width < matrix[rowIndex][colIndex].getWidth() &&
                height < matrix[rowIndex][colIndex].getHeight()) {
            nrOfRows++;
            nrOfColumns++;
        }
    }

    /**
     * Finds a the first empty position in the matrix where we can place the rectangle
     * Returns null if no position was found
     */
    public int[] findPosition(int width, int height) {
        int rowIndex;
        int colIndex;
        for (colIndex = 0; colIndex < nrOfColumns; colIndex++) {
            for (rowIndex = 0; rowIndex < nrOfRows; rowIndex++) {
                if (height <= matrix[rowIndex][colIndex].getHeight() &&
                        width <= matrix[rowIndex][colIndex].getWidth()) {
                    int[] position = new int[2];
                    position[0] = rowIndex;
                    position[1] = colIndex;
                    return position;
                }
            }
        }
        return null;
    }
}
