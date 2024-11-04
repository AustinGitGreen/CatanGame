package catan.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Robber;
import catan.players.Player;
import catan.resources.Bank;
import catan.resources.Resource;

public class Game {
    private List<Player> players;
    private Board board;
    private Bank bank;
    private Robber robber;
    private TurnManager turnManager;
    private int currentPlayerIndex;
    private static final int WINNING_POINTS = 10;

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

        // Initialize the board and perform initial placement
        GameSetup gameSetup = new GameSetup();
        gameSetup.initializeBoard(board);
        gameSetup.initialPlacement(players, board);
    }

    // Begins the game loop
    public void startGame() {
        System.out.println("Starting the game...");
        while (!checkVictory()) {
            startTurn();
            endTurn();
        }
        System.out.println("Game over! We have a winner.");
    }

    // Starts a player's turn
    public void startTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        System.out.println("Player " + currentPlayerIndex + "'s turn starts.");
        
        // Roll dice and distribute resources
        int diceRoll = rollDice();
        System.out.println("Player " + currentPlayerIndex + " rolled a " + diceRoll);
        if (diceRoll == 7) {
            triggerRobber(currentPlayer);
        } else {
            distributeResources(diceRoll);
        }

        // Placeholder for player actions (build, trade, play cards, etc.)
        // Example: currentPlayer.takeActions();
    }

    // Rolls two six-sided dice
    private int rollDice() {
        Random random = new Random();
        return random.nextInt(6) + 1 + random.nextInt(6) + 1;
    }

    // Distributes resources based on dice roll, except if the robber is on a tile
    private void distributeResources(int diceRoll) {
        for (HexTile tile : board.getHexTiles()) {
            if (tile.getNumberToken() == diceRoll && !tile.isDesert() && tile != robber.getCurrentTile()) {
                for (Player player : players) {
                    if (playerHasSettlementOnTile(player, tile)) { // Assuming such a method exists
                        Resource resource = Resource.valueOf(tile.getResourceType().toUpperCase());
                        player.addResource(resource, 1);
                        System.out.println("Player " + players.indexOf(player) + " receives 1 " + resource);
                    }
                }
            }
        }
    }

    // Trigger the robber when a 7 is rolled
    private void triggerRobber(Player currentPlayer) {
        System.out.println("Robber activated! Player " + currentPlayerIndex + " must move the robber.");

        // Force players with more than 7 resources to discard half
        for (Player player : players) {
            int totalResources = player.getTotalResources();
            if (totalResources > 7) {
                player.discardHalfResources();
                System.out.println("Player " + players.indexOf(player) + " discards half of their resources.");
            }
        }

        // Move the robber to a new tile
        HexTile newLocation = selectTileForRobber();
        robber.move(newLocation);
        System.out.println("Robber moved to tile with resource " + newLocation.getResourceType());

        // Allow the current player to steal a resource from a player with a settlement on the new tile
        Player targetPlayer = selectPlayerToStealFrom(newLocation, currentPlayer);
        if (targetPlayer != null) {
            Resource stolenResource = targetPlayer.stealRandomResource();
            currentPlayer.addResource(stolenResource, 1);
            System.out.println("Player " + currentPlayerIndex + " stole " + stolenResource + " from Player " + players.indexOf(targetPlayer));
        }
    }

    // Ends the player's turn and advances to the next player
    public void endTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        System.out.println("Player " + currentPlayerIndex + "'s turn ends.");
    }

    // Checks for a victory condition (10 victory points)
    public boolean checkVictory() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= WINNING_POINTS) {
                System.out.println("Player " + players.indexOf(player) + " wins with " + player.getVictoryPoints() + " points!");
                return true;
            }
        }
        return false;
    }

    // Helper method to select a tile for the robber (placeholder for actual implementation)
    private HexTile selectTileForRobber() {
        // Logic for selecting a tile, typically chosen by the current player
        // Placeholder: Just return the first non-desert tile
        for (HexTile tile : board.getHexTiles()) {
            if (!tile.isDesert()) {
                return tile;
            }
        }
        return null;
    }

    // Placeholder to select a player to steal from on the given tile
    private Player selectPlayerToStealFrom(HexTile tile, Player currentPlayer) {
        for (Player player : players) {
            if (playerHasSettlementOnTile(player, tile) && player != currentPlayer) {
                return player;
            }
        }
        return null;
    }

    // Placeholder method to check if a player has a settlement on a given tile
    private boolean playerHasSettlementOnTile(Player player, HexTile tile) {
        // Implement based on how settlements are tracked on tiles
        return false; // Placeholder
    }

    // Getter for the list of players
    public List<Player> getPlayers() {
        return players;
    }
}
