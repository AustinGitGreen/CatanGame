package catan.board;

import catan.resources.Resource;
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
        ports = new ArrayList<>();
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
        addResourceTiles(Resource.WOOD, 4);
        addResourceTiles(Resource.BRICK, 3);
        addResourceTiles(Resource.SHEEP, 4);
        addResourceTiles(Resource.WHEAT, 4);
        addResourceTiles(Resource.ORE, 3);
        
        // Add the desert tile with no number token
        desertTile = new HexTile(Resource.DESERT, 0);
        hexTiles.add(desertTile);
        
        // Verify tile count (should be 19)
        if (hexTiles.size() != 19) {
            System.err.println("Error: Expected 19 tiles, but found " + hexTiles.size());
        }

        // Assign number tokens to each tile (excluding the desert)
        for (HexTile tile : hexTiles) {
            if (tile.getResourceType() != Resource.DESERT && !numberTokens.isEmpty()) {
                tile.setNumberToken(numberTokens.remove(0));
            }
        }

        // Shuffle hex tiles to randomize their placement on the board
        Collections.shuffle(hexTiles);
    }

    // Helper method to add resource tiles of a specific type and count
    private void addResourceTiles(Resource resourceType, int count) {
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

    // Optional: Method to retrieve tiles by resource type, useful for testing
    public List<HexTile> getTilesByResource(Resource resourceType) {
        List<HexTile> result = new ArrayList<>();
        for (HexTile tile : hexTiles) {
            if (tile.getResourceType() == resourceType) {
                result.add(tile);
            }
        }
        return result;
    }
}
