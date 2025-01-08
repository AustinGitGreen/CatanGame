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

    // Initialize ports on the board (optional; could be moved to the Board class)
    public void initializeBoard(Board board) {
        System.out.println("Initializing ports...");

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

        board.setPorts(ports);
        System.out.println("Ports initialized.");
    }

    // Handle the initial placement of settlements and roads
    public void initialPlacement(List<Player> players, Board board) {
        System.out.println("Starting initial placement phase...");

        // First round of placement
        for (Player player : players) {
            placeSettlementAndRoad(player, board, true);
        }

        // Second round of placement (reverse order)
        for (int i = players.size() - 1; i >= 0; i--) {
            placeSettlementAndRoad(players.get(i), board, false);
        }
    }

    private void placeSettlementAndRoad(Player player, Board board, boolean isFirstSettlement) {
        System.out.println(player.getId() + ", place your settlement.");

        HexTile settlementTile = null;
        int intersection = 0;

        // Retry until a valid settlement placement is made
        while (settlementTile == null || !settlementTile.placeSettlement(player, intersection)) {
            settlementTile = selectTileForSettlement(player, board);
            if (settlementTile != null) {
                intersection = selectIntersectionForSettlement(settlementTile);
                if (settlementTile.placeSettlement(player, intersection)) {
                    player.buildSettlement(settlementTile, intersection); // Call Player's buildSettlement
                    System.out.println("Settlement placed successfully.");
                    System.out.println("Player Settlements: " + player.getSettlements());

                    // Distribute resources for the second settlement
                    if (!isFirstSettlement) {
                        distributeInitialResources(player, settlementTile, intersection);
                    }
                    break; // Exit loop if placement succeeds
                } else {
                    System.out.println("Invalid settlement placement. Try again.");
                }
            } else {
                System.out.println("Invalid tile selection. Try again.");
            }
        }

        // Place road after settlement
        System.out.println(player.getId() + ", place your road adjacent to the settlement.");
        placeRoad(player, settlementTile);
    }

    private HexTile selectTileForSettlement(Player player, Board board) {
        List<HexTile> hexTiles = board.getHexTiles();
        System.out.println("Enter tile index for settlement (0-" + (hexTiles.size() - 1) + "): ");
        int tileIndex = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (tileIndex >= 0 && tileIndex < hexTiles.size()) {
            HexTile tile = hexTiles.get(tileIndex);
            if (!tile.isDesert()) {
                return tile;
            } else {
                System.out.println("Cannot place settlement on a desert tile.");
            }
        } else {
            System.out.println("Invalid tile index.");
        }
        return null;
    }

    private int selectIntersectionForSettlement(HexTile tile) {
        System.out.println("Select an intersection (1-6) for your settlement on this tile:");
        int intersection = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (intersection >= 1 && intersection <= 6) {
            return intersection;
        } else {
            System.out.println("Invalid intersection. Defaulting to 1.");
            return 1; // Default to intersection 1 if invalid
        }
    }

    private void distributeInitialResources(Player player, HexTile settlementTile, int intersection) {
        System.out.println("Distributing resources from adjacent tiles...");
        for (HexTile adjacentTile : settlementTile.getAdjacentTiles()) {
            Resource resource = adjacentTile.getResourceType();
            if (resource != Resource.DESERT) {
                player.addResource(resource, 1);
                System.out.println("Player " + player.getId() + " receives 1 " + resource + ".");
            }
        }
    }

    private void placeRoad(Player player, HexTile settlementTile) {
        List<HexTile> adjacentTiles = settlementTile.getAdjacentTiles();
        System.out.println("Available tiles for road placement: ");
        for (int i = 0; i < adjacentTiles.size(); i++) {
            System.out.println(i + ": Tile with resource " + adjacentTiles.get(i).getResourceType());
        }

        System.out.println("Enter the index of the tile to place a road:");
        int roadIndex = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (roadIndex >= 0 && roadIndex < adjacentTiles.size()) {
            HexTile roadTile = adjacentTiles.get(roadIndex);
            if (roadTile != null) {
                player.placeRoadWithoutResources(); // Special method to skip resource check
                System.out.println("Road placed on tile with resource: " + roadTile.getResourceType());
            } else {
                System.out.println("Invalid tile selected for road placement.");
            }
        } else {
            System.out.println("Invalid index. No road placed.");
        }
    }
}
