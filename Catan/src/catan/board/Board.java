package catan.board;

import catan.components.City;
import catan.components.Road;
import catan.components.Settlement;
import catan.resources.Resource;
import catan.resources.Robber;

import java.util.*;

/**
 * Represents the game board, including hexes, intersections, edges, and the robber.
 *
 * This board generator creates a classic 19-hex Catan topology (3-4-5-4-3 layout)
 * resulting in:
 * - 19 hexes
 * - 54 intersections
 * - 72 edges
 */
public class Board {
    private List<Hex> hexes;
    private List<Intersection> intersections;
    private List<Edge> edges;
    private Robber robber;

    // Track robber location explicitly (so game logic doesn't depend on Robber's API)
    private Hex robberHex;

    // Occupancy tracking (recommended for validation + resource distribution later)
    private final Map<Intersection, Settlement> settlementByIntersection = new HashMap<>();
    private final Map<Intersection, City> cityByIntersection = new HashMap<>();
    private final Map<String, Road> roadByEdgeKey = new HashMap<>();

    // Helpful adjacency indexes
    private final Map<Intersection, List<Edge>> edgesTouchingIntersection = new HashMap<>();
    private final Map<Intersection, List<Intersection>> adjacentIntersections = new HashMap<>();

    // Hex -> 6 corner intersections, used for resource payout
    private final Map<Hex, List<Intersection>> hexCorners = new HashMap<>();

    // Intersection -> list of touching hexes (for setup starting resources)
    private final Map<Intersection, List<Hex>> hexesTouchingIntersection = new HashMap<>();

    // Quantization to keep shared corners identical.
    private static final double EPS = 1e-6;

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
        hexCorners.clear();
        hexesTouchingIntersection.clear();
        robber = null;
        robberHex = null;

        settlementByIntersection.clear();
        cityByIntersection.clear();
        roadByEdgeKey.clear();
        edgesTouchingIntersection.clear();
        adjacentIntersections.clear();

