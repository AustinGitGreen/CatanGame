package test.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import board.Hex;
import resources.Resource;
import resources.Robber;

public class RobberTest {
    private Robber robber;
    private Hex desertHex;
    private Hex forestHex;

    @Before
    public void setUp() {
        desertHex = new Hex(Resource.DESERT, 0); // Desert tile, no resource
        forestHex = new Hex(Resource.WOOD, 5);  // Forest tile with number token 5
        robber = new Robber(desertHex);
    }

    @Test
    public void testRobberInitialization() {
        assertEquals("Robber should start on the desert hex", desertHex, robber.getCurrentHex());
    }

    @Test
    public void testRobberMovement() {
        robber.moveTo(forestHex);
        assertEquals("Robber should now be on the forest hex", forestHex, robber.getCurrentHex());
    }

    @Test
    public void testBlocksResourceProduction() {
        assertTrue("Robber should block resource production on the desert hex", robber.blocksResourceProduction(desertHex));
    }
}
