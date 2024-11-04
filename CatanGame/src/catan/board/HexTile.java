package catan.board;

public class HexTile {
    private String resourceType;  // Type of resource (wood, brick, sheep, wheat, ore, or desert)
    private int numberToken;      // Number token assigned to this tile (0 if it's a desert)

    public HexTile(String resourceType, int numberToken) {
        this.resourceType = resourceType;
        this.numberToken = numberToken;
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
