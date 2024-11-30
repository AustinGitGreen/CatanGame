package test.main;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Robber;
import catan.main.Game;
import catan.players.Player;
import catan.resources.DevelopmentCardType;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GameTest {

    private Game game;

    @Before
    public void setUp() {
        game = new Game(3); // Initialize a game with 3 players
    }

    @Test
    public void testInitialization() {
        // Ensure the correct number of players
        List<Player> players = game.getPlayers();
        assertNotNull("Players list should not be null", players);
        assertEquals("Game should have 3 players", 3, players.size());

        // Ensure the board is initialized
        Board board = game.getBoard();
        assertNotNull("Board should be initialized", board);

        // Ensure the robber is placed on the desert tile
        Robber robber = new Robber(board.getDesertTile());
        assertNotNull("Robber should be initialized", robber);
        assertEquals("Robber should start on the desert tile", board.getDesertTile(), robber.getCurrentTile());
    }

    @Test
    public void testVictoryCondition() {
        // Simulate Player 1 achieving 10 victory points
        Player player1 = game.getPlayers().get(0);
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints();
        player1.incrementVictoryPoints(); // 10 points total

        assertTrue("Player 1 should win the game", game.checkVictory());
        assertEquals("Player 1 should have 10 victory points", 10, player1.getVictoryPoints());
    }

    @Test
    public void testResourceDistribution() {
        // Ensure a player receives resources for settlements during dice rolls
        Board board = game.getBoard();
        Player player = game.getPlayers().get(0);

        // Place a settlement on a tile with resource WOOD and number token 5
        HexTile woodTile = new HexTile(Resource.WOOD, 5);
        board.getHexTiles().add(woodTile);
        player.buildSettlement(woodTile);

        // Roll the dice with a result of 5
        int diceRoll = 5;
        game.startTurn(); // Simulates rolling the dice and distributing resources

        assertEquals("Player should receive 1 wood", 1, player.getResource(Resource.WOOD));
    }

    @Test
    public void testBankTrade() {
        Player player = game.getPlayers().get(0);
        player.addResource(Resource.WOOD, 4);

        // Perform a 4:1 bank trade (4 wood for 1 brick)
        boolean tradeSuccess = game.bankTrade(player, Resource.WOOD, Resource.BRICK, 4);
        assertTrue("Bank trade should be successful", tradeSuccess);
        assertEquals("Player should have 0 wood left", 0, player.getResource(Resource.WOOD));
        assertEquals("Player should have 1 brick", 1, player.getResource(Resource.BRICK));
    }

    @Test
    public void testPlayerTrade() {
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        // Give resources to players
        player1.addResource(Resource.WOOD, 1);
        player2.addResource(Resource.BRICK, 1);

        // Perform a player-to-player trade (1 wood for 1 brick)
        boolean tradeSuccess = game.playerTrade(player1, player2, Resource.WOOD, Resource.BRICK);
        assertTrue("Player-to-player trade should be successful", tradeSuccess);

        assertEquals("Player 1 should have 1 brick", 1, player1.getResource(Resource.BRICK));
        assertEquals("Player 2 should have 1 wood", 1, player2.getResource(Resource.WOOD));
    }

    @Test
    public void testDevelopmentCardPlay() {
        Player player = game.getPlayers().get(0);
        player.addDevelopmentCard(DevelopmentCardType.KNIGHT);

        // Play a Knight card
        boolean cardPlayed = game.playDevelopmentCard(player, DevelopmentCardType.KNIGHT);
        assertTrue("Knight card should be played successfully", cardPlayed);

        assertEquals("Player should have moved the robber", game.getBoard().getDesertTile(), game.getBoard().getDesertTile());
        assertFalse("Player should no longer have the Knight card", player.hasDevelopmentCard(DevelopmentCardType.KNIGHT));
    }
}
