package catan.board;

import catan.players.Player;
import catan.resources.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HexTile {
    private Resource resourceType;  // Type of resource (WOOD, BRICK, SHEEP, WHEAT, ORE, DESERT)
    private int numberToken;        // Number token assigned to this tile (0 if it's a desert)
    private Map<Integer, SettlementOrCity> intersections; // Tracks ownership of each intersection (1-6)
    private List<HexTile> adjacentTiles; // List of adjacent tiles

    public HexTile(Resource resourceType, int numberToken) {
        this.resourceType = resourceType;
        this.numberToken = numberToken;
        this.intersections = new HashMap<>();
        this.adjacentTiles = new ArrayList<>();
    }

    // Represents a settlement or city on an intersection
    public static class SettlementOrCity {
        private Player owner;
        private boolean isCity; // true if it's a city, false if it's a settlement

        public SettlementOrCity(Player owner, boolean isCity) {
            this.owner = owner;
            this.isCity = isCity;
        }

        public Player getOwner() {
            return owner;
        }

        public boolean isCity() {
            return isCity;
        }

        public void upgradeToCity() {
            this.isCity = true;
        }
    }

    // Getters and setters for resource type and number token
    public Resource getResourceType() {
        return resourceType;
    }

    public int getNumberToken() {
        return numberToken;
    }

    public void setNumberToken(int numberToken) {
        this.numberToken = numberToken;
    }

    public boolean isDesert() {
        return resourceType == Resource.DESERT;
    }

    // Add an adjacent tile
    public void addAdjacentTile(HexTile tile) {
        if (!adjacentTiles.contains(tile)) {
            adjacentTiles.add(tile);
        }
    }

    // Get the list of adjacent tiles
    public List<HexTile> getAdjacentTiles() {
        return adjacentTiles;
    }

    // Place a settlement on a specific intersection
    public boolean placeSettlement(Player player, int intersection) {
        // Check if the intersection is already occupied
        if (intersections.containsKey(intersection)) {
            System.out.println("Intersection " + intersection + " is already occupied.");
            return false;
        }

        // Check the distance rule: adjacent intersections must not have settlements
        for (int adjacent : getAdjacentIntersections(intersection)) {
            if (intersections.containsKey(adjacent)) {
                System.out.println("Cannot place settlement. Adjacent intersection " + adjacent + " is occupied.");
                return false;
            }
        }

        // Place the settlement
        intersections.put(intersection, new SettlementOrCity(player, false));
        return true;
    }

    // Returns a list of adjacent intersections (for distance rule)
    private List<Integer> getAdjacentIntersections(int intersection) {
        // Define adjacency logic based on your hexagonal grid system
        // For example, intersection 1 might be adjacent to 2 and 6
        Map<Integer, List<Integer>> adjacencyMap = new HashMap<>();
        adjacencyMap.put(1, Arrays.asList(2, 6));
        adjacencyMap.put(2, Arrays.asList(1, 3));
        adjacencyMap.put(3, Arrays.asList(2, 4));
        adjacencyMap.put(4, Arrays.asList(3, 5));
        adjacencyMap.put(5, Arrays.asList(4, 6));
        adjacencyMap.put(6, Arrays.asList(1, 5));

        return adjacencyMap.getOrDefault(intersection, new ArrayList<>());
    }

    // Upgrade a settlement to a city on a specific intersection
    public boolean upgradeSettlementToCity(Player player, int intersection) {
        SettlementOrCity settlement = intersections.get(intersection);
        if (settlement != null && settlement.getOwner().equals(player) && !settlement.isCity()) {
            settlement.upgradeToCity();
            return true;
        }
        System.out.println("Cannot upgrade. No settlement exists for the player on intersection " + intersection);
        return false;
    }

    // Check if an intersection has a settlement or city owned by a player
    public boolean hasSettlementOrCity(Player player, int intersection) {
        SettlementOrCity settlementOrCity = intersections.get(intersection);
        return settlementOrCity != null && settlementOrCity.getOwner().equals(player);
    }

    // Check if an intersection has a city
    public boolean hasCity(Player player, int intersection) {
        SettlementOrCity settlementOrCity = intersections.get(intersection);
        return settlementOrCity != null && settlementOrCity.getOwner().equals(player) && settlementOrCity.isCity();
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return "HexTile { resourceType=" + resourceType + ", numberToken=" + numberToken + " }";
    }
}
