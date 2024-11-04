package catan.main;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Robber;
import catan.players.Player;
import catan.resources.Resource;

import java.util.List;
import java.util.Scanner;

public class GameSetup {
    private Scanner scanner;

    public GameSetup() {
        scanner = new Scanner(System.in); // Used for user input during initial placement
    }

    // Initializes the board with hex tiles, number tokens, and the robber's starting position
    public void initializeBoard(Board board) {
        // The Board class should already have its hex tiles and number tokens set up
        board = new Board(); // Creates the randomized board with resource tiles and tokens

        // Place the robber on the desert tile at the start of the game
        HexTile desertTile = board.getDesertTile();
        Robber robber = new Robber(desertTile);
        System.out.println("Board initialized with hex tiles and tokens. Robber placed on the desert tile.");
    }

    // Handles the initial placement phase, where each player places two settlements and two roads
    public void initialPlacement(List<Player> players, Board board) {
        System.out.println("Starting initial placement phase...");

        // Each player places two settlements and two roads, in turn order
        // First pass: Each player places their first settlement and road
        for (Player player : players) {
            placeSettlementAndRoad(player, board);
        }

        // Second pass: Each player places their second settlement and road in reverse turn order
        for (int i = players.size() - 1; i >= 0; i--) {
            placeSettlementAndRoad(players.get(i), board);
        }
    }

    // Method to place a settlement and road for a given player
    private void placeSettlementAndRoad(Player player, Board board) {
        System.out.println("Player " + player + ", place your settlement:");

        // Placeholder for selecting tile for settlement
        HexTile settlementTile = selectTileForSettlement(board);
        if (settlementTile != null) {
            player.buildSettlement();
            System.out.println("Player placed settlement on " + settlementTile.getResourceType() + " tile.");

            // Assign initial resources if it's the second settlement in the setup phase
            if (player.getSettlements() == 2) {
                distributeInitialResources(player, settlementTile);
            }
        }

        System.out.println("Player " + player + ", place your road adjacent to the settlement:");
        // Placeholder for selecting tile for road, typically adjacent to the settlement
        player.buildRoad();
        System.out.println("Player placed road adjacent to the settlement.");
    }

    // Selects a tile for the settlement (placeholder for user input or logic)
    private HexTile selectTileForSettlement(Board board) {
        // Placeholder logic for selecting a tile
        // Here we just return a random tile or prompt the user for input
        List<HexTile> hexTiles = board.getHexTiles();
        for (HexTile tile : hexTiles) {
            if (!tile.isDesert()) {
                return tile;
            }
        }
        return null;
    }

    // Distributes resources for the initial settlement placement if on a resource tile
    private void distributeInitialResources(Player player, HexTile tile) {
        String resourceType = tile.getResourceType();
        try {
            Resource resource = Resource.valueOf(resourceType.toUpperCase());
            player.addResource(resource, 1);
            System.out.println("Player receives 1 " + resourceType + " from initial settlement placement.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid resource type for initial settlement placement.");
        }
    }
}
