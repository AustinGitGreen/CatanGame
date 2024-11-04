package catan.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Robber;
import catan.players.Player;
import catan.resources.Bank;
import catan.resources.DevelopmentCardType;
import catan.resources.Resource;

public class Game {
    private List<Player> players;
    private Board board;
    private Bank bank;
    private Robber robber;
    private TurnManager turnManager;
    private int currentPlayerIndex;
    private static final int WINNING_POINTS = 10;
    private Scanner scanner;

    public Game(int numberOfPlayers) {
        players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player());
        }
        board = new Board();
        bank = new Bank();
        robber = new Robber(board.getDesertTile());
        turnManager = new TurnManager(players, board, bank, robber);
        currentPlayerIndex = 0;
        scanner = new Scanner(System.in);

        // Initialize the board and perform initial placement
        GameSetup gameSetup = new GameSetup();
        gameSetup.initializeBoard(board);
        gameSetup.initialPlacement(players, board);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // Check if a player has reached the victory condition
    public boolean checkVictory() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= WINNING_POINTS) {
                System.out.println(player + " wins with " + player.getVictoryPoints() + " points!");
                return true;
            }
        }
        return false;
    }

    // Starts a player's turn
    public void startTurn() {
        Player currentPlayer = getCurrentPlayer();
        System.out.println(currentPlayer + "'s turn starts.");
        
        // Roll dice and distribute resources
        int diceRoll = rollDice();
        System.out.println(currentPlayer + " rolled a " + diceRoll);
        if (diceRoll == 7) {
            triggerRobber(currentPlayer);
        } else {
            distributeResources(diceRoll);
        }

        // Placeholder for player actions (e.g., building, trading, playing cards)
        handlePlayerActions(currentPlayer);
    }

    // Ends the player's turn and advances to the next player
    public void endTurn() {
        System.out.println(getCurrentPlayer() + "'s turn ends.");
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // Helper method to roll two six-sided dice
    private int rollDice() {
        Random random = new Random();
        return random.nextInt(6) + 1 + random.nextInt(6) + 1;
    }

    // Distributes resources based on dice roll, except if the robber is on a tile
    private void distributeResources(int diceRoll) {
        for (HexTile tile : board.getHexTiles()) {
            if (tile.getNumberToken() == diceRoll && !tile.isDesert() && tile != robber.getCurrentTile()) {
                for (Player player : players) {
                    if (player.hasSettlementOnTile(tile)) {
                        Resource resource = Resource.valueOf(tile.getResourceType().toUpperCase());
                        player.addResource(resource, 1);
                        System.out.println(player + " receives 1 " + resource);
                    }
                }
            }
        }
    }

    // Placeholder for handling player actions during their turn
    private void handlePlayerActions(Player currentPlayer) {
        System.out.println(currentPlayer + ", you can build, trade, play a development card, or end your turn.");
        // Extend with interactive options or predefined actions in an actual game
    }

    // Select a tile for a player's settlement
    public HexTile selectTileForSettlement(Player player) {
        System.out.println("Enter tile index for settlement (0 - " + (board.getHexTiles().size() - 1) + "): ");
        int tileIndex = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (tileIndex >= 0 && tileIndex < board.getHexTiles().size()) {
            HexTile selectedTile = board.getHexTiles().get(tileIndex);
            if (!selectedTile.isDesert() && !selectedTile.hasSettlement(player)) {
                return selectedTile;
            } else {
                System.out.println("Invalid tile selection for settlement.");
            }
        }
        return null;
    }

    // Plays a development card for the player
    public boolean playDevelopmentCard(Player player, DevelopmentCardType cardType) {
        if (player.hasDevelopmentCard(cardType)) {
            switch (cardType) {
                case KNIGHT:
                    player.incrementKnights();
                    triggerRobber(player); // Move the robber as part of playing the knight
                    break;
                case ROAD_BUILDING:
                    // Allow the player to build two roads without resource cost
                    player.buildRoad();
                    player.buildRoad();
                    break;
                case YEAR_OF_PLENTY:
                    System.out.println("Select two resources to receive:");
                    Resource resource1 = selectResource();
                    Resource resource2 = selectResource();
                    player.addResource(resource1, 1);
                    player.addResource(resource2, 1);
                    break;
                case MONOPOLY:
                    System.out.println("Choose a resource type to monopolize:");
                    Resource monopolyResource = selectResource();
                    int totalCollected = 0;
                    for (Player otherPlayer : players) {
                        if (otherPlayer != player) {
                            totalCollected += otherPlayer.getResource(monopolyResource);
                            otherPlayer.removeResource(monopolyResource, otherPlayer.getResource(monopolyResource));
                        }
                    }
                    player.addResource(monopolyResource, totalCollected);
                    break;
                default:
                    System.out.println("Unknown development card type.");
                    return false;
            }
            player.useDevelopmentCard(cardType); // Mark the card as used
            return true;
        } else {
            System.out.println("You don't have this development card.");
            return false;
        }
    }

    private void triggerRobber(Player currentPlayer) {
        System.out.println("Robber activated! " + currentPlayer + " must move the robber.");

        // Step 1: Players with more than 7 resources must discard half
        for (Player player : players) {
            int totalResources = player.getTotalResources();
            if (totalResources > 7) {
                player.discardHalfResources();
                System.out.println(player + " discards half of their resources.");
            }
        }

        // Step 2: Move the robber to a new tile
        HexTile newLocation = selectTileForRobber();
        robber.move(newLocation);
        System.out.println("Robber moved to tile with resource: " + newLocation.getResourceType());

        // Step 3: Allow the current player to steal a resource from another player with a settlement on the new tile
        Player targetPlayer = selectPlayerToStealFrom(newLocation, currentPlayer);
        if (targetPlayer != null) {
            Resource stolenResource = targetPlayer.stealRandomResource();
            if (stolenResource != null) {
                currentPlayer.addResource(stolenResource, 1);
                System.out.println(currentPlayer + " stole " + stolenResource + " from " + targetPlayer);
            } else {
                System.out.println(targetPlayer + " has no resources to steal.");
            }
        } else {
            System.out.println("No players to steal from on the new robber location.");
        }
    }

    private Player selectPlayerToStealFrom(HexTile newLocation, Player currentPlayer) {
        for (Player player : players) {
            if (player != currentPlayer && player.hasSettlementOnTile(newLocation)) {
                return player; // Return the first player with a settlement on the robber's new tile
            }
        }
        return null; // No other players have a settlement on this tile
    }

    private HexTile selectTileForRobber() {
        System.out.println("Select a tile for the robber (0 - " + (board.getHexTiles().size() - 1) + "): ");
        int tileIndex = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (tileIndex >= 0 && tileIndex < board.getHexTiles().size()) {
            HexTile selectedTile = board.getHexTiles().get(tileIndex);
            if (!selectedTile.isDesert()) {
                return selectedTile; // Return the selected tile if valid
            } else {
                System.out.println("You cannot place the robber on the desert tile.");
            }
        } else {
            System.out.println("Invalid tile index.");
        }
        return selectTileForRobber(); // Retry if invalid selection
    }

    private Resource selectResource() {
        System.out.println("Enter resource (WOOD, BRICK, SHEEP, WHEAT, ORE): ");
        String resourceName = scanner.nextLine().toUpperCase();
        try {
            return Resource.valueOf(resourceName);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid resource type.");
            return selectResource(); // Retry if invalid input
        }
    }

    // Handles trading with the bank
    public boolean bankTrade(Player player, Resource giveResource, Resource receiveResource, int ratio) {
        if (player.hasResource(giveResource, ratio)) {
            player.removeResource(giveResource, ratio);
            player.addResource(receiveResource, 1);
            System.out.println("Trade successful: Traded " + ratio + " " + giveResource + " for 1 " + receiveResource);
            return true;
        } else {
            System.out.println("Insufficient resources for bank trade.");
            return false;
        }
    }

    // Handles player-to-player trading
    public boolean playerTrade(Player player, Player targetPlayer, Resource giveResource, Resource receiveResource) {
        if (player.hasResource(giveResource, 1) && targetPlayer.hasResource(receiveResource, 1)) {
            player.removeResource(giveResource, 1);
            targetPlayer.addResource(giveResource, 1);
            targetPlayer.removeResource(receiveResource, 1);
            player.addResource(receiveResource, 1);
            System.out.println("Trade successful with " + targetPlayer);
            return true;
        } else {
            System.out.println("Trade failed. One of the players lacks the required resources.");
            return false;
        }
    }

    // Finds a player by ID
    public Player getPlayerById(int playerId) {
        if (playerId >= 0 && playerId < players.size()) {
            return players.get(playerId);
        } else {
            System.out.println("Player ID not found.");
            return null;
        }
    }
}
