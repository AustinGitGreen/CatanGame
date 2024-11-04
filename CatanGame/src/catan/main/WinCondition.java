package catan.main;

import catan.players.Player;
import java.util.List;

public class WinCondition {
    private static final int WINNING_POINTS = 10;

    public static Player checkForWinner(List<Player> players) {
        for (Player player : players) {
            if (player.getVictoryPoints() >= WINNING_POINTS) {
                return player;
            }
        }
        return null;
    }
}