package catan.main;

import catan.board.Board;
import catan.board.HexTile;
import catan.players.Player;
import catan.resources.DevelopmentCardType;
import catan.resources.Resource;

import java.util.Scanner;

public class GameController {
    private Game game;
    private Scanner scanner;

    public GameController(int numberOfPlayers) {
        // Initialize Game with the specified number of players
        this.game = new Game(numberOfPlayers);
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("Starting the game...");

        while (true) {
            game.startTurn();  // Begin the current player's turn
            System.out.println("It's Player " + game.getCurrentPlayer().getId() + "'s turn.");

            handlePlayerActions();  // Handle available player actions

            game.endTurn();  // End the current player's turn

            // Check for a winner after each turn
            if (game.checkVictory()) {
                System.out.println("Game over! We have a winner.");
                break;
            }
        }
    }

    public Game getGame() {
        return game;
    }

    private void handlePlayerActions() {
        boolean turnActive = true;

        while (turnActive) {
            // Display available actions
            System.out.println("Available actions:");
            System.out.println("1. Build Settlement");
            System.out.println("2. Build City");
            System.out.println("3. Build Road");
            System.out.println("4. Trade");
            System.out.println("5. Play Development Card");
            System.out.println("6. End Turn");
            System.out.println("7. View Game Summary");
            System.out.print("Enter action number: ");

            int action = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            Player currentPlayer = game.getCurrentPlayer();

            switch (action) {
                case 1:
                    buildSettlement(currentPlayer);
                    break;
                case 2:
                    buildCity(currentPlayer);
                    break;
                case 3:
                    buildRoad(currentPlayer);
                    break;
                case 4:
                    trade(currentPlayer);
                    break;
                case 5:
                    playDevelopmentCard(currentPlayer);
                    break;
                case 6:
                    turnActive = false;
                    System.out.println("Ending turn...");
                    break;
                case 7:
                    printGameSummary();
                    break;
                default:
                    System.out.println("Invalid action. Please select a valid option.");
                    break;
            }
        }
    }

    // Methods for each action

    private void buildSettlement(Player player) {
        if (player.canBuildSettlement()) {
            HexTile tile = selectTileForSettlement();
            if (tile != null && isValidSettlementLocation(player, tile)) {
                int intersection = selectIntersectionForPlacement();
                if (tile.placeSettlement(player, intersection)) {
                    System.out.println("Settlement built on tile with resource: " + tile.getResourceType());
                } else {
                    System.out.println("Invalid settlement placement. Try again.");
                }
            } else {
                System.out.println("Invalid location for a settlement.");
            }
        } else {
            System.out.println("Insufficient resources to build a settlement.");
        }
    }

    private void buildCity(Player player) {
        if (player.canBuildCity()) {
            HexTile tile = selectTileForSettlement();
            if (tile != null) {
                int intersection = selectIntersectionForPlacement();
                if (tile.upgradeSettlementToCity(player, intersection)) {
                    System.out.println("City built on tile with resource: " + tile.getResourceType());
                } else {
                    System.out.println("You must upgrade an existing settlement to build a city.");
                }
            } else {
                System.out.println("Invalid tile for city upgrade.");
            }
        } else {
            System.out.println("Insufficient resources to build a city.");
        }
    }

    private void buildRoad(Player player) {
        if (player.canBuildRoad()) {
            HexTile tile = selectTileForRoad();
            if (tile != null && isValidRoadLocation(player, tile)) {
                player.buildRoad();
                System.out.println("Road built adjacent to tile: " + tile.getResourceType());
            } else {
                System.out.println("Invalid location for a road.");
            }
        } else {
            System.out.println("Insufficient resources to build a road.");
        }
    }

    private void trade(Player player) {
        System.out.println("Enter trade type: (1) Bank trade or (2) Player-to-player trade");
        int tradeType = scanner.nextInt();
        scanner.nextLine();

        switch (tradeType) {
            case 1:
                bankTrade(player);
                break;
            case 2:
                playerTrade(player);
                break;
            default:
                System.out.println("Invalid trade type selected.");
                break;
        }
    }

    private void bankTrade(Player player) {
        System.out.println("Enter resource to give: ");
        String giveResource = scanner.nextLine().toUpperCase();
        System.out.println("Enter resource to receive: ");
        String receiveResource = scanner.nextLine().toUpperCase();
        System.out.println("Enter exchange ratio (e.g., 4 for a 4:1 trade): ");
        int ratio = scanner.nextInt();
        scanner.nextLine();

        if (game.bankTrade(player, Resource.valueOf(giveResource), Resource.valueOf(receiveResource), ratio)) {
            System.out.println("Trade successful with the bank.");
        } else {
            System.out.println("Trade failed. Check resources or ratio.");
        }
    }

    private void playerTrade(Player player) {
        System.out.println("Enter the ID of the player to trade with: ");
        int targetPlayerId = scanner.nextInt();
        scanner.nextLine();

        Player targetPlayer = game.getPlayerById(targetPlayerId);
        if (targetPlayer == null || targetPlayer == player) {
            System.out.println("Invalid player selected for trade.");
            return;
        }

        System.out.println("Enter resource to give: ");
        String giveResource = scanner.nextLine().toUpperCase();
        System.out.println("Enter resource to receive: ");
        String receiveResource = scanner.nextLine().toUpperCase();

        if (game.playerTrade(player, targetPlayer, Resource.valueOf(giveResource), Resource.valueOf(receiveResource))) {
            System.out.println("Trade successful with Player " + targetPlayerId);
        } else {
            System.out.println("Trade failed. Check resources.");
        }
    }

    private void playDevelopmentCard(Player player) {
        System.out.println("Enter development card type to play (KNIGHT, ROAD_BUILDING, YEAR_OF_PLENTY, MONOPOLY): ");
        String cardType = scanner.nextLine().toUpperCase();

        if (game.playDevelopmentCard(player, DevelopmentCardType.valueOf(cardType))) {
            System.out.println("Played " + cardType + " card.");
        } else {
            System.out.println("Failed to play " + cardType + ". Check if you own the card.");
        }
    }

    public void printGameSummary() {
        System.out.println("===== Game Summary =====");
        for (Player player : game.getPlayers()) {
            System.out.println("Player " + player.getId() + ":");
            System.out.println("  - Resources: " + player.getResourceSummary());
            System.out.println("  - Settlements: " + player.getSettlements());
            System.out.println("  - Cities: " + player.getCities());
            System.out.println("  - Roads: " + player.getRoads());
            System.out.println("  - Victory Points: " + player.getVictoryPoints());
            System.out.println();
        }
    }

    // Helper methods for placement validation
    private HexTile selectTileForSettlement() {
        System.out.println("Enter tile index for settlement: ");
        int tileIndex = scanner.nextInt();
        scanner.nextLine();
        return game.getBoard().getHexTiles().get(tileIndex);
    }

    private int selectIntersectionForPlacement() {
        System.out.println("Enter intersection number (1-6): ");
        int intersection = scanner.nextInt();
        scanner.nextLine();
        return intersection;
    }

    private HexTile selectTileForRoad() {
        System.out.println("Enter tile index for road placement: ");
        int tileIndex = scanner.nextInt();
        scanner.nextLine();
        return game.getBoard().getHexTiles().get(tileIndex);
    }

    private boolean isValidSettlementLocation(Player player, HexTile tile) {
        // Add custom validation logic (e.g., adjacency rules)
        return !tile.isDesert(); // Example placeholder
    }

    private boolean isValidRoadLocation(Player player, HexTile tile) {
        // Add custom validation logic for road placement
        return true; // Placeholder logic
    }
}
