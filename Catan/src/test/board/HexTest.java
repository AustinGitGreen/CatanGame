package test.board;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import catan.board.Hex;
import resources.Resource;

public class HexTest {
    private Hex hex;

    @Before
    public void setUp() {
        hex = new Hex(Resource.WOOD, 5); // Example: A forest hex with number token 5
    }

    @Test
    public void testHexInitialization() {
        assertEquals("Resource should be WOOD", Resource.WOOD, hex.getResource());
        assertEquals("Number token should be 5", 5, hex.getNumberToken());
    }

    @Test
    public void testSetResource() {
        hex.setResource(Resource.BRICK);
        assertEquals("Resource should now be BRICK", Resource.BRICK, hex.getResource());
    }

    @Test
    public void testSetNumberToken() {
        hex.setNumberToken(8);
        assertEquals("Number token should now be 8", 8, hex.getNumberToken());
    }
}
