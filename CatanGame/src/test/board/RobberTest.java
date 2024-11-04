package test.board;

import catan.board.HexTile;
import catan.board.Robber;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RobberTest {
    private HexTile startingTile;
    private HexTile newTile;
    private Robber robber;

    @Before
    public void setUp() {
        // Initialize two tiles: a starting tile and a new tile
        startingTile = new HexTile(Resource.DESERT, 0); // Desert tile as the initial location
        newTile = new HexTile(Resource.WOOD, 5);        // Wood tile as the target location
        robber = new Robber(startingTile);              // Initialize the robber on the starting tile
    }

    @Test
    public void testInitialization() {
        // Verify that the robber is initially placed on the starting tile
        assertEquals("Robber should start on the starting tile", startingTile, robber.getCurrentTile());
    }

    @Test
    public void testMove() {
        // Move the robber to a new tile and verify that the position is updated
        robber.move(newTile);
        assertEquals("Robber should be on the new tile after moving", newTile, robber.getCurrentTile());
    }

    @Test
    public void testMoveBackToStartingTile() {
        // Move the robber to the new tile, then move it back to the starting tile
        robber.move(newTile);
        assertEquals("Robber should be on the new tile after moving", newTile, robber.getCurrentTile());
        
        robber.move(startingTile);
        assertEquals("Robber should return to the starting tile", startingTile, robber.getCurrentTile());
    }
}
