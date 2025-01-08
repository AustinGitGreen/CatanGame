package catan.players;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoadManager {
    private List<Road> roads;

    public RoadManager() {
        roads = new ArrayList<>();
    }

    // Adds a road to the player's collection and updates the owner
    public void addRoad(Player player, Road road) {
        if (player.canBuildRoad()) {
            roads.add(road);
            road.setOwner(player);
            player.buildRoad(); // Deducts resources and increments road count
        } else {
            System.out.println("Player does not have enough resources to build a road.");
        }
    }

    // Method to calculate the longest road for a player
    public int calculateLongestRoad(Player player) {
        int longestRoad = 0;
        Set<Road> visitedRoads = new HashSet<>(); // Global visited set for the player

        for (Road road : roads) {
            if (road.getOwner() == player && !visitedRoads.contains(road)) {
                // Perform DFS to calculate the length of the current road network
                int roadLength = dfsRoadLength(road, player, visitedRoads, null);
                longestRoad = Math.max(longestRoad, roadLength);
            }
        }

        // Update the player's longest road status if it surpasses the minimum length
        if (longestRoad >= 5) { // Typically, the minimum length for "longest road" in Catan is 5
            player.setLongestRoad(true);
        } else {
            player.setLongestRoad(false);
        }

        return longestRoad;
    }

    // Helper method for depth-first search to calculate road length
    private int dfsRoadLength(Road road, Player player, Set<Road> visited, Road previousRoad) {
        visited.add(road); // Mark the road as visited

        int maxLength = 0;

        // Traverse all connected roads
        for (Road connectedRoad : road.getConnectedRoads()) {
            if (connectedRoad != previousRoad && connectedRoad.getOwner() == player && !visited.contains(connectedRoad)) {
                maxLength = Math.max(maxLength, dfsRoadLength(connectedRoad, player, visited, road));
            }
        }

        return 1 + maxLength; // Include the current road in the total length
    }


}
