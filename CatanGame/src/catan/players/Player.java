package catan.players;

import catan.resources.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Player {
    private Map<Resource, Integer> resources;
    private int settlements;
    private int cities;
    private int roads;
    private int victoryPoints;
    private int knights; // Tracks the number of knights played
    private int victoryCardsCount; // Tracks the number of victory point cards
    private boolean hasLongestRoad;
    private boolean hasLargestArmy;
    private Random random; // Used for random resource selection

    public Player() {
        resources = new HashMap<>();
        for (Resource resource : Resource.values()) {
            resources.put(resource, 0);
        }
        settlements = 0;
        cities = 0;
        roads = 0;
        victoryPoints = 0;
        knights = 0;
        victoryCardsCount = 0;
        hasLongestRoad = false;
        hasLargestArmy = false;
        random = new Random();
    }

    // Resource management methods
    public void addResource(Resource resource, int amount) {
        resources.put(resource, resources.get(resource) + amount);
    }

    public void removeResource(Resource resource, int amount) {
        resources.put(resource, Math.max(0, resources.get(resource) - amount));
    }

    public boolean hasResource(Resource resource, int amount) {
        return resources.getOrDefault(resource, 0) >= amount;
    }

    // Methods for building structures
    public boolean canBuildSettlement() {
        return hasResource(Resource.WOOD, 1) && hasResource(Resource.BRICK, 1) &&
               hasResource(Resource.SHEEP, 1) && hasResource(Resource.WHEAT, 1);
    }

    public void buildSettlement() {
        if (canBuildSettlement()) {
            removeResource(Resource.WOOD, 1);
            removeResource(Resource.BRICK, 1);
            removeResource(Resource.SHEEP, 1);
            removeResource(Resource.WHEAT, 1);
            settlements++;
            victoryPoints++;
        }
    }

    public boolean canBuildCity() {
        return hasResource(Resource.WHEAT, 2) && hasResource(Resource.ORE, 3);
    }

    public void buildCity() {
        if (canBuildCity() && settlements > 0) {
            removeResource(Resource.WHEAT, 2);
            removeResource(Resource.ORE, 3);
            settlements--;
            cities++;
            victoryPoints++; // Cities add an additional point on top of the one from settlements
        }
    }

    public boolean canBuildRoad() {
        return hasResource(Resource.WOOD, 1) && hasResource(Resource.BRICK, 1);
    }

    public void buildRoad() {
        if (canBuildRoad()) {
            removeResource(Resource.WOOD, 1);
            removeResource(Resource.BRICK, 1);
            roads++;
        }
    }

    // Victory point management
    public void incrementVictoryPoints() {
        victoryPoints++;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    // Resource counting and discarding methods
    public int getTotalResources() {
        int total = 0;
        for (int count : resources.values()) {
            total += count;
        }
        return total;
    }

    public void discardHalfResources() {
        int totalResources = getTotalResources();
        int resourcesToDiscard = totalResources / 2;

        // Continue discarding resources until the required amount is discarded
        while (resourcesToDiscard > 0) {
            Resource randomResource = getRandomResourceWithAmount();
            if (randomResource != null) {
                int currentAmount = resources.get(randomResource);
                int discardAmount = Math.min(resourcesToDiscard, currentAmount);
                resources.put(randomResource, currentAmount - discardAmount);
                resourcesToDiscard -= discardAmount;
            }
        }
    }

    public Resource stealRandomResource() {
        Resource randomResource = getRandomResourceWithAmount();
        if (randomResource != null) {
            resources.put(randomResource, resources.get(randomResource) - 1);
            return randomResource;
        }
        return null;
    }

    // Helper method for random resource selection with at least 1 unit
    private Resource getRandomResourceWithAmount() {
        Resource[] resourceArray = resources.keySet().toArray(new Resource[0]);
        Resource randomResource = null;
        int attempts = 0;

        while (attempts < 10) {
            randomResource = resourceArray[random.nextInt(resourceArray.length)];
            if (resources.get(randomResource) > 0) {
                return randomResource;
            }
            attempts++;
        }
        return null;
    }

    // Knight and development card tracking
    public void incrementKnights() {
        knights++;
    }

    public int getKnights() {
        return knights;
    }

    public void addVictoryCard() {
        victoryCardsCount++;
        victoryPoints++;
    }

    public int getVictoryCardsCount() {
        return victoryCardsCount;
    }

    // Longest road and largest army tracking
    public boolean hasLongestRoad() {
        return hasLongestRoad;
    }

    public void setLongestRoad(boolean hasLongestRoad) {
        if (this.hasLongestRoad != hasLongestRoad) {
            this.hasLongestRoad = hasLongestRoad;
            victoryPoints += hasLongestRoad ? 2 : -2;
        }
    }

    public boolean hasLargestArmy() {
        return hasLargestArmy;
    }

    public void setLargestArmy(boolean hasLargestArmy) {
        if (this.hasLargestArmy != hasLargestArmy) {
            this.hasLargestArmy = hasLargestArmy;
            victoryPoints += hasLargestArmy ? 2 : -2;
        }
    }

    // Getters for building counts
    public int getSettlements() {
        return settlements;
    }

    public int getCities() {
        return cities;
    }

    public int getRoads() {
        return roads;
    }
}
