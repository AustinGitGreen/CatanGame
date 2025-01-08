package test.players;

import catan.players.Player;
import catan.board.HexTile;
import catan.resources.Resource;
import catan.resources.DevelopmentCardType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlayerTest {
    private Player player;
    private HexTile tile;

    @Before
    public void setUp() {
        player = new Player();
        tile = new HexTile(Resource.WOOD, 5);
    }

    @Test
    public void testAddResource() {
        player.addResource(Resource.WOOD, 3);
        assertEquals("Player should have 3 wood", 3, player.getResource(Resource.WOOD));
        
        player.addResource(Resource.WOOD, 2);
        assertEquals("Player should have 5 wood", 5, player.getResource(Resource.WOOD));
    }

    @Test
    public void testRemoveResource() {
        player.addResource(Resource.BRICK, 5);
        player.removeResource(Resource.BRICK, 2);
        assertEquals("Player should have 3 brick after removing 2", 3, player.getResource(Resource.BRICK));

        player.removeResource(Resource.BRICK, 5); // Remove more than available
        assertEquals("Player should not have negative resources", 0, player.getResource(Resource.BRICK));
    }

    @Test
    public void testCanBuildSettlement() {
        player.addResource(Resource.WOOD, 1);
        player.addResource(Resource.BRICK, 1);
        player.addResource(Resource.SHEEP, 1);
        player.addResource(Resource.WHEAT, 1);
        
        assertTrue("Player should be able to build a settlement", player.canBuildSettlement());
    }

    @Test
    public void testBuildSettlement() {
        player.addResource(Resource.WOOD, 1);
        player.addResource(Resource.BRICK, 1);
        player.addResource(Resource.SHEEP, 1);
        player.addResource(Resource.WHEAT, 1);

        player.buildSettlement(tile);
        
        assertEquals("Player should have 1 settlement", 1, player.getSettlements());
        assertEquals("Player should have 1 victory point after building a settlement", 1, player.getVictoryPoints());
    }

    @Test
    public void testCanBuildCity() {
        player.addResource(Resource.WHEAT, 2);
        player.addResource(Resource.ORE, 3);
        
        assertTrue("Player should be able to build a city", player.canBuildCity());
    }

    @Test
    public void testBuildCity() {
        // Add resources for a city
        player.addResource(Resource.WHEAT, 2);
        player.addResource(Resource.ORE, 3);

        // Add resources and build a settlement first, as required to build a city
        player.addResource(Resource.WOOD, 1);
        player.addResource(Resource.BRICK, 1);
        player.addResource(Resource.SHEEP, 1);
        player.addResource(Resource.WHEAT, 1);
        HexTile settlementTile = new HexTile(Resource.WOOD, 5);
        player.buildSettlement(settlementTile); // Place a settlement

        // Now attempt to build a city
        player.buildCity();

        // Assertions
        assertEquals("Player should have 1 city", 1, player.getCities());
        assertEquals("Player should have 2 victory points after building a city", 2, player.getVictoryPoints());
    }


    @Test
    public void testIncrementVictoryPoints() {
        player.incrementVictoryPoints();
        assertEquals("Player should have 1 victory point after increment", 1, player.getVictoryPoints());
    }

    @Test
    public void testDiscardHalfResources() {
        player.addResource(Resource.WOOD, 4);
        player.addResource(Resource.BRICK, 4);
        
        int totalBefore = player.getTotalResources();
        player.discardHalfResources();
        
        int totalAfter = player.getTotalResources();
        assertEquals("Player should have discarded half of their resources", totalBefore / 2, totalAfter);
    }

    @Test
    public void testAddDevelopmentCard() {
        player.addDevelopmentCard(DevelopmentCardType.KNIGHT);
        assertTrue("Player should have a knight card", player.hasDevelopmentCard(DevelopmentCardType.KNIGHT));
    }

    @Test
    public void testUseDevelopmentCard() {
        player.addDevelopmentCard(DevelopmentCardType.KNIGHT);
        player.useDevelopmentCard(DevelopmentCardType.KNIGHT);
        
        assertFalse("Player should no longer have the knight card after using it", player.hasDevelopmentCard(DevelopmentCardType.KNIGHT));
    }

    @Test
    public void testGetTotalResources() {
        player.addResource(Resource.WOOD, 2);
        player.addResource(Resource.BRICK, 3);
        
        assertEquals("Player should have 5 total resources", 5, player.getTotalResources());
    }

    @Test
    public void testStealRandomResource() {
        // Add 1 unit of SHEEP to the player's resources
        player.addResource(Resource.SHEEP, 1);

        // Steal a random resource
        Resource stolenResource = player.stealRandomResource();

        // Assertions
        assertNotNull("Should steal a resource", stolenResource);
        assertEquals("Player should have 0 SHEEP after stealing", 0, player.getResource(Resource.SHEEP));
        assertEquals("Stolen resource should be SHEEP", Resource.SHEEP, stolenResource);
    }
}

