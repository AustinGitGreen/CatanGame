package catan.board;

import resources.Resource;
import resources.Robber;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board, including hexes, intersections, edges, and the robber.
 */
public class Board {
    private List<Hex> hexes;
    private List<Intersection> intersections;
    private List<Edge> edges;
    private Robber robber;

    /**
     * Constructs a new game board.
     */
    public Board() {
        hexes = new ArrayList<>();
        intersections = new ArrayList<>();
        edges = new ArrayList<>();
    }

    /**
     * Initializes the game board with hexes, intersections, edges, and the robber.
     */
    public void initializeBoard() {
        initializeHexes();
        initializeIntersections();
        initializeEdges();
        initializeRobber();
    }

    /**
     * Initializes the hexes on the board.
     */
    private void initializeHexes() {
        // Add 19 hexes, including one desert tile
        hexes.add(new Hex(Resource.DESERT, 0));
        hexes.add(new Hex(Resource.WOOD, 8));
        hexes.add(new Hex(Resource.WOOD, 4));
        hexes.add(new Hex(Resource.WOOD, 11));
        hexes.add(new Hex(Resource.WOOD, 3));
        hexes.add(new Hex(Resource.BRICK, 6));
        hexes.add(new Hex(Resource.BRICK, 5));
        hexes.add(new Hex(Resource.BRICK, 9));
        hexes.add(new Hex(Resource.SHEEP, 2));
        hexes.add(new Hex(Resource.SHEEP, 10));
        hexes.add(new Hex(Resource.SHEEP, 12));
        hexes.add(new Hex(Resource.SHEEP, 9));
        hexes.add(new Hex(Resource.WHEAT, 8));
        hexes.add(new Hex(Resource.WHEAT, 10));
        hexes.add(new Hex(Resource.WHEAT, 11));
        hexes.add(new Hex(Resource.ORE, 3));
        hexes.add(new Hex(Resource.ORE, 4));
        hexes.add(new Hex(Resource.ORE, 5));
        hexes.add(new Hex(Resource.WHEAT, 6));
    }

    /**
     * Initializes the intersections on the board.
     */
    private void initializeIntersections() {
        // Example: Add 54 intersections
        for (int i = 0; i < 54; i++) {
            intersections.add(new Intersection(i / 10, i % 10));
        }
    }

    /**
     * Initializes the edges on the board.
     */
    private void initializeEdges() {
        // Add edges between intersections within the hexes
        for (int i = 0; i < intersections.size() - 1; i++) {
            edges.add(new Edge(intersections.get(i), intersections.get(i + 1)));
        }

        // Add edges for the outside perimeter
        addOutsideEdges();
    }
    
    private void addOutsideEdges() {
        // Example: Logic to add edges for the perimeter
        // Adjust logic based on the structure of your intersections
        for (int i = 0; i < intersections.size(); i++) {
            // Connect each perimeter intersection to its next neighbor
            // Assuming you have logic to identify perimeter intersections
            Intersection start = intersections.get(i);
            Intersection end = getNextPerimeterIntersection(start); // Define this helper method
            if (end != null) {
                edges.add(new Edge(start, end));
            }
        }
    }
    
    /**
     * Gets the next perimeter intersection for the given intersection.
     * This method assumes the board is structured in a hexagonal layout.
     * @param intersection The current intersection.
     * @return The next perimeter intersection, or null if not applicable.
     */
    private Intersection getNextPerimeterIntersection(Intersection intersection) {
        // Example logic to find the next perimeter intersection based on coordinates
        int x = intersection.getX();
        int y = intersection.getY();

        // Adjust logic based on your actual board layout
        if (x == 0 && y < 5) {
            return new Intersection(x, y + 1); // Move right along the top row
        } else if (y == 5 && x < 5) {
            return new Intersection(x + 1, y); // Move down along the right column
        } else if (x == 5 && y > 0) {
            return new Intersection(x, y - 1); // Move left along the bottom row
        } else if (y == 0 && x > 0) {
            return new Intersection(x - 1, y); // Move up along the left column
        }

        return null; // No next perimeter intersection
    }

    /**
     * Initializes the robber on the desert hex.
     */
    private void initializeRobber() {
        for (Hex hex : hexes) {
            if (hex.getResource() == Resource.DESERT) {
                robber = new Robber(hex);
                break;
            }
        }
    }

    /**
     * Gets the list of hexes on the board.
     * @return The list of hexes.
     */
    public List<Hex> getHexes() {
        return hexes;
    }

    /**
     * Gets the list of intersections on the board.
     * @return The list of intersections.
     */
    public List<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Gets the list of edges on the board.
     * @return The list of edges.
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Gets the robber on the board.
     * @return The robber.
     */
    public Robber getRobber() {
        return robber;
    }
}