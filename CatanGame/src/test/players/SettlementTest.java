package test.players;

import static org.junit.Assert.*;

import catan.board.HexTile;
import catan.players.Player;
import catan.players.Settlement;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

public class SettlementTest {

    private Player player;
    private HexTile tile;
    private Settlement settlement;

    @Before
    public void setUp() {
        player = new Player();
        tile = new HexTile(Resource.WOOD, 5); // Tile with wood resource and number token 5
        settlement = new Settlement(player, tile);
    }

    @Test
    public void testSettlementInitialization() {
        assertEquals("Settlement should belong to the correct player", player, settlement.getOwner());
        assertEquals("Settlement should be on the correct tile", tile, settlement.getLocation());
        assertFalse("Settlement should not be a city initially", settlement.isCity());
    }

    @Test
    public void testUpgradeToCity() {
        int initialVictoryPoints = player.getVictoryPoints();

        settlement.upgradeToCity();

        assertTrue("Settlement should be upgraded to a city", settlement.isCity());
        assertEquals("Player should gain 1 victory point after upgrading to a city", 
                     initialVictoryPoints + 1, player.getVictoryPoints());

        // Ensure upgrading twice does not add extra victory points
        settlement.upgradeToCity();
        assertEquals("Player's victory points should not change on redundant upgrade",
                     initialVictoryPoints + 1, player.getVictoryPoints());
    }
}
