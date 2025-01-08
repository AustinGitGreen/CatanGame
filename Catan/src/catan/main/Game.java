package catan.main;

import catan.board.Board;
import catan.players.Player;
import catan.main.TurnManager;
import catan.resources.ResourcePool;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the main game logic for Catan.
 */
public class Game {
    private Board board;
    private List<Player> players;
    private ResourcePool resourcePool;
    private TurnManager turnManager;

    /**
     * Initializes the game with the specified number of players.
     * @param numberOfPlayers The number of players in the game.
     */
    public void initializeGame(int numberOfPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");
        }

        // Initialize the board, resource pool, and players
        board = new Board();
        board.initializeBoard();
        resourcePool = new ResourcePool();

        players = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            players.add(new Player("Player " + i));
        }

        turnManager = new TurnManager(players); // Use TurnManager to handle player turns

        System.out.println("Game initialized with " + numberOfPlayers + " players.");
    }

    /**
     * Gets the list of players in the game.
     * @return The list of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the game board.
     * @return The game board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the resource pool.
     * @return The resource pool.
     */
    public ResourcePool getResourcePool() {
        return resourcePool;
    }

    /**
     * Gets the current player.
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return turnManager.getCurrentPlayer();
    }

    /**
     * Ends the current player's turn and moves to the next player.
     */
    public void endTurn() {
        turnManager.nextTurn();
        System.out.println("It is now " + getCurrentPlayer().getName() + "'s turn.");
    }

    /**
     * Checks if a player has won the game.
     * @return True if a player has won, false otherwise.
     */
    public boolean checkVictory() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= 10) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the winning player, if there is one.
     * @return The winning player, or null if no player has won yet.
     */
    public Player getWinningPlayer() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= 10) {
                return player;
            }
        }
        return null;
    }

    /**
     * Starts the game loop, interacting with the console.
     */
    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Starting the game...");
        while (!checkVictory()) {
            Player currentPlayer = getCurrentPlayer();
            System.out.println(currentPlayer.getName() + ", it's your turn.");

            // Example of interaction: Roll dice (simulate with random numbers)
            System.out.println("Rolling the dice...");
            int diceRoll = (int) (Math.random() * 6 + 1) + (int) (Math.random() * 6 + 1);
            System.out.println("You rolled: " + diceRoll);

            // End turn
            System.out.println("Enter any key to end your turn.");
            scanner.nextLine();
            endTurn();
        }

        Player winner = getWinningPlayer();
        System.out.println("Congratulations, " + winner.getName() + "! You have won the game with " + winner.getVictoryPoints() + " victory points!");

        scanner.close();
    }
}
