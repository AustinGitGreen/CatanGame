package test.players;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import board.Intersection;
import components.City;
import components.Road;
import components.Settlement;
import players.Player;

public class PlayerTest {
    private Player player;

    @Before
    public void setUp() {
        player = new Player("Alice");
    }

    @Test
    public void testPlayerInitialization() {
        assertEquals("Player name should be Alice", "Alice", player.getName());
        assertEquals("Player should start with 0 Victory Points", 0, player.getVictoryPoints());
        assertEquals("Player should have no settlements initially", 0, player.getSettlements().size());
        assertEquals("Player should have no cities initially", 0, player.getCities().size());
        assertEquals("Player should have no roads initially", 0, player.getRoads().size());
    }

    @Test
    public void testAddSettlement() {
        Settlement settlement = new Settlement(player, new Intersection(0, 0));
        player.addSettlement(settlement);

        assertEquals("Player should have 1 settlement", 1, player.getSettlements().size());
        assertEquals("Player should gain 1 Victory Point", 1, player.getVictoryPoints());
    }

    @Test
    public void testUpgradeSettlementToCity() {
        Settlement settlement = new Settlement(player, new Intersection(0, 0));
        City city = new City(player, new Intersection(0, 0));
        player.addSettlement(settlement);
        player.upgradeSettlementToCity(settlement, city);

        assertEquals("Player should have no settlements after upgrade", 0, player.getSettlements().size());
        assertEquals("Player should have 1 city", 1, player.getCities().size());
        assertEquals("Player should have 2 Victory Points after upgrade", 2, player.getVictoryPoints());
    }

    @Test
    public void testAddRoad() {
        Road road = new Road(player, null);
        player.addRoad(road);

        assertEquals("Player should have 1 road", 1, player.getRoads().size());
    }
}
