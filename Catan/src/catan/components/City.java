package catan.components;

import catan.players.Player;
import catan.board.Intersection;

/**
 * Represents a city built by a player on the game board.
 */
public class City {
    private Player owner;
    private Intersection location;
    private final int victoryPoints;

    /**
     * Constructs a City with an owner and a location.
     * @param owner The player who owns the city.
     * @param location The intersection where the city is built.
     */
    public City(Player owner, Intersection location) {
        if (owner == null || location == null) {
            throw new IllegalArgumentException("Owner and location cannot be null");
        }
        this.owner = owner;
        this.location = location;
        this.victoryPoints = 2; // Cities are worth 2 victory points
    }

    /**
     * Gets the owner of the city.
     * @return The player who owns the city.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Gets the location of the city.
     * @return The intersection where the city is built.
     */
    public Intersection getLocation() {
        return location;
    }

    /**
     * Gets the victory points awarded by the city.
     * @return The number of victory points (always 2).
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }
}
