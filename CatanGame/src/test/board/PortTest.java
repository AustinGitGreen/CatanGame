package test.board;

import catan.board.Port;
import catan.players.Player;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PortTest {
    private Port woodPort;
    private Port generalPort;
    private Player player;

    @Before
    public void setUp() {
        // Initialize a specific port (2:1 wood) and a general port (3:1 any resource)
        woodPort = new Port(2, Resource.WOOD);
        generalPort = new Port(3, null);
        
        // Initialize a player and give them some resources
        player = new Player();
        player.addResource(Resource.WOOD, 5);
        player.addResource(Resource.BRICK, 4);
    }

    @Test
    public void testPortInitialization() {
        // Test specific port (2:1 wood)
        assertEquals("Trade ratio should be 2", 2, woodPort.getTradeRatio());
        assertEquals("Resource type should be WOOD", Resource.WOOD, woodPort.getResourceType());

        // Test general port (3:1 any resource)
        assertEquals("Trade ratio should be 3", 3, generalPort.getTradeRatio());
        assertNull("Resource type should be null for general port", generalPort.getResourceType());
    }

    @Test
    public void testTradeWithSpecificPort() {
        // Test successful trade at a 2:1 wood port
        assertTrue("Trade should succeed with sufficient resources", woodPort.trade(player, Resource.WOOD, Resource.SHEEP));
        assertEquals("Player should have 3 wood remaining", 3, player.getResource(Resource.WOOD));
        assertEquals("Player should gain 1 sheep", 1, player.getResource(Resource.SHEEP));
        assertTrue("Trade should succeed with sufficient resources", woodPort.trade(player, Resource.WOOD, Resource.SHEEP));

        // Test failed trade due to insufficient wood resources
        assertFalse("Trade should fail due to insufficient wood", woodPort.trade(player, Resource.WOOD, Resource.SHEEP));
    }

    @Test
    public void testTradeWithIncorrectResourceType() {
        // Attempt to trade brick at a 2:1 wood port, which should fail
        assertFalse("Trade should fail due to incorrect resource type", woodPort.trade(player, Resource.BRICK, Resource.SHEEP));
        assertEquals("Player should still have 4 bricks", 4, player.getResource(Resource.BRICK));
    }

    @Test
    public void testTradeWithGeneralPort() {
        // Test successful trade with any resource (3:1) at the general port
        assertTrue("Trade should succeed with sufficient resources", generalPort.trade(player, Resource.BRICK, Resource.WHEAT));
        assertEquals("Player should have 1 brick remaining", 1, player.getResource(Resource.BRICK));
        assertEquals("Player should gain 1 wheat", 1, player.getResource(Resource.WHEAT));

        // Test failed trade due to insufficient resources for a 3:1 trade
        assertFalse("Trade should fail due to insufficient resources", generalPort.trade(player, Resource.BRICK, Resource.WHEAT));
    }

    @Test
    public void testToString() {
        // Verify toString output for both specific and general ports
        assertEquals("2:1 Port (WOOD)", woodPort.toString());
        assertEquals("3:1 Port (Any resource)", generalPort.toString());
    }
}
