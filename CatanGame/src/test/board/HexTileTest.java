package test.board;

import catan.board.HexTile;
import catan.players.Player;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HexTileTest {
    private HexTile woodTile;
    private HexTile desertTile;
    private Player player1;
    private Player player2;

    @Before
    public void setUp() {
        woodTile = new HexTile(Resource.WOOD, 5);     // A wood tile with a number token
        desertTile = new HexTile(Resource.DESERT, 0); // A desert tile with no number token
        player1 = new Player();
        player2 = new Player();
    }

    @Test
    public void testResourceTypeAndNumberToken() {
        // Test resource type and number token for a wood tile
        assertEquals("The resource type should be WOOD", Resource.WOOD, woodTile.getResourceType());
        assertEquals("The number token should be 5", 5, woodTile.getNumberToken());

        // Test resource type and number token for a desert tile
        assertEquals("The resource type should be DESERT", Resource.DESERT, desertTile.getResourceType());
        assertEquals("The number token for desert should be 0", 0, desertTile.getNumberToken());
    }

    @Test
    public void testIsDesert() {
        // Check if isDesert correctly identifies desert and non-desert tiles
        assertTrue("The tile should be marked as desert", desertTile.isDesert());
        assertFalse("The tile should not be marked as desert", woodTile.isDesert());
    }

    @Test
    public void testSetNumberToken() {
        // Set a new number token for woodTile and verify it
        woodTile.setNumberToken(8);
        assertEquals("The number token should be updated to 8", 8, woodTile.getNumberToken());
    }

    @Test
    public void testAddAndCheckSettlement() {
        // Add settlements and verify that hasSettlement correctly identifies them
        woodTile.addSettlement(player1);
        assertTrue("Player 1 should have a settlement on woodTile", woodTile.hasSettlement(player1));
        assertFalse("Player 2 should not have a settlement on woodTile", woodTile.hasSettlement(player2));

        // Add another settlement and check
        woodTile.addSettlement(player2);
        assertTrue("Player 2 should now have a settlement on woodTile", woodTile.hasSettlement(player2));
    }

    @Test
    public void testToString() {
        // Verify the toString output for debugging purposes
        assertEquals("HexTile { resourceType='WOOD', numberToken=5 }", woodTile.toString());
        assertEquals("HexTile { resourceType='DESERT', numberToken=0 }", desertTile.toString());
    }
}
