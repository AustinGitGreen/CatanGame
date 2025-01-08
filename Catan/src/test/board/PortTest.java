package test.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import catan.board.Port;
import resources.Resource;

public class PortTest {
    private Port genericPort;
    private Port specificPort;

    @Before
    public void setUp() {
        // Set up a generic port and a specific port for testing
        genericPort = new Port(null, 3); // 3:1 generic port
        specificPort = new Port(Resource.WOOD, 2); // 2:1 specific port for wood
    }

    @Test
    public void testGenericPortInitialization() {
        // Validate initialization of a generic port
        assertTrue("Generic port should be identified as generic", genericPort.isGenericPort());
        assertEquals("Trade ratio for generic port should be 3", 3, genericPort.getTradeRatio());
        assertEquals("Generic port resource should be null", null, genericPort.getResource());
    }

    @Test
    public void testSpecificPortInitialization() {
        // Validate initialization of a specific port
        assertFalse("Specific port should not be identified as generic", specificPort.isGenericPort());
        assertEquals("Trade ratio for specific port should be 2", 2, specificPort.getTradeRatio());
        assertEquals("Specific port resource should be WOOD", Resource.WOOD, specificPort.getResource());
    }

    @Test
    public void testGenericPortBehavior() {
        // Ensure a generic port behaves as expected
        assertTrue("Generic port should allow trading any resource", genericPort.isGenericPort());
    }

    @Test
    public void testSpecificPortBehavior() {
        // Ensure a specific port behaves as expected
        assertFalse("Specific port should not allow trading any resource", specificPort.isGenericPort());
        assertEquals("Specific port should allow trading WOOD", Resource.WOOD, specificPort.getResource());
    }
}
