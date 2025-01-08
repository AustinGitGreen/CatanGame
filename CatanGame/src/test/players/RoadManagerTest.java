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
    }

    @Test
    public void testAddRoad() {
        roadManager.addRoad(player1, road1);
        assertEquals("Road1 should be owned by player1", player1, road1.getOwner());
        assertEquals("Player1 should have 1 road", 1, player1.getRoads());
    }

    @Test
    public void testCalculateLongestRoadWithConnectedRoads() {
        // Connect roads for Player 1
        road1.connectRoad(road2);
        road2.connectRoad(road3);
        road3.connectRoad(road4);
        road4.connectRoad(road5);

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
        // Connect road1 -> road2 -> road3 in sequence
        road1.connectRoad(road2);
        road2.connectRoad(road3);

        // Add roads to the RoadManager
        roadManager.addRoad(player1, road1);
        roadManager.addRoad(player1, road2);
        roadManager.addRoad(player1, road3);

        // Add a disconnected road (road5) for Player 1
        roadManager.addRoad(player1, road5);

        int longestRoad = roadManager.calculateLongestRoad(player1);

        assertEquals("Player1 should have a longest road of length 3", 3, longestRoad);
        assertFalse("Player1 should not have the longest road", player1.hasLongestRoad());
        assertEquals("Player1 should have a total of 4 roads", 4, player1.getRoads());
    }

    @Test
    public void testCalculateLongestRoadForMultiplePlayers() {
        // Connect Player 1's roads
        road1.connectRoad(road2);
        road2.connectRoad(road3);

        // Add Player 1's roads
        roadManager.addRoad(player1, road1);
        roadManager.addRoad(player1, road2);
        roadManager.addRoad(player1, road3);

        // Connect Player 2's roads
        Road player2Road = new Road(player2);
        road6.connectRoad(player2Road);

        // Add Player 2's roads
        roadManager.addRoad(player2, road6);
        roadManager.addRoad(player2, player2Road);

        // Calculate longest roads
        int longestRoadPlayer1 = roadManager.calculateLongestRoad(player1);
        int longestRoadPlayer2 = roadManager.calculateLongestRoad(player2);

        // Assertions
        assertEquals("Player1 should have a longest road of length 3", 3, longestRoadPlayer1);
        assertEquals("Player2 should have a longest road of length 2", 2, longestRoadPlayer2);
        assertFalse("Player1 should not have the longest road status", player1.hasLongestRoad());
        assertFalse("Player2 should not have the longest road status", player2.hasLongestRoad());
    }

    @Test
    public void testCalculateLongestRoadWithBranching() {
        // Create a branching structure for Player 1
        road1.connectRoad(road2);
        road2.connectRoad(road3);
        road3.connectRoad(road4);
        road2.connectRoad(road5); // Branch from road2 to road5

        // Add roads to the RoadManager
        roadManager.addRoad(player1, road1);
        roadManager.addRoad(player1, road2);
        roadManager.addRoad(player1, road3);
        roadManager.addRoad(player1, road4);
        roadManager.addRoad(player1, road5);

        // Calculate longest road
        int longestRoad = roadManager.calculateLongestRoad(player1);

        // Assertions
        assertEquals("Player1 should have a longest road of length 4", 4, longestRoad);
        assertFalse("Player1 should not have the longest road (requires length >= 5)", player1.hasLongestRoad());
    }

}
