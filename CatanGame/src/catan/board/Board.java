package catan.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private List<HexTile> hexTiles;
    private HexTile desertTile;
    private List<Port> ports;
    private List<Integer> numberTokens; // Tokens (2-12 excluding 7) to be assigned to tiles

    public Board() {
        hexTiles = new ArrayList<>();
        numberTokens = new ArrayList<>();
        initializeNumberTokens();
        initializeBoard();
    }

    // Initializes the number tokens (2-12, excluding 7) and shuffles them
    private void initializeNumberTokens() {
        int[] tokens = {2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12};
        for (int token : tokens) {
            numberTokens.add(token);
        }
        Collections.shuffle(numberTokens);
    }

    // Initializes the board with resource types and assigns number tokens
    private void initializeBoard() {
        // Define the resource tiles: 3 wood, 3 brick, 4 sheep, 4 wheat, 3 ore, 1 desert
        addResourceTiles("wood", 3);
        addResourceTiles("brick", 3);
        addResourceTiles("sheep", 4);
        addResourceTiles("wheat", 4);
        addResourceTiles("ore", 3);
        
        // Add the desert tile with no number token
        desertTile = new HexTile("desert", 0);
        hexTiles.add(desertTile);

        // Assign number tokens to each tile (excluding the desert)
        for (HexTile tile : hexTiles) {
            if (!tile.getResourceType().equals("desert") && !numberTokens.isEmpty()) {
                tile.setNumberToken(numberTokens.remove(0));
            }
        }

        // Shuffle hex tiles to randomize their placement on the board
        Collections.shuffle(hexTiles);
    }

    // Helper method to add resource tiles of a specific type and count
    private void addResourceTiles(String resourceType, int count) {
        for (int i = 0; i < count; i++) {
            hexTiles.add(new HexTile(resourceType, 0)); // Number tokens assigned later
        }
    }

    public List<HexTile> getHexTiles() {
        return hexTiles;
    }

    public HexTile getDesertTile() {
        return desertTile;
    }
    
    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    public List<Port> getPorts() {
        return ports;
    }

    // Optional: Method to retrieve a tile by resource type, useful for testing
    public List<HexTile> getTilesByResource(String resourceType) {
        List<HexTile> result = new ArrayList<>();
        for (HexTile tile : hexTiles) {
            if (tile.getResourceType().equals(resourceType)) {
                result.add(tile);
            }
        }
        return result;
    }
}
