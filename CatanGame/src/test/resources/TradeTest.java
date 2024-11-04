package test.resources;

import catan.players.Player;
import catan.resources.Resource;
import catan.resources.Trade;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TradeTest {
    private Player player1;
    private Player player2;

    @Before
    public void setUp() {
        // Initialize two players and provide them with initial resources
        player1 = new Player();
        player2 = new Player();
        
        player1.addResource(Resource.WOOD, 5); // Player 1 has 5 wood
        player2.addResource(Resource.BRICK, 3); // Player 2 has 3 brick
    }

    @Test
    public void testPlayerToPlayerTradeSuccess() {
        // Successful trade: player1 trades 2 wood for player2's 2 brick
        assertTrue("Trade should succeed with sufficient resources", 
                   Trade.playerToPlayerTrade(player1, Resource.WOOD, 2, player2, Resource.BRICK, 2));
        
        // Verify updated resources for both players
        assertEquals("Player 1 should have 3 wood remaining", 3, player1.getResource(Resource.WOOD));
        assertEquals("Player 2 should have 1 brick remaining", 1, player2.getResource(Resource.BRICK));
        assertEquals("Player 1 should have 2 brick after trade", 2, player1.getResource(Resource.BRICK));
        assertEquals("Player 2 should have 2 wood after trade", 2, player2.getResource(Resource.WOOD));
    }

    @Test
    public void testPlayerToPlayerTradeFailureInsufficientResources() {
        // Attempt trade where player2 lacks sufficient brick
        assertFalse("Trade should fail due to insufficient resources from player2", 
                    Trade.playerToPlayerTrade(player1, Resource.WOOD, 2, player2, Resource.BRICK, 4));
        
        // Verify resources remain unchanged
        assertEquals("Player 1 should still have 5 wood", 5, player1.getResource(Resource.WOOD));
        assertEquals("Player 2 should still have 3 brick", 3, player2.getResource(Resource.BRICK));
    }

    @Test
    public void testPlayerToBankTradeSuccess() {
        // Give player1 enough brick for a 4:1 trade
        player1.addResource(Resource.BRICK, 4);

        // Successful trade: player1 trades 4 brick for 1 wheat with the bank
        assertTrue("Bank trade should succeed with sufficient resources", 
                   Trade.playerToBankTrade(player1, Resource.BRICK, Resource.WHEAT));

        // Verify updated resources
        assertEquals("Player 1 should have 0 brick remaining after trade", 0, player1.getResource(Resource.BRICK));
        assertEquals("Player 1 should gain 1 wheat from trade", 1, player1.getResource(Resource.WHEAT));
    }

    @Test
    public void testPlayerToBankTradeFailureInsufficientResources() {
        // Player1 has only 1 wood, which is insufficient for a 4:1 trade
    	player1.removeResource(Resource.WOOD, 4);  // Ensuring they only have
        assertFalse("Bank trade should fail due to insufficient resources", 
                    Trade.playerToBankTrade(player1, Resource.WOOD, Resource.SHEEP));

        // Verify resources remain unchanged
        assertEquals("Player 1 should still have 1 wood", 1, player1.getResource(Resource.WOOD));
        assertEquals("Player 1 should have 0 sheep", 0, player1.getResource(Resource.SHEEP));
    }
}
