package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import catan.main.Game;
import catan.players.Player;
import catan.resources.Resource;

public class GameTest {
    private Game game;

    private void completeSetupPhase() {
        while (game.isSetupPhase()) {
            Player current = game.getCurrentPlayer();
            List<Integer> settlements = game.getValidSettlementPlacements(current, true);
            game.placeSetupSettlement(current, settlements.get(0));

            List<Integer> roads = game.getValidRoadPlacements(current, true);
            game.placeSetupRoad(current, roads.get(0));
        }
    }

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
        completeSetupPhase();

        Player currentPlayer = game.getCurrentPlayer();
        game.endTurn(); // Move to the next player's turn
        Player nextPlayer = game.getCurrentPlayer();

        assertTrue("Current player should change after ending the turn", currentPlayer != nextPlayer);
    }
    @Test
    public void testBuildCityUpgradesSettlement() {
        completeSetupPhase();

        Player player = game.getCurrentPlayer();
        player.getInventory().addResource(Resource.ORE, 3);
        player.getInventory().addResource(Resource.WHEAT, 2);

        int intersectionIndex = game.getValidCityPlacements(player).get(0);
        catan.components.City city = game.buildCity(player, intersectionIndex);

        assertNotNull("City should be created", city);
        assertEquals("Player should have one city after upgrade", 1, player.getCities().size());
        assertEquals("Player should have one fewer settlement after upgrade", 1, player.getSettlements().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildCityRequiresSettlement() {
        completeSetupPhase();

        Player player = game.getCurrentPlayer();
        player.getInventory().addResource(Resource.ORE, 3);
        player.getInventory().addResource(Resource.WHEAT, 2);

        List<Integer> options = game.getValidCityPlacements(player);
        int invalidIndex = -1;
        for (int i = 0; i < game.getBoard().getIntersections().size(); i++) {
            if (!options.contains(i)) {
                invalidIndex = i;
                break;
            }
        }

        game.buildCity(player, invalidIndex);
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildCityRequiresCurrentPlayer() {
        completeSetupPhase();

        Player current = game.getCurrentPlayer();
        Player notCurrent = game.getPlayers().get(1);
        if (notCurrent == current) {
            notCurrent = game.getPlayers().get(2);
        }

        current.getInventory().addResource(Resource.ORE, 3);
        current.getInventory().addResource(Resource.WHEAT, 2);

        int intersectionIndex = game.getValidCityPlacements(current).get(0);
        game.buildCity(notCurrent, intersectionIndex);
    }

}
