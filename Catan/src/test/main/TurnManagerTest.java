package test.main;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import game.TurnManager;
import players.Player;

import java.util.ArrayList;
import java.util.List;

public class TurnManagerTest {
    private TurnManager turnManager;
    private List<Player> players;

    @Before
    public void setUp() {
        players = new ArrayList<>();
        players.add(new Player("Alice"));
        players.add(new Player("Bob"));
        players.add(new Player("Charlie"));
        players.add(new Player("Diana"));

        turnManager = new TurnManager(players);
    }

    @Test
    public void testTurnSequence() {
        Player currentPlayer = turnManager.getCurrentPlayer();
        turnManager.nextTurn(); // Move to the next player's turn
        Player nextPlayer = turnManager.getCurrentPlayer();

        assertEquals("Next player should be Bob", "Bob", nextPlayer.getName());

        turnManager.nextTurn(); // Move to the next turn
        nextPlayer = turnManager.getCurrentPlayer();

        assertEquals("Next player should be Charlie", "Charlie", nextPlayer.getName());
    }

    @Test
    public void testTurnWrapping() {
        for (int i = 0; i < 4; i++) {
            turnManager.nextTurn(); // Cycle through all players
        }

        Player currentPlayer = turnManager.getCurrentPlayer();
        assertEquals("Turn should wrap back to Alice", "Alice", currentPlayer.getName());
    }

    @Test
    public void testGetCurrentPlayer() {
        Player currentPlayer = turnManager.getCurrentPlayer();
        assertEquals("Current player should be Alice initially", "Alice", currentPlayer.getName());
    }
}
