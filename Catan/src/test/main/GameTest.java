package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import catan.main.Game;
import catan.players.Player;

public class GameTest {
    private Game game;

    @Before
    public void setUp() {
        game = new Game();
        game.initializeGame(4); // Example: Start the game with 4 players
    }

    @Test
    public void testGameInitialization() {
        assertEquals("Game should initialize with 4 players", 4, game.getPlayers().size());
        assertTrue("Game board should be initialized", game.getBoard() != null);
        assertTrue("Resource pool should be initialized", game.getResourcePool() != null);
    }

    @Test
    public void testVictoryCondition() {
        Player player = game.getPlayers().get(0); // Get the first player
        player.addVictoryPoints(10); // Simulate the player earning 10 points

        assertTrue("Game should detect a victory", game.checkVictory());
        assertEquals("Winning player should be the first player", player, game.getWinningPlayer());
    }

    @Test
    public void testPlayerTurns() {
        Player currentPlayer = game.getCurrentPlayer();
        game.endTurn(); // Move to the next player's turn
        Player nextPlayer = game.getCurrentPlayer();

        assertTrue("Current player should change after ending the turn", currentPlayer != nextPlayer);
    }
}
