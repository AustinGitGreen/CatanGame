package catan.main;

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
                default:
                    System.out.println("Invalid action. Please select a valid option.");
                    break;
            }
        }
    }

    // Methods for each action

    private void buildSettlement(Player player) {
        if (player.canBuildSettlement()) {
            // Assuming `selectTileForSettlement` method is available in Game
            HexTile tile = game.selectTileForSettlement(player);
            if (tile != null) {
                player.buildSettlement(tile);
                System.out.println("Settlement built on tile with resource: " + tile.getResourceType());
            } else {
                System.out.println("Invalid location for a settlement.");
            }
        } else {
            System.out.println("Insufficient resources to build a settlement.");
        }
    }

    private void buildCity(Player player) {
        if (player.canBuildCity()) {
            player.buildCity();
            System.out.println("City built! Player " + player.getId() + " now has " + player.getVictoryPoints() + " victory points.");
        } else {
            System.out.println("Insufficient resources to build a city.");
        }
    }

    private void buildRoad(Player player) {
        if (player.canBuildRoad()) {
            // Assuming `selectLocationForRoad` method exists in Game
            player.buildRoad();
            System.out.println("Road built!");
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
}
