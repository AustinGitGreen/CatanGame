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
        initializeTileAdjacency(); // Initialize adjacency after shuffling tiles
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
        // Define the resource tiles: 4 wood, 3 brick, 4 sheep, 4 wheat, 3 ore, 1 desert
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

    // Method to retrieve tiles by resource type, useful for testing
    public List<HexTile> getTilesByResource(Resource resourceType) {
        List<HexTile> result = new ArrayList<>();
        for (HexTile tile : hexTiles) {
            if (tile.getResourceType() == resourceType) {
                result.add(tile);
            }
        }
        return result;
    }

    // Initializes adjacency relationships for tiles based on a 3-4-5-4-3 layout
    private void initializeTileAdjacency() {
        // Define a fixed hexagonal grid layout (3-4-5-4-3 pattern)
        int[] layout = {3, 4, 5, 4, 3};
        int index = 0;

        List<List<HexTile>> rows = new ArrayList<>();

        // Split tiles into rows based on the layout
        for (int rowSize : layout) {
            List<HexTile> row = new ArrayList<>();
            for (int i = 0; i < rowSize; i++) {
                row.add(hexTiles.get(index++));
            }
            rows.add(row);
        }

        // Define adjacency relationships
        for (int r = 0; r < rows.size(); r++) {
            List<HexTile> currentRow = rows.get(r);
            List<HexTile> aboveRow = (r > 0) ? rows.get(r - 1) : null;
            List<HexTile> belowRow = (r < rows.size() - 1) ? rows.get(r + 1) : null;

            for (int c = 0; c < currentRow.size(); c++) {
                HexTile currentTile = currentRow.get(c);

                // Add adjacent tiles in the current row
                if (c > 0) currentTile.addAdjacentTile(currentRow.get(c - 1));
                if (c < currentRow.size() - 1) currentTile.addAdjacentTile(currentRow.get(c + 1));

                // Add adjacent tiles from the row above
                if (aboveRow != null) {
                    if (c < aboveRow.size()) currentTile.addAdjacentTile(aboveRow.get(c));
                    if (c > 0) currentTile.addAdjacentTile(aboveRow.get(c - 1));
                }

                // Add adjacent tiles from the row below
                if (belowRow != null) {
                    if (c < belowRow.size()) currentTile.addAdjacentTile(belowRow.get(c));
                    if (c > 0) currentTile.addAdjacentTile(belowRow.get(c - 1));
                }
            }
        }
    }
}
