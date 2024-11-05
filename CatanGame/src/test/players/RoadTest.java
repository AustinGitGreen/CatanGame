package test.players;

import catan.players.Player;
import catan.players.Road;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoadTest {
    private Player player1;
    private Player player2;
    private Road road1;
    private Road road2;
    private Road road3;

    @Before
    public void setUp() {
        player1 = new Player();
        player2 = new Player();
        road1 = new Road(player1);
        road2 = new Road(player1);
        road3 = new Road(player2);
    }

    @Test
    public void testRoadOwnership() {
        // Check initial ownership of road
        assertEquals("Road should initially be owned by player1", player1, road1.getOwner());

        // Change the ownership of the road
        road1.setOwner(player2);
        assertEquals("Road ownership should change to player2", player2, road1.getOwner());
    }

    @Test
    public void testSettingConnectedRoads() {
        // Set connections between roads
        road1.setConnectedRoad1(road2);
        road1.setConnectedRoad2(road3);

        // Verify connections
        assertEquals("Road1 should be connected to Road2", road2, road1.getConnectedRoad1());
        assertEquals("Road1 should be connected to Road3", road3, road1.getConnectedRoad2());
    }

    @Test
    public void testIsConnectedTo() {
        // Connect roads
        road1.setConnectedRoad1(road2);
        road1.setConnectedRoad2(road3);

        // Test if road1 is connected to road2 and road3
        assertTrue("Road1 should be connected to Road2", road1.isConnectedTo(road2));
        assertTrue("Road1 should be connected to Road3", road1.isConnectedTo(road3));

        // Test if road1 is not connected to a new road
        Road road4 = new Road(player1);
        assertFalse("Road1 should not be connected to Road4", road1.isConnectedTo(road4));
    }
}
