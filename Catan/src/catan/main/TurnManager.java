package catan.main;

import catan.players.Player;

import java.util.List;

/**
 * Manages the turns of players in the game.
 */
public class TurnManager {
    private List<Player> players;
    private int currentPlayerIndex;

    /**
     * Constructs a TurnManager with the given list of players.
     * @param players The list of players in the game.
     */
    public TurnManager(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list cannot be null or empty");
        }
        this.players = players;
        this.currentPlayerIndex = 0; // Start with the first player
    }

    /**
     * Gets the current player.
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Moves to the next player's turn.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}