        initializeHexes();
        initializeIntersectionsAndEdgesFromHexLayout();
        indexAdjacency();
        initializeRobber();
    }

    /**
     * Initializes the hexes on the board.
     * (Resource/number assignment can be randomized later.)
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
     * Generates intersections and edges from a standard 3-4-5-4-3 hex layout.
     */
    private void initializeIntersectionsAndEdgesFromHexLayout() {
        Map<String, Intersection> cornerToIntersection = new HashMap<>();
        Set<String> edgeKeys = new HashSet<>();

        List<int[]> axialHexes = getStandardAxialHexCoordinates();
        if (axialHexes.size() != hexes.size()) {
            throw new IllegalStateException("Hex coordinate count mismatch: " + axialHexes.size() + " vs " + hexes.size());
        }

        for (int i = 0; i < axialHexes.size(); i++) {
            int q = axialHexes.get(i)[0];
            int r = axialHexes.get(i)[1];
            Hex hex = hexes.get(i);

            double[] center = axialToPixel(q, r);
            List<Intersection> corners = new ArrayList<>(6);

            for (int c = 0; c < 6; c++) {
                double angleRad = Math.toRadians(60.0 * c - 30.0);
                double px = center[0] + Math.cos(angleRad);
                double py = center[1] + Math.sin(angleRad);

                String key = quantizedKey(px, py);
                Intersection corner = cornerToIntersection.get(key);
                if (corner == null) {
                    int ix = quantizeToInt(px);
                    int iy = quantizeToInt(py);
                    corner = new Intersection(ix, iy);
                    cornerToIntersection.put(key, corner);
                }
                corners.add(corner);
            }

            hexCorners.put(hex, corners);

            // index intersection -> touching hexes
            for (Intersection corner : corners) {
                hexesTouchingIntersection.computeIfAbsent(corner, k -> new ArrayList<>()).add(hex);
            }

            // Add 6 perimeter edges around the hex, globally deduped
            for (int c = 0; c < 6; c++) {
                Intersection a = corners.get(c);
                Intersection b = corners.get((c + 1) % 6);
                String eKey = edgeKey(a, b);
                if (edgeKeys.add(eKey)) {
                    edges.add(new Edge(a, b));
                }
            }
        }

        intersections.addAll(cornerToIntersection.values());

        // Classic Catan sanity checks
        if (hexes.size() != 19) throw new IllegalStateException("Expected 19 hexes, got " + hexes.size());
        if (intersections.size() != 54) throw new IllegalStateException("Expected 54 intersections, got " + intersections.size());
        if (edges.size() != 72) throw new IllegalStateException("Expected 72 edges, got " + edges.size());
    }

    private void indexAdjacency() {
        for (Intersection i : intersections) {
            edgesTouchingIntersection.put(i, new ArrayList<>());
            adjacentIntersections.put(i, new ArrayList<>());
        }

        for (Edge e : edges) {
            Intersection a = e.getStart();
            Intersection b = e.getEnd();

            edgesTouchingIntersection.get(a).add(e);
            edgesTouchingIntersection.get(b).add(e);

            adjacentIntersections.get(a).add(b);
            adjacentIntersections.get(b).add(a);
        }
    }

    private List<int[]> getStandardAxialHexCoordinates() {
        List<int[]> coords = new ArrayList<>(19);

        coords.add(new int[]{0, -2});
        coords.add(new int[]{1, -2});
        coords.add(new int[]{2, -2});

        coords.add(new int[]{-1, -1});
        coords.add(new int[]{0, -1});
        coords.add(new int[]{1, -1});
        coords.add(new int[]{2, -1});

        coords.add(new int[]{-2, 0});
        coords.add(new int[]{-1, 0});
        coords.add(new int[]{0, 0});
        coords.add(new int[]{1, 0});
        coords.add(new int[]{2, 0});

        coords.add(new int[]{-2, 1});
        coords.add(new int[]{-1, 1});
        coords.add(new int[]{0, 1});
        coords.add(new int[]{1, 1});

        coords.add(new int[]{-2, 2});
        coords.add(new int[]{-1, 2});
        coords.add(new int[]{0, 2});

        return coords;
    }

    private double[] axialToPixel(int q, int r) {
        double x = Math.sqrt(3.0) * (q + r / 2.0);
        double y = 1.5 * r;
        return new double[]{x, y};
    }

    private String quantizedKey(double x, double y) {
        long qx = Math.round(x / EPS);
        long qy = Math.round(y / EPS);
        return qx + "," + qy;
    }

    private int quantizeToInt(double v) {
        return (int) Math.round(v / EPS);
    }

    private String edgeKey(Intersection a, Intersection b) {
        if (compare(a, b) <= 0) return a.getX() + ":" + a.getY() + "|" + b.getX() + ":" + b.getY();
        return b.getX() + ":" + b.getY() + "|" + a.getX() + ":" + a.getY();
    }

    private int compare(Intersection a, Intersection b) {
        if (a.getX() != b.getX()) return Integer.compare(a.getX(), b.getX());
        return Integer.compare(a.getY(), b.getY());
    }

    private void initializeRobber() {
        for (Hex hex : hexes) {
            if (hex.getResource() == Resource.DESERT) {
                robber = new Robber(hex);
                robberHex = hex;
                break;
            }
        }
    }

    /** Moves the robber to the given hex (rebuilds robber instance to avoid relying on Robber API). */
    public void moveRobberTo(Hex hex) {
        if (hex == null) throw new IllegalArgumentException("Hex cannot be null");
        robber = new Robber(hex);
        robberHex = hex;
    }

    /** Returns the hex the robber is currently on (tracked by Board). */
    public Hex getRobberHex() {
        return robberHex;
    }

    // ---------- Occupancy / placement helpers ----------

    public boolean isIntersectionOccupied(Intersection intersection) {
        return settlementByIntersection.containsKey(intersection) || cityByIntersection.containsKey(intersection);
    }

    public Settlement getSettlementAt(Intersection intersection) {
        return settlementByIntersection.get(intersection);
    }

    public City getCityAt(Intersection intersection) {
        return cityByIntersection.get(intersection);
    }

    public void placeSettlement(Settlement settlement) {
        if (settlement == null) throw new IllegalArgumentException("Settlement cannot be null.");
        Intersection loc = settlement.getLocation();
        if (isIntersectionOccupied(loc)) throw new IllegalArgumentException("Intersection already occupied.");
        settlementByIntersection.put(loc, settlement);
    }

    public boolean isEdgeOccupied(Edge edge) {
        return roadByEdgeKey.containsKey(edgeKey(edge.getStart(), edge.getEnd()));
    }

    public Road getRoadAt(Edge edge) {
        return roadByEdgeKey.get(edgeKey(edge.getStart(), edge.getEnd()));
    }

    public void placeRoad(Road road) {
        if (road == null) throw new IllegalArgumentException("Road cannot be null.");
        Edge e = road.getEdge();
        String key = edgeKey(e.getStart(), e.getEnd());
        if (roadByEdgeKey.containsKey(key)) throw new IllegalArgumentException("Edge already has a road.");
        roadByEdgeKey.put(key, road);
    }

    // ---------- Adjacency helpers (for Validator) ----------

    public List<Edge> getEdgesTouching(Intersection intersection) {
        return edgesTouchingIntersection.getOrDefault(intersection, Collections.emptyList());
    }

    public List<Intersection> getAdjacentIntersections(Intersection intersection) {
        return adjacentIntersections.getOrDefault(intersection, Collections.emptyList());
    }

    // ---------- Getters ----------

    public List<Hex> getHexes() {
        return hexes;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Robber getRobber() {
        return robber;
    }

    public List<Intersection> getCornersForHex(Hex hex) {
        return hexCorners.getOrDefault(hex, Collections.emptyList());
    }

    public List<Hex> getHexesTouchingIntersection(Intersection intersection) {
        return hexesTouchingIntersection.getOrDefault(intersection, Collections.emptyList());
    }
}
