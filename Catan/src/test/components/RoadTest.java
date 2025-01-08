package test.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Road;
import catan.players.Player;

public class RoadTest {
    private Road road;
    private Player player;
    private Edge edge;

    @Before
    public void setUp() {
        player = new Player("Bob");
        Intersection start = new Intersection(0, 0);
        Intersection end = new Intersection(0, 1);
        edge = new Edge(start, end);
        road = new Road(player, edge);
    }

    @Test
    public void testRoadInitialization() {
        assertEquals("Road owner should be Bob", player, road.getOwner());
        assertEquals("Road edge should match the given edge", edge, road.getEdge());
    }
}
