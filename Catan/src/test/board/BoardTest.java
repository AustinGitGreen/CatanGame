package test.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import catan.resources.Resource;
import catan.board.Board;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
        board.initializeBoard();
    }

    @Test
    public void testHexInitialization() {
        assertEquals("There should be 19 hexes on the board", 19, board.getHexes().size());
        boolean hasDesert = board.getHexes().stream().anyMatch(hex -> hex.getResource() == Resource.DESERT);
        assertEquals("The board should include a desert tile", true, hasDesert);
    }

    @Test
    public void testIntersectionInitialization() {
        assertNotNull("Intersections should not be null", board.getIntersections());
        // Example: Adjust expected number of intersections based on your board size
        assertEquals("Expected number of intersections", 54, board.getIntersections().size());
    }

    @Test
    public void testEdgeInitialization() {
        assertNotNull("Edges should not be null", board.getEdges());
        // Example: Adjust expected number of edges based on your board size
        assertEquals("Expected number of edges", 72, board.getEdges().size());
    }

    @Test
    public void testRobberInitialization() {
        assertNotNull("Robber should be initialized", board.getRobber());
        assertEquals("Robber should start on the desert tile", Resource.DESERT, board.getRobber().getCurrentHex().getResource());
    }
}
