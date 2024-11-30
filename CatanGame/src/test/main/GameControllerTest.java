package test.main;

import catan.main.GameController;
import catan.players.Player;
import catan.board.Board;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GameControllerTest {

    private GameController gameController;

    @Before
    public void setUp() {
        // Initialize a game with 2 players
        int numberOfPlayers = 2;
        gameController = new GameController(numberOfPlayers);

        // Set up players with initial resources for testing
        List<Player> players = gameController.getGame().getPlayers();
        for (Player player : players) {
            player.addResource(Resource.WOOD, 5);
            player.addResource(Resource.BRICK, 5);
            player.addResource(Resource.SHEEP, 5);
            player.addResource(Resource.WHEAT, 5);
            player.addResource(Resource.ORE, 5);
        }
    }

    @Test
    public void testStartGame() {
        // Simulate starting the game
        gameController.startGame();

        // Check if the game starts correctly
        Player currentPlayer = gameController.getGame().getCurrentPlayer();
        assertNotNull("There should be a current player", currentPlayer);
    }

    @Test
    public void testBuildSettlement() {
        Player player = gameController.getGame().getCurrentPlayer();
        Board board = gameController.getGame().getBoard();

        // Check player resources before building settlement
        assertTrue("Player should be able to build settlement", player.canBuildSettlement());

        // Simulate building a settlement
        gameController.buildSettlement(player);

        // Verify the player has one less resource of each type
        assertEquals("Player should have 4 wood remaining", 4, player.getResource(Resource.WOOD));
        assertEquals("Player should have 4 brick remaining", 4, player.getResource(Resource.BRICK));
        assertEquals("Player should have 4 sheep remaining", 4, player.getResource(Resource.SHEEP));
        assertEquals("Player should have 4 wheat remaining", 4, player.getResource(Resource.WHEAT));

        // Verify the player's victory points increased
        assertEquals("Player should have 1 victory point", 1, player.getVictoryPoints());
    }

    @Test
    public void testBuildCity() {
        Player player = gameController.getGame().getCurrentPlayer();

        // Add resources needed to build a city
        player.addResource(Resource.WHEAT, 2);
        player.addResource(Resource.ORE, 3);

        // Check player resources before building a city
        assertTrue("Player should be able to build city", player.canBuildCity());

        // Simulate building a city
        gameController.buildCity(player);

        // Verify the player has one less resource of each type
        assertEquals("Player should have 4 wheat remaining", 4, player.getResource(Resource.WHEAT));
        assertEquals("Player should have 2 ore remaining", 2, player.getResource(Resource.ORE));

        // Verify the player's victory points increased
        assertEquals("Player should have 2 victory points after upgrading a settlement to a city", 2, player.getVictoryPoints());
    }

    @Test
    public void testBuildRoad() {
        Player player = gameController.getGame().getCurrentPlayer();

        // Check player resources before building a road
        assertTrue("Player should be able to build road", player.canBuildRoad());

        // Simulate building a road
        gameController.buildRoad(player);

        // Verify the player has one less resource of each type
        assertEquals("Player should have 4 wood remaining", 4, player.getResource(Resource.WOOD));
        assertEquals("Player should have 4 brick remaining", 4, player.getResource(Resource.BRICK));
    }

    @Test
    public void testPrintGameSummary() {
        // Add some actions to test summary
        Player player = gameController.getGame().getCurrentPlayer();
        player.buildSettlement(gameController.getGame().getBoard().getHexTiles().get(0));

        // Capture the printed summary (useful if you mock the output stream)
        gameController.printGameSummary();

        // Validate the player's summary data
        assertEquals("Player should have 1 settlement", 1, player.getSettlements());
        assertEquals("Player should have 1 victory point", 1, player.getVictoryPoints());
    }
}
