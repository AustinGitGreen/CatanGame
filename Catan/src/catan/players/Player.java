package catan.players;

import catan.board.Intersection;
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

    /**
     * Constructs a Player with a given name.
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.victoryPoints = 0;
        this.settlements = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.roads = new ArrayList<>();
    }

    /**
     * Gets the name of the player.
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the victory points of the player.
     * @return The victory points of the player.
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Gets the list of settlements owned by the player.
     * @return The list of settlements.
     */
    public List<Settlement> getSettlements() {
        return settlements;
    }

    /**
     * Gets the list of cities owned by the player.
     * @return The list of cities.
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     * Gets the list of roads owned by the player.
     * @return The list of roads.
     */
    public List<Road> getRoads() {
        return roads;
    }

    /**
     * Adds a settlement to the player's assets and awards 1 victory point.
     * @param settlement The settlement to add.
     */
    public void addSettlement(Settlement settlement) {
        settlements.add(settlement);
        victoryPoints += 1;
    }

    /**
     * Upgrades a settlement to a city, removing the settlement and adding the city.
     * Awards 1 additional victory point.
     * @param settlement The settlement to upgrade.
     * @param city The city to replace the settlement.
     */
    public void upgradeSettlementToCity(Settlement settlement, City city) {
        settlements.remove(settlement);
        cities.add(city);
        victoryPoints += 1;
    }

    /**
     * Adds a road to the player's assets.
     * @param road The road to add.
     */
    public void addRoad(Road road) {
        roads.add(road);
    }

    public void addVictoryPoints(int i) {
        this.victoryPoints += i;
    }
}
