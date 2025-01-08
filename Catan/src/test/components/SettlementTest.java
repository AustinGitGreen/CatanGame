package test.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import catan.players.Player;
import catan.board.Intersection;
import catan.components.Settlement;

public class SettlementTest {
    private Player player;
    private Intersection location;
    private Settlement settlement;

    @Before
    public void setUp() {
        // Set up mock Player and Intersection objects
        player = new Player("Alice");
        location = new Intersection(0, 1); // Example location
        settlement = new Settlement(player, location);
    }

    @Test
    public void testSettlementInitialization() {
        // Test that the settlement initializes correctly
        assertEquals("Owner should be Alice", player, settlement.getOwner());
        assertEquals("Location should match the given intersection", location, settlement.getLocation());
        assertEquals("Victory Points should be 1", 1, settlement.getVictoryPoints());
    }

    @Test
    public void testOwner() {
        // Test that the owner is correct
        assertEquals("Owner should be Alice", player, settlement.getOwner());
    }

    @Test
    public void testLocation() {
        // Test that the location is correct
        assertEquals("Location should match the given intersection", location, settlement.getLocation());
    }

    @Test
    public void testVictoryPoints() {
        // Test that settlements are worth 1 Victory Point
        assertEquals("Victory Points should be 1", 1, settlement.getVictoryPoints());
    }
}
