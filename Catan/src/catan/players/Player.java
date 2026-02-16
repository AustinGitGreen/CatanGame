package catan.players;

import catan.components.City;
import catan.components.Road;
import catan.components.Settlement;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game.
 */
public class Player {
    private String name;
    private int victoryPoints;
    private List<Settlement> settlements;
    private List<City> cities;
    private List<Road> roads;

    // Economy: each player has a resource inventory
    private Inventory inventory;

    public Player(String name) {
        this.name = name;
        this.victoryPoints = 0;
        this.settlements = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.roads = new ArrayList<>();
        this.inventory = new Inventory();
    }

    public String getName() {
        return name;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public List<Settlement> getSettlements() {
        return settlements;
    }

    public List<City> getCities() {
        return cities;
    }

    public List<Road> getRoads() {
        return roads;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void addSettlement(Settlement settlement) {
        settlements.add(settlement);
        victoryPoints += 1;
    }

    public void upgradeSettlementToCity(Settlement settlement, City city) {
        settlements.remove(settlement);
        cities.add(city);
        victoryPoints += 1;
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    public void addVictoryPoints(int i) {
        this.victoryPoints += i;
    }
}
