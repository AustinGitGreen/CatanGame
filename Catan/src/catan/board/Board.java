package catan.board;

import catan.resources.Resource;
import catan.resources.Robber;

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
        hexes.clear();
        intersections.clear();
        edges.clear();
        robber = null;

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
        // Add 54 intersections
        for (int i = 0; i < 54; i++) {
            intersections.add(new Intersection(i / 10, i % 10));
        }
    }

    /**
     * Initializes the edges on the board.
     */
    private void initializeEdges() {
        // Chain connectivity across all intersections.
        for (int i = 0; i < intersections.size() - 1; i++) {
            edges.add(new Edge(intersections.get(i), intersections.get(i + 1)));
        }

        // Close full rows (0-9, 10-19, 20-29, 30-39, 40-49).
        for (int row = 0; row < 5; row++) {
            int rowStart = row * 10;
            int rowEnd = rowStart + 9;
            edges.add(new Edge(intersections.get(rowStart), intersections.get(rowEnd)));
        }

        // Connect the first two rows vertically (0-9 to 10-19).
        for (int col = 0; col < 10; col++) {
            edges.add(new Edge(intersections.get(col), intersections.get(10 + col)));
        }

        // Connect the bottom short row vertically (40-43 to 50-53).
        for (int col = 0; col < 4; col++) {
            edges.add(new Edge(intersections.get(40 + col), intersections.get(50 + col)));
        }
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
