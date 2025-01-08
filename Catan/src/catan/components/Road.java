package catan.components;

import catan.board.Edge;
import catan.players.Player;

/**
 * Represents a road built by a player on the game board.
 */
public class Road {
    private Player owner;
    private Edge edge;

    /**
     * Constructs a Road with an owner and an edge.
     * @param owner The player who owns the road.
     * @param edge The edge where the road is built.
     */
    public Road(Player owner, Edge edge) {
        if (owner == null || edge == null) {
            throw new IllegalArgumentException("Owner and edge cannot be null");
        }
        this.owner = owner;
        this.edge = edge;
    }

    /**
     * Gets the owner of the road.
     * @return The player who owns the road.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Gets the edge where the road is built.
     * @return The edge of the road.
     */
    public Edge getEdge() {
        return edge;
    }
}