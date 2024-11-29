package test.main;

import static org.junit.Assert.*;

import catan.main.WinCondition;
import catan.players.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WinConditionTest {

    private List<Player> players;

    @Before
    public void setUp() {
        // Initialize a list of players for testing
        players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player());
        }
    }

    @Test
    public void testNoWinner() {
        // Ensure no player has the winning points
        for (Player player : players) {
            assertEquals("Players should start with 0 victory points", 0, player.getVictoryPoints());
        }

        // Verify that no winner is detected
        assertNull("There should be no winner if no player has 10 or more victory points",
                WinCondition.checkForWinner(players));
    }

    @Test
    public void testSingleWinner() {
        // Set one player to have the winning points
        Player winner = players.get(2);
        winner.incrementVictoryPoints(); // 1 point
        winner.incrementVictoryPoints(); // 2 points
        winner.incrementVictoryPoints(); // 3 points
        winner.incrementVictoryPoints(); // 4 points
        winner.incrementVictoryPoints(); // 5 points
        winner.incrementVictoryPoints(); // 6 points
        winner.incrementVictoryPoints(); // 7 points
        winner.incrementVictoryPoints(); // 8 points
        winner.incrementVictoryPoints(); // 9 points
        winner.incrementVictoryPoints(); // 10 points

        // Verify that the correct player is identified as the winner
        assertEquals("Player 2 should be the winner", winner, WinCondition.checkForWinner(players));
    }

    @Test
    public void testMultiplePlayersWithWinningPoints() {
        // Set multiple players to have the winning points
        Player winner1 = players.get(1);
        Player winner2 = players.get(3);

        for (int i = 0; i < 10; i++) {
            winner1.incrementVictoryPoints();
            winner2.incrementVictoryPoints();
        }

        // Verify that the first player with winning points is returned
        assertEquals("Player 1 should be returned as the winner if multiple players qualify",
                winner1, WinCondition.checkForWinner(players));
    }
}
