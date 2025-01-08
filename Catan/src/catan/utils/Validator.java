package catan.utils;

import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Settlement;
import catan.players.Player;

/**
 * Validator class to validate game rules.
 */
public class Validator {

    /**
     * Validates settlement placement.
     * @param intersection The intersection where the settlement is being placed.
     * @param settlement The existing settlement at the intersection (if any).
     * @return True if the placement is valid, false otherwise.
     */
    public static boolean isValidSettlementPlacement(Intersection intersection, Settlement settlement) {
        return intersection != null && settlement == null; // Valid if intersection exists and is unoccupied
    }

    /**
     * Validates road placement.
     * @param edge The edge where the road is being placed.
     * @param player The player placing the road.
     * @return True if the placement is valid, false otherwise.
     */
    public static boolean isValidRoadPlacement(Edge edge, Player player) {
        return edge != null && player != null; // Basic validation for example
    }

    /**
     * Validates trade amounts.
     * @param offerAmount The amount of resources being offered.
     * @param requestAmount The amount of resources being requested.
     * @return True if the trade is valid, false otherwise.
     */
    public static boolean isValidTrade(int offerAmount, int requestAmount) {
        return offerAmount > 0 && requestAmount > 0; // Both offer and request must be positive
    }
}
