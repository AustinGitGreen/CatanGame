package catan.board;

import catan.players.Player;
import java.util.HashSet;
import java.util.Set;

public class HexTile {
    private String resourceType;  // Type of resource (wood, brick, sheep, wheat, ore, or desert)
    private int numberToken;      // Number token assigned to this tile (0 if it's a desert)
    private Set<Player> settlementOwners; // Tracks players who have settlements on this tile

    public HexTile(String resourceType, int numberToken) {
        this.resourceType = resourceType;
        this.numberToken = numberToken;
        this.settlementOwners = new HashSet<>();
    }

    // Get the type of resource produced by this tile
    public String getResourceType() {
        return resourceType;
    }

    // Get the number token assigned to this tile
    public int getNumberToken() {
        return numberToken;
    }

    // Set the number token for this tile (used during board setup)
    public void setNumberToken(int numberToken) {
        this.numberToken = numberToken;
    }

    // Check if the tile is a desert
    public boolean isDesert() {
        return "desert".equalsIgnoreCase(resourceType);
    }
    
 // Add a settlement owned by a player
    public void addSettlement(Player player) {
        settlementOwners.add(player);
    }

    // Check if a player has a settlement on this tile
    public boolean hasSettlement(Player player) {
        return settlementOwners.contains(player);
    }

    // Optional: Override toString for easier debugging and printing
    @Override
    public String toString() {
        if (isDesert()) {
            return "HexTile { desert }";
        } else {
            return "HexTile { resourceType='" + resourceType + "', numberToken=" + numberToken + " }";
        }
    }
}
