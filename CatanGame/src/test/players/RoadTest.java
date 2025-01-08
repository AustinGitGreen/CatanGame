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
    private Road road4;

    @Before
    public void setUp() {
        player1 = new Player();
        player2 = new Player();
        road1 = new Road(player1);
        road2 = new Road(player1);
        road3 = new Road(player2);
        road4 = new Road(player1);
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
    public void testConnectingRoads() {
        // Connect roads bidirectionally
        road1.connectRoad(road2);
        road1.connectRoad(road3);

        // Verify connections
        assertTrue("Road1 should be connected to Road2", road1.isConnectedTo(road2));
        assertTrue("Road1 should be connected to Road3", road1.isConnectedTo(road3));
        assertTrue("Road2 should be connected to Road1", road2.isConnectedTo(road1));
        assertTrue("Road3 should be connected to Road1", road3.isConnectedTo(road1));
    }

    @Test
    public void testDisconnectingRoads() {
        // Connect and then disconnect roads
        road1.connectRoad(road2);
        road1.connectRoad(road3);

        // Remove connection by creating a new road without linking back
        road1.getConnectedRoads().remove(road2);
        road2.getConnectedRoads().remove(road1);

        // Verify connections after removal
        assertFalse("Road1 should not be connected to Road2", road1.isConnectedTo(road2));
        assertFalse("Road2 should not be connected to Road1", road2.isConnectedTo(road1));
        assertTrue("Road1 should still be connected to Road3", road1.isConnectedTo(road3));
    }

    @Test
    public void testMultipleConnections() {
        // Connect multiple roads to road1
        road1.connectRoad(road2);
        road1.connectRoad(road3);
        road1.connectRoad(road4);

        // Verify all connections
        assertTrue("Road1 should be connected to Road2", road1.isConnectedTo(road2));
        assertTrue("Road1 should be connected to Road3", road1.isConnectedTo(road3));
        assertTrue("Road1 should be connected to Road4", road1.isConnectedTo(road4));

        // Verify bidirectional connections
        assertTrue("Road2 should be connected to Road1", road2.isConnectedTo(road1));
        assertTrue("Road3 should be connected to Road1", road3.isConnectedTo(road1));
        assertTrue("Road4 should be connected to Road1", road4.isConnectedTo(road1));
    }

    @Test
    public void testNoConnectionsInitially() {
        // Verify that new roads have no connections
        assertTrue("Road1 should have no connections initially", road1.getConnectedRoads().isEmpty());
        assertTrue("Road2 should have no connections initially", road2.getConnectedRoads().isEmpty());
    }
}
