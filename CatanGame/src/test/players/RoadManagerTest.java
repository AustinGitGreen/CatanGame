package test.players;

import catan.players.Player;
import catan.players.Road;
import catan.players.RoadManager;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoadManagerTest {
    private RoadManager roadManager;
    private Player player1;
    private Player player2;
    private Road road1, road2, road3, road4, road5, road6;

    @Before
    public void setUp() {
        roadManager = new RoadManager();
        player1 = new Player();
        player2 = new Player();

        // Start each player with enough resources to build multiple roads
        player1.addResource(Resource.WOOD, 10);
        player1.addResource(Resource.BRICK, 10);
        player2.addResource(Resource.WOOD, 10);
        player2.addResource(Resource.BRICK, 10);

        // Initialize roads
        road1 = new Road(player1);
        road2 = new Road(player1);
        road3 = new Road(player1);
        road4 = new Road(player1);
        road5 = new Road(player1);
        road6 = new Road(player2); // Road belonging to another player

        // Connect roads for a contiguous path
        road1.setConnectedRoad1(road2);
        road2.setConnectedRoad1(road1);
        road2.setConnectedRoad2(road3);
        road3.setConnectedRoad1(road2);
        road3.setConnectedRoad2(road4);
        road4.setConnectedRoad1(road3);
        road4.setConnectedRoad2(road5);
        road5.setConnectedRoad1(road4);
    }

    @Test
    public void testAddRoad() {
        roadManager.addRoad(player1, road1);
        assertEquals("Road1 should be owned by player1", player1, road1.getOwner());
        assertEquals("Player1 should have 1 road", 1, player1.getRoads());
    }

    @Test
    public void testCalculateLongestRoadWithConnectedRoads() {
        // Add roads to the RoadManager
        roadManager.addRoad(player1, road1);
        roadManager.addRoad(player1, road2);
        roadManager.addRoad(player1, road3);
        roadManager.addRoad(player1, road4);
        roadManager.addRoad(player1, road5);

        int longestRoad = roadManager.calculateLongestRoad(player1);

        assertEquals("Player1 should have a longest road of length 5", 5, longestRoad);
        assertTrue("Player1 should have the longest road", player1.hasLongestRoad());
        assertEquals("Player1 should have a total of 5 roads", 5, player1.getRoads());
    }

    @Test
    public void testCalculateLongestRoadWithDisconnectedRoads() {
        // Add 3 connected roads (road1, road2, road3)
        roadManager.addRoad(player1, road1);
        roadManager.addRoad(player1, road2);
        roadManager.addRoad(player1, road3);

        // Connect road1 -> road2 -> road3 in sequence
        road1.setConnectedRoad1(road2);
        road2.setConnectedRoad1(road1);
        road2.setConnectedRoad2(road3);
        road3.setConnectedRoad1(road2);

        // Add a disconnected road (road5) for player1
        roadManager.addRoad(player1, road5); // Road not connected to others

        int longestRoad = roadManager.calculateLongestRoad(player1);

        assertEquals("Player1 should have a longest road of length 3", 3, longestRoad);
        assertFalse("Player1 should not have the longest road", player1.hasLongestRoad());
        assertEquals("Player1 should have a total of 4 roads", 4, player1.getRoads());
    }

    @Test
    public void testCalculateLongestRoadForMultiplePlayers() {
        // Add roads for player1
        roadManager.addRoad(player1, road1);
        roadManager.addRoad(player1, road2);
        roadManager.addRoad(player1, road3);

        // Add roads for player2
        road6.setConnectedRoad1(new Road(player2)); // Additional connected road for player2
        roadManager.addRoad(player2, road6);
        
        //TODO fix road problem
        int longestRoadPlayer1 = roadManager.calculateLongestRoad(player1);
        int longestRoadPlayer2 = roadManager.calculateLongestRoad(player2);

        assertEquals("Player1 should have a longest road of length 3", 3, longestRoadPlayer1);
        assertEquals("Player2 should have a longest road of length 2", 2, longestRoadPlayer2);
        assertFalse("Player1 should not have the longest road status", player1.hasLongestRoad());
        assertFalse("Player2 should not have the longest road status", player2.hasLongestRoad());
    }
}
