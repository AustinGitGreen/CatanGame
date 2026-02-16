package catan.main;

import catan.players.Player;

import java.util.List;

/**
 * Handles player turn rotation.
 */
public class TurnManager {
    private final List<Player> players;
    private int currentPlayerIndex;

    public TurnManager(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Players list cannot be null or empty.");
        }
        this.players = players;
        this.currentPlayerIndex = 0;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void previousTurn() {
        currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int idx) {
        if (idx < 0 || idx >= players.size()) {
            throw new IllegalArgumentException("Player index out of range.");
        }
        this.currentPlayerIndex = idx;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }
}
