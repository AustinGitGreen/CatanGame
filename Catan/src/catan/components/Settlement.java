package catan.components;

import catan.players.Player;
import catan.board.Intersection;

/**
 * Represents a settlement built by a player on the game board.
 */
public class Settlement {
    private Player owner;
    private Intersection location;
    private final int victoryPoints;

    /**
     * Constructs a Settlement with an owner and a location.
     * @param owner The player who owns the settlement.
     * @param location The intersection where the settlement is built.
     */
    public Settlement(Player owner, Intersection location) {
        if (owner == null || location == null) {
            throw new IllegalArgumentException("Owner and location cannot be null");
        }
        this.owner = owner;
        this.location = location;
        this.victoryPoints = 1; // Settlements are worth 1 victory point
    }

    /**
     * Gets the owner of the settlement.
     * @return The player who owns the settlement.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Gets the location of the settlement.
     * @return The intersection where the settlement is built.
     */
    public Intersection getLocation() {
        return location;
    }

    /**
     * Gets the victory points awarded by the settlement.
     * @return The number of victory points (always 1).
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }
}
