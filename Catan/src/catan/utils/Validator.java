package catan.utils;

import catan.board.Board;
import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Road;
import catan.players.Player;

import java.util.List;

/**
 * Validates core game rules.
 *
 * This is intentionally "rule-only": it does not mutate game state.
 */
public class Validator {

    /**
     * Settlement placement rules.
     *
     * Setup phase:
     * - Intersection must be empty
     * - Distance rule: no adjacent intersections may be occupied
     *
     * Normal phase:
     * - Same as setup
     * - Must connect to player's existing road network
     */
    public static boolean isValidSettlementPlacement(Board board, Player player, Intersection intersection, boolean isSetupPhase) {
        if (board == null || player == null || intersection == null) return false;
        if (board.isIntersectionOccupied(intersection)) return false;

        // Distance rule: adjacent intersections must be empty
        for (Intersection adj : board.getAdjacentIntersections(intersection)) {
            if (board.isIntersectionOccupied(adj)) return false;
        }

        if (isSetupPhase) {
            return true;
        }

        // Normal rule: settlement must connect to player's road network
        return isIntersectionConnectedToPlayerNetwork(board, player, intersection);
    }

    /**
     * Normal road placement rules:
     * - Edge must be empty
     * - Road must connect to player's existing network (road or settlement/city)
     */
    public static boolean isValidRoadPlacement(Board board, Player player, Edge edge) {
        if (board == null || player == null || edge == null) return false;
        if (board.isEdgeOccupied(edge)) return false;

        // Connects to player's network via either endpoint
        return isIntersectionConnectedToPlayerNetwork(board, player, edge.getStart())
                || isIntersectionConnectedToPlayerNetwork(board, player, edge.getEnd());
    }

    /**
     * Setup road placement rules:
     * - Edge must be empty
     * - Must touch the anchor (the settlement just placed this setup turn)
     */
    public static boolean isValidSetupRoadPlacement(Board board, Player player, Edge edge, Intersection anchor) {
        if (board == null || player == null || edge == null || anchor == null) return false;
        if (board.isEdgeOccupied(edge)) return false;

        // Must touch the anchor intersection
        return edge.getStart().equals(anchor) || edge.getEnd().equals(anchor);
    }

    /**
     * Trade amounts.
     */
    public static boolean isValidTrade(int offerAmount, int requestAmount) {
        return offerAmount > 0 && requestAmount > 0;
    }

    // -------------------- Helpers --------------------

    private static boolean isIntersectionConnectedToPlayerNetwork(Board board, Player player, Intersection intersection) {
        // If player already has a settlement/city here (shouldn't happen for new placement)
        if (playerOwnsBuildingAtIntersection(player, intersection)) return true;

        // If any adjacent edge has a player's road, it's connected
        List<Edge> touching = board.getEdgesTouching(intersection);
        for (Edge e : touching) {
            Road r = board.getRoadAt(e);
            if (r != null && r.getOwner() == player) return true;
        }
        return false;
    }

    private static boolean playerOwnsBuildingAtIntersection(Player player, Intersection intersection) {
        // Player stores settlements/cities lists.
        return player.getSettlements().stream().anyMatch(s -> s.getLocation().equals(intersection))
                || player.getCities().stream().anyMatch(c -> c.getLocation().equals(intersection));
    }
}
