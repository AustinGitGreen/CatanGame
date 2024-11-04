package test.resources;

import catan.players.Player;
import catan.resources.Bank;
import catan.resources.DevelopmentCard;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BankTest {
    private Bank bank;
    private Player player;

    @Before
    public void setUp() {
        bank = new Bank();
        player = new Player();
        player.addResource(Resource.WOOD, 4);
        player.addResource(Resource.BRICK, 4);
    }

    @Test
    public void testInitialization() {
        // Check initial quantities for each resource
        for (Resource resource : Resource.values()) {
            assertTrue("Bank should have at least 19 of each resource", bank.hasResource(resource, 19));
        }

        // Check total development card count (14 knights, 5 victory points, 2 each of other types)
        int totalCards = 25;
        for (int i = 0; i < totalCards; i++) {
            assertNotNull("The bank should have development cards to draw", bank.drawDevelopmentCard());
        }
        assertNull("The bank should have no development cards left", bank.drawDevelopmentCard());
    }

    @Test
    public void testDrawDevelopmentCard() {
        // Draw and count specific card types
        int knightCount = 0, victoryCount = 0, roadCount = 0, plentyCount = 0, monopolyCount = 0;
        DevelopmentCard card;
        
        while ((card = bank.drawDevelopmentCard()) != null) {
            switch (card.getType()) {
                case KNIGHT: knightCount++; break;
                case VICTORY_POINT: victoryCount++; break;
                case ROAD_BUILDING: roadCount++; break;
                case YEAR_OF_PLENTY: plentyCount++; break;
                case MONOPOLY: monopolyCount++; break;
            }
        }
        
        // Verify counts based on standard Catan deck setup
        assertEquals("Should be 14 KNIGHT cards", 14, knightCount);
        assertEquals("Should be 5 VICTORY_POINT cards", 5, victoryCount);
        assertEquals("Should be 2 ROAD_BUILDING cards", 2, roadCount);
        assertEquals("Should be 2 YEAR_OF_PLENTY cards", 2, plentyCount);
        assertEquals("Should be 2 MONOPOLY cards", 2, monopolyCount);
    }

    @Test
    public void testTradeWithPlayerSuccess() {
        // Successful trade: player trades 4 wood for 1 brick
        assertTrue("Trade should succeed", bank.tradeWithPlayer(player, Resource.WOOD, Resource.BRICK, 4));
        assertEquals("Player should have 0 wood remaining", 0, player.getResource(Resource.WOOD));
        assertEquals("Player should have 5 brick", 5, player.getResource(Resource.BRICK));
    }

    @Test
    public void testTradeWithPlayerFailureInsufficientResources() {
        // Failed trade due to insufficient wood
        player.removeResource(Resource.WOOD, 2); // Leave only 2 wood
        assertFalse("Trade should fail due to insufficient resources", bank.tradeWithPlayer(player, Resource.WOOD, Resource.BRICK, 4));
        assertEquals("Player should still have 2 wood", 2, player.getResource(Resource.WOOD));
        assertEquals("Player should have 4 brick", 4, player.getResource(Resource.BRICK));
    }

    @Test
    public void testTradeWithPlayerFailureBankLacksResource() {
        // Failed trade due to bank lacking brick (removing all brick from the bank)
        bank.removeResource(Resource.BRICK, 19); // Deplete all brick from the bank
        assertFalse("Trade should fail because the bank lacks the requested resource", bank.tradeWithPlayer(player, Resource.WOOD, Resource.BRICK, 4));
    }

    @Test
    public void testAddAndRemoveResources() {
        // Test adding resources to the bank
        bank.addResource(Resource.WHEAT, 5);
        assertTrue("Bank should have at least 24 wheat", bank.hasResource(Resource.WHEAT, 24));

        // Test removing resources from the bank
        bank.removeResource(Resource.WHEAT, 10);
        assertTrue("Bank should have at least 14 wheat after removal", bank.hasResource(Resource.WHEAT, 14));
    }
}
