package test.main;

import catan.board.HexTile;
import catan.main.VictoryPointManager;
import catan.players.Player;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VictoryPointManagerTest {
    private VictoryPointManager victoryPointManager;
    private Player player;
    private HexTile tile1;
    private HexTile tile2;

    @Before
    public void setUp() {
        victoryPointManager = new VictoryPointManager();
        player = new Player();

        // Create tiles to associate with settlements and cities
        tile1 = new HexTile(Resource.WOOD, 5);
        tile2 = new HexTile(Resource.BRICK, 8);
    }

    @Test
    public void testVictoryPointsWithSettlements() {
        // Add resources to build settlements
        player.addResource(Resource.WOOD, 2);
        player.addResource(Resource.BRICK, 2);
        player.addResource(Resource.SHEEP, 2);
        player.addResource(Resource.WHEAT, 2);

        // Build settlements on tiles
        player.buildSettlement(tile1);
        player.buildSettlement(tile2);

        // Calculate victory points
        int points = victoryPointManager.calculateVictoryPoints(player);

        assertEquals("Player should have 2 victory points for 2 settlements", 2, points);
    }

    @Test
    public void testVictoryPointsWithCities() {
        // Add resources to build settlements and upgrade to cities
        player.addResource(Resource.WOOD, 1);
        player.addResource(Resource.BRICK, 1);
        player.addResource(Resource.SHEEP, 1);
        player.addResource(Resource.WHEAT, 3);
        player.addResource(Resource.ORE, 3);

        // Build a settlement and upgrade it to a city
        player.buildSettlement(tile1);
        player.buildCity();

        // Calculate victory points
        int points = victoryPointManager.calculateVictoryPoints(player);

        assertEquals("Player should have 2 victory points for 1 city", 2, points);
    }

    @Test
    public void testVictoryPointsWithLargestArmyAndLongestRoad() {
        // Simulate conditions for largest army and longest road
        player.setLargestArmy(true);
        player.setLongestRoad(true);

        // Add resources and build settlements
        player.addResource(Resource.WOOD, 1);
        player.addResource(Resource.BRICK, 1);
        player.addResource(Resource.SHEEP, 1);
        player.addResource(Resource.WHEAT, 1);

        player.buildSettlement(tile1);

        // Calculate victory points
        int points = victoryPointManager.calculateVictoryPoints(player);

        assertEquals("Player should have 5 points (1 settlement, 2 for army, 2 for road)", 5, points);
    }
}
