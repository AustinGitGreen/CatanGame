package test.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import catan.board.Intersection;
import catan.components.City;
import catan.players.Player;

public class CityTest {
    private City city;
    private Player player;
    private Intersection location;

    @Before
    public void setUp() {
        player = new Player("Alice");
        location = new Intersection(1, 2); // Example intersection
        city = new City(player, location);
    }

    @Test
    public void testCityInitialization() {
        assertEquals("City owner should be Alice", player, city.getOwner());
        assertEquals("City location should match the given intersection", location, city.getLocation());
    }

    @Test
    public void testVictoryPoints() {
        assertEquals("City should be worth 2 Victory Points", 2, city.getVictoryPoints());
    }
}
