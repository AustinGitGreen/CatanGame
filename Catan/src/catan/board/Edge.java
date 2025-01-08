package catan.board;

/**
 * Represents an edge on the game board connecting two intersections.
 */
public class Edge {
    private Intersection start;
    private Intersection end;

    /**
     * Constructs an Edge with a start and end intersection.
     * @param start The starting intersection of the edge.
     * @param end The ending intersection of the edge.
     */
    public Edge(Intersection start, Intersection end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end intersections cannot be null");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Gets the starting intersection of the edge.
     * @return The starting intersection.
     */
    public Intersection getStart() {
        return start;
    }

    /**
     * Gets the ending intersection of the edge.
     * @return The ending intersection.
     */
    public Intersection getEnd() {
        return end;
    }
}

