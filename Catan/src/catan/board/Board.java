package catan.board;

import catan.resources.Resource;
import catan.resources.Robber;

import java.util.*;

/**
 * Represents the game board, including hexes, intersections, edges, and the robber.
 *
 * This implementation generates a LEGAL classic Catan topology:
 * - 19 hexes (3-4-5-4-3 layout)
 * - 54 intersections
 * - 72 edges
 *
 * It does NOT create a rectangular grid or wrap-around edges.
 */
public class Board {
    private List<Hex> hexes;
    private List<Intersection> intersections;
    private List<Edge> edges;
    private Robber robber;

    /**
     * Optional: adjacency data for future resource distribution.
     * Maps each Hex to its 6 corner intersections.
     */
    private Map<Hex, List<Intersection>> hexCorners;

    // Quantization tolerance for matching shared corners across neighboring hexes.
    private static final double EPS = 1e-6;

    public Board() {
        hexes = new ArrayList<>();
        intersections = new ArrayList<>();
        edges = new ArrayList<>();
        hexCorners = new HashMap<>();
    }

    public void initializeBoard() {
        hexes.clear();
        intersections.clear();
        edges.clear();
        hexCorners.clear();
        robber = null;

        initializeHexes();
        initializeIntersectionsAndEdgesFromHexLayout();
        initializeRobber();
    }

    /**
     * Initializes the hexes on the board.
     * (Resource/number assignment can be randomized later.)
     */
    private void initializeHexes() {
        // 19 hexes, including one desert tile
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
     *
     * Approach:
     * 1) Place 19 hex centers using axial coordinates (q,r) in rows: 3,4,5,4,3
     * 2) Convert each hex center to 2D "pixel" coords (pointy-top)
     * 3) Compute 6 corner points for each hex
     * 4) Quantize corner points so shared corners become the same Intersection object
     * 5) Add edges around each hex, deduped globally
     */
    private void initializeIntersectionsAndEdgesFromHexLayout() {
        // Map quantized corner-key -> Intersection (so neighboring hexes share the same corners)
        Map<String, Intersection> cornerToIntersection = new HashMap<>();

        // Deduplicate edges globally
        Set<String> edgeKeys = new HashSet<>();

        // Axial coordinates for classic 3-4-5-4-3 layout (pointy-top)
        List<int[]> axialHexes = getStandardAxialHexCoordinates();

        // Corner angles for pointy-top hexes (Red Blob Games convention)
        // angle = 60*i - 30 degrees
        for (int i = 0; i < axialHexes.size(); i++) {
            int q = axialHexes.get(i)[0];
            int r = axialHexes.get(i)[1];

            // Keep 1-to-1 with your hex list order:
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
                    // Store quantized integer coords inside Intersection (stable equality)
                    int ix = quantizeToInt(px);
                    int iy = quantizeToInt(py);
                    corner = new Intersection(ix, iy);
                    cornerToIntersection.put(key, corner);
                }

                corners.add(corner);
            }

            // Save for future: resource distribution & robber blocking logic
            hexCorners.put(hex, corners);

            // Add 6 perimeter edges around this hex (deduped)
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

        // Safety check: classic Catan should land here
        if (hexes.size() != 19) {
            throw new IllegalStateException("Expected 19 hexes, got " + hexes.size());
        }
        if (intersections.size() != 54) {
            throw new IllegalStateException("Expected 54 intersections, got " + intersections.size());
        }
        if (edges.size() != 72) {
            throw new IllegalStateException("Expected 72 edges, got " + edges.size());
        }
    }

    /**
     * Returns axial coordinates (q,r) for the 19-hex classic layout:
     * rows: 3,4,5,4,3 (pointy-top axial)
     *
     * This ordering intentionally matches the index order 0..18 so it lines up with your hex list.
     */
    private List<int[]> getStandardAxialHexCoordinates() {
        List<int[]> coords = new ArrayList<>(19);

        // r = -2 has 3 hexes: q = 0..2
        coords.add(new int[]{0, -2});
        coords.add(new int[]{1, -2});
        coords.add(new int[]{2, -2});

        // r = -1 has 4 hexes: q = -1..2
        coords.add(new int[]{-1, -1});
        coords.add(new int[]{0, -1});
        coords.add(new int[]{1, -1});
        coords.add(new int[]{2, -1});

        // r = 0 has 5 hexes: q = -2..2
        coords.add(new int[]{-2, 0});
        coords.add(new int[]{-1, 0});
        coords.add(new int[]{0, 0});
        coords.add(new int[]{1, 0});
        coords.add(new int[]{2, 0});

        // r = 1 has 4 hexes: q = -2..1
        coords.add(new int[]{-2, 1});
        coords.add(new int[]{-1, 1});
        coords.add(new int[]{0, 1});
        coords.add(new int[]{1, 1});

        // r = 2 has 3 hexes: q = -2..0
        coords.add(new int[]{-2, 2});
        coords.add(new int[]{-1, 2});
        coords.add(new int[]{0, 2});

        return coords;
    }

    /**
     * Converts axial hex coordinates (q,r) to 2D coordinates for pointy-top hexes.
     * size = 1.0
     *
     * x = sqrt(3) * (q + r/2)
     * y = 3/2 * r
     */
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
        // Store the quantized integer "grid coordinate" so equals/hashCode work reliably.
        return (int) Math.round(v / EPS);
    }

    private String edgeKey(Intersection a, Intersection b) {
        // Order endpoints so A-B and B-A dedupe correctly
        if (compare(a, b) <= 0) {
            return a.getX() + ":" + a.getY() + "|" + b.getX() + ":" + b.getY();
        } else {
            return b.getX() + ":" + b.getY() + "|" + a.getX() + ":" + a.getY();
        }
    }

    private int compare(Intersection a, Intersection b) {
        if (a.getX() != b.getX()) return Integer.compare(a.getX(), b.getX());
        return Integer.compare(a.getY(), b.getY());
    }

    private void initializeRobber() {
        for (Hex hex : hexes) {
            if (hex.getResource() == Resource.DESERT) {
                robber = new Robber(hex);
                break;
            }
        }
    }

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

    /**
     * Optional helper for later: which intersections touch a given hex.
     * Useful for "roll -> distribute resources".
     */
    public List<Intersection> getCornersForHex(Hex hex) {
        return hexCorners.getOrDefault(hex, Collections.emptyList());
    }
}
