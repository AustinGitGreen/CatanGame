package test.main;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Robber;
import catan.main.TurnManager;
import catan.players.Player;
import catan.resources.Bank;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TurnManagerTest {
    private TurnManager turnManager;
    private List<Player> players;
    private Board board;
    private Bank bank;
    private Robber robber;

    @Before
    public void setUp() {
        // Initialize game components
        players = new ArrayList<>();
        Player player1 = new Player();
        Player player2 = new Player();
        players.add(player1);
        players.add(player2);

        board = new Board();
        bank = new Bank();

        // Set up hex tiles for the board
        HexTile woodTile = new HexTile(Resource.WOOD, 5); // Number token 5
        HexTile brickTile = new HexTile(Resource.BRICK, 8); // Number token 8
        board.getHexTiles().add(woodTile);
        board.getHexTiles().add(brickTile);

        robber = new Robber(woodTile);

        turnManager = new TurnManager(players, board, bank, robber);

        // Player 1 places a settlement on the wood tile
        player1.buildSettlement(woodTile);
        // Player 2 places a settlement on the brick tile
        player2.buildSettlement(brickTile);
    }

    @Test
    public void testStartTurnDistributesResources() {
        // Simulate rolling a 5 to trigger resource distribution
        turnManager.startTurn(); // Player 1 starts their turn

        // Player 1 should receive 1 wood
        assertEquals("Player 1 should have 1 wood", 1, players.get(0).getResource(Resource.WOOD));

        // Player 2 should not receive any resources
        assertEquals("Player 2 should have 0 resources", 0, players.get(1).getResource(Resource.WOOD));
    }

    @Test
    public void testTriggerRobber() {
        // Simulate a roll of 7 to trigger the robber
        players.get(0).addResource(Resource.WOOD, 8); // Give Player 1 excess resources
        turnManager.startTurn(); // Player 1's turn, roll 7 triggers robber

        // Player 1 should discard half their resources (4 wood left)
        assertEquals("Player 1 should have 4 wood after discarding", 4, players.get(0).getResource(Resource.WOOD));

        // Move the robber and ensure it's not on a desert tile
        HexTile newRobberTile = turnManager.selectTileForRobber();
        assertNotNull("Robber should move to a valid tile", newRobberTile);
        assertFalse("Robber should not move to a desert tile", newRobberTile.isDesert());
    }

    @Test
    public void testEndTurnCyclesToNextPlayer() {
        // Player 1 starts
        assertEquals("Player 1 should be the current player", players.get(0), turnManager.getCurrentPlayer());

        // End turn, Player 2 becomes the current player
        turnManager.endTurn();
        assertEquals("Player 2 should be the current player", players.get(1), turnManager.getCurrentPlayer());

        // End turn again, cycle back to Player 1
        turnManager.endTurn();
        assertEquals("Player 1 should be the current player again", players.get(0), turnManager.getCurrentPlayer());
    }

    @Test
    public void testResourceDistributionWithRobber() {
        // Place the robber on the wood tile
        robber.move(board.getHexTiles().get(0)); // Robber on wood tile

        // Simulate rolling an 8 (brick tile number)
        turnManager.startTurn(); // Player 1's turn

        // Player 2 should receive 1 brick
        assertEquals("Player 2 should have 1 brick", 1, players.get(1).getResource(Resource.BRICK));

        // Player 1 should not receive any resources due to the robber
        assertEquals("Player 1 should not receive wood due to robber", 0, players.get(0).getResource(Resource.WOOD));
    }
}
