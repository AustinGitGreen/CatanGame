package catan.main;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Port;
import catan.players.Player;
import catan.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameSetup {
    private Scanner scanner;

    public GameSetup() {
        scanner = new Scanner(System.in);
    }

    public void initializeBoard(Board board) {
        System.out.println("Initializing board with hex tiles, tokens, and ports.");

        // Create ports around the edges of the board
        List<Port> ports = new ArrayList<>();
        ports.add(new Port(2, Resource.WOOD));  // 2:1 Wood port
        ports.add(new Port(2, Resource.BRICK)); // 2:1 Brick port
        ports.add(new Port(2, Resource.SHEEP)); // 2:1 Sheep port
        ports.add(new Port(2, Resource.WHEAT)); // 2:1 Wheat port
        ports.add(new Port(2, Resource.ORE));   // 2:1 Ore port
        ports.add(new Port(3, null));           // 3:1 Generic port
        ports.add(new Port(3, null));           // 3:1 Generic port
        ports.add(new Port(3, null));           // 3:1 Generic port
        ports.add(new Port(3, null));           // 3:1 Generic port

        // Assign ports to the board (assuming Board has a setPorts method)
        board.setPorts(ports);
        
        System.out.println("Board initialized with hex tiles, tokens, and ports.");
    }

    public void initialPlacement(List<Player> players, Board board) {
        System.out.println("Starting initial placement phase...");
        for (Player player : players) {
            placeSettlementAndRoad(player, board);
        }
        for (int i = players.size() - 1; i >= 0; i--) {
            placeSettlementAndRoad(players.get(i), board);
        }
    }

    private void placeSettlementAndRoad(Player player, Board board) {
        System.out.println("Player " + player + ", place your settlement:");

        HexTile settlementTile = selectTileForSettlement(board);
        if (settlementTile != null) {
            player.buildSettlement(settlementTile);
            System.out.println("Player placed settlement on " + settlementTile.getResourceType() + " tile.");

            if (player.getSettlements() == 2) {
                distributeInitialResources(player, settlementTile);
            }
        }

        System.out.println("Player " + player + ", place your road adjacent to the settlement:");
        player.buildRoad();
        System.out.println("Player placed road adjacent to the settlement.");
    }

    private HexTile selectTileForSettlement(Board board) {
        List<HexTile> hexTiles = board.getHexTiles();
        for (HexTile tile : hexTiles) {
            if (!tile.isDesert()) {
                return tile;
            }
        }
        return null;
    }

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
