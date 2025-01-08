package test.players;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import catan.players.Inventory;
import catan.players.Trade;
import catan.resources.Resource;
import catan.resources.ResourcePool;

public class TradeTest {
    private Inventory player1Inventory;
    private Inventory player2Inventory;
    private ResourcePool resourcePool;
    private Trade trade;

    @Before
    public void setUp() {
        player1Inventory = new Inventory();
        player2Inventory = new Inventory();
        resourcePool = new ResourcePool();

        // Add some resources to the inventories and resource pool for testing
        player1Inventory.addResource(Resource.WOOD, 4);
        player1Inventory.addResource(Resource.BRICK, 3);

        player2Inventory.addResource(Resource.WHEAT, 5);

        resourcePool.addResource(Resource.SHEEP, 10);
        resourcePool.addResource(Resource.WHEAT, 8);

        trade = new Trade(resourcePool);
    }

    @Test
    public void testPlayerToPlayerTradeSuccessful() {
        // Player 1 offers 2 WOOD, Player 2 offers 1 WHEAT
        Map<Resource, Integer> player1Offer = new HashMap<>();
        player1Offer.put(Resource.WOOD, 2);

        Map<Resource, Integer> player2Offer = new HashMap<>();
        player2Offer.put(Resource.WHEAT, 1);

        boolean result = trade.playerToPlayerTrade(player1Inventory, player1Offer, player2Inventory, player2Offer);

        assertTrue("Player-to-player trade should succeed", result);
        assertTrue("Player 1 should now have 1 WHEAT", player1Inventory.getResourceCount(Resource.WHEAT) == 1);
        assertTrue("Player 2 should now have 2 WOOD", player2Inventory.getResourceCount(Resource.WOOD) == 2);
    }

    @Test
    public void testPlayerToPlayerTradeInsufficientResources() {
        // Player 1 offers 5 WOOD, Player 2 offers 1 WHEAT (Player 1 doesn't have 5 WOOD)
        Map<Resource, Integer> player1Offer = new HashMap<>();
        player1Offer.put(Resource.WOOD, 5);

        Map<Resource, Integer> player2Offer = new HashMap<>();
        player2Offer.put(Resource.WHEAT, 1);

        boolean result = trade.playerToPlayerTrade(player1Inventory, player1Offer, player2Inventory, player2Offer);

        assertFalse("Player-to-player trade should fail due to insufficient resources", result);
    }

    @Test
    public void testPlayerToBankTradeSuccessful() {
        // Player 1 trades 4 WOOD for 1 SHEEP with the bank
        boolean result = trade.playerToBankTrade(player1Inventory, resourcePool, Resource.WOOD, 4, Resource.SHEEP, 1);

        assertTrue("Player-to-bank trade should succeed", result);
        assertTrue("Player 1 should now have 1 SHEEP", player1Inventory.getResourceCount(Resource.SHEEP) == 1);
        assertTrue("Player 1 should have 0 WOOD left", player1Inventory.getResourceCount(Resource.WOOD) == 0);
    }

    @Test
    public void testPlayerToBankTradeInsufficientResources() {
        // Player 1 tries to trade 4 BRICK for 1 SHEEP (Player 1 only has 3 BRICK)
        boolean result = trade.playerToBankTrade(player1Inventory, resourcePool, Resource.BRICK, 4, Resource.SHEEP, 1);

        assertFalse("Player-to-bank trade should fail due to insufficient resources", result);
    }

    @Test
    public void testPlayerToBankTradeBankHasInsufficientResources() {
        // Player 1 tries to trade 4 WOOD for 11 SHEEP (Bank only has 10 SHEEP)
        boolean result = trade.playerToBankTrade(player1Inventory, resourcePool, Resource.WOOD, 4, Resource.SHEEP, 11);

        assertFalse("Player-to-bank trade should fail due to bank's insufficient resources", result);
    }
}
