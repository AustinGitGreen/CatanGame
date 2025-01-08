package catan.board;

/**
 * Represents an intersection on the game board.
 */
public class Intersection {
    private int x;
    private int y;

    /**
     * Constructs an Intersection with x and y coordinates.
     * @param x The x-coordinate of the intersection.
     * @param y The y-coordinate of the intersection.
     */
    public Intersection(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the intersection.
     * @return The x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the intersection.
     * @return The y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Checks if two intersections are equal based on their coordinates.
     * @param obj The object to compare.
     * @return True if the intersections have the same coordinates, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Intersection that = (Intersection) obj;
        return x == that.x && y == that.y;
    }

    /**
     * Generates a hash code based on the coordinates of the intersection.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        int result = Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        return result;
    }
}