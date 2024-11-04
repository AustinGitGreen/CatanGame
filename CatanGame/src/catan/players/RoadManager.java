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
        roads.add(road);
        road.setOwner(player);
    }

    // Method to calculate the longest road for a player
    public int calculateLongestRoad(Player player) {
        int longestRoad = 0;
        
        // Track roads visited in each DFS to avoid cycles
        Set<Road> visitedRoads = new HashSet<>();
        
        // Check each road owned by the player as a starting point
        for (Road road : roads) {
            if (road.getOwner() == player && !visitedRoads.contains(road)) {
                // Perform DFS to find the longest path from this road
                int roadLength = dfsRoadLength(road, player, visitedRoads, null);
                longestRoad = Math.max(longestRoad, roadLength);
            }
        }

        // Update the player's longest road status if it surpasses the previous longest
        if (longestRoad >= 5) { // Typically, the minimum length for "longest road" in Catan is 5
            player.setLongestRoad(true);
        } else {
            player.setLongestRoad(false);
        }

        return longestRoad;
    }

    // Helper method for depth-first search to calculate road length
    private int dfsRoadLength(Road road, Player player, Set<Road> visited, Road previousRoad) {
        // Mark the current road as visited
        visited.add(road);

        int length = 1; // Start with this road

        // Traverse connected roads if they belong to the same player and are not visited
        if (road.getConnectedRoad1() != null && road.getConnectedRoad1() != previousRoad &&
            road.getConnectedRoad1().getOwner() == player) {
            length = Math.max(length, 1 + dfsRoadLength(road.getConnectedRoad1(), player, visited, road));
        }
        
        if (road.getConnectedRoad2() != null && road.getConnectedRoad2() != previousRoad &&
            road.getConnectedRoad2().getOwner() == player) {
            length = Math.max(length, 1 + dfsRoadLength(road.getConnectedRoad2(), player, visited, road));
        }

        return length;
    }
}
