package catan.players;

import catan.board.HexTile;
import catan.resources.DevelopmentCardType;
import catan.resources.Resource;

import java.util.*;
import java.util.stream.Collectors;

public class Player {
    private static int playerCount = 0; // Static variable to assign unique IDs to players
    private final String id; // Unique identifier for each player

    private Map<Resource, Integer> resources; // Tracks the resources owned by the player
    private Map<HexTile, Integer> settlements; // Tracks tiles with settlements and their intersection indexes
    private Map<HexTile, Integer> cities;      // Tracks tiles with cities and their intersection indexes
    private int roads;                         // Number of roads owned by the player
    private int victoryPoints;                 // Tracks the player's victory points
    private int knights;                       // Tracks the number of knights played
    private int victoryCardsCount;             // Tracks the number of victory point cards
    private boolean hasLongestRoad;            // Tracks whether the player has the longest road
    private boolean hasLargestArmy;            // Tracks whether the player has the largest army
    private Random random;                     // Used for random resource selection
    private Map<DevelopmentCardType, Integer> developmentCards; // Tracks development cards

    public Player() {
        // Increment player count and assign a unique ID
        playerCount++;
        this.id = "Player " + playerCount;

        resources = new HashMap<>();
        for (Resource resource : Resource.values()) {
            resources.put(resource, 0);
        }
        settlements = new HashMap<>();
        cities = new HashMap<>();
        roads = 0;
        victoryPoints = 0;
        knights = 0;
        victoryCardsCount = 0;
        hasLongestRoad = false;
        hasLargestArmy = false;
        random = new Random();
        developmentCards = new HashMap<>();
        for (DevelopmentCardType cardType : DevelopmentCardType.values()) {
            developmentCards.put(cardType, 0);
        }
    }

    // Basic Info and Overrides
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + " [VP: " + victoryPoints + ", Roads: " + roads + ", Settlements: " + settlements.size()
                + ", Cities: " + cities.size() + "]";
    }

    // Resource Management
    public void addResource(Resource resource, int amount) {
        resources.put(resource, resources.get(resource) + amount);
    }

    public void removeResource(Resource resource, int amount) {
        resources.put(resource, Math.max(0, resources.get(resource) - amount));
    }

    public boolean hasResource(Resource resource, int amount) {
        return resources.getOrDefault(resource, 0) >= amount;
    }

    public int getResource(Resource resource) {
        return resources.getOrDefault(resource, 0);
    }

    public int getTotalResources() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void discardHalfResources() {
        int totalResources = getTotalResources();
        int resourcesToDiscard = totalResources / 2;

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

    private Resource getRandomResourceWithAmount() {
        List<Resource> availableResources = resources.keySet().stream()
                .filter(resource -> resources.get(resource) > 0)
                .collect(Collectors.toList());

        if (availableResources.isEmpty()) {
            return null;
        }

        return availableResources.get(random.nextInt(availableResources.size()));
    }

    public Resource stealRandomResource() {
        Resource randomResource = getRandomResourceWithAmount();
        if (randomResource != null) {
            resources.put(randomResource, resources.get(randomResource) - 1);
            return randomResource;
        }
        return null;
    }

    public String getResourceSummary() {
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<Resource, Integer> entry : resources.entrySet()) {
            summary.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        if (summary.length() > 2) {
            summary.setLength(summary.length() - 2); // Remove trailing comma and space
        }
        return summary.toString();
    }

    // Settlement and City Management
    public boolean canBuildSettlement() {
        return hasResource(Resource.WOOD, 1) &&
               hasResource(Resource.BRICK, 1) &&
               hasResource(Resource.SHEEP, 1) &&
               hasResource(Resource.WHEAT, 1);
    }

    public void buildSettlement(HexTile tile, int intersection) {
        if (canBuildSettlement() && tile.placeSettlement(this, intersection)) {
            // Deduct resources for the settlement
            removeResource(Resource.WOOD, 1);
            removeResource(Resource.BRICK, 1);
            removeResource(Resource.SHEEP, 1);
            removeResource(Resource.WHEAT, 1);

            // Add the settlement to the player's list
            settlements.put(tile, intersection); // Track the tile and intersection
            victoryPoints++;
            System.out.println("Settlement successfully placed. Total settlements: " + settlements.size());
        } else {
            System.out.println("Invalid settlement placement. Settlement not added.");
        }
    }

    public boolean canBuildCity() {
        return hasResource(Resource.WHEAT, 2) &&
               hasResource(Resource.ORE, 3) &&
               !settlements.isEmpty();
    }

    public void upgradeSettlementToCity(HexTile tile, int intersection) {
        if (canBuildCity() && settlements.containsKey(tile) && settlements.get(tile) == intersection) {
            removeResource(Resource.WHEAT, 2);
            removeResource(Resource.ORE, 3);
            settlements.remove(tile);
            cities.put(tile, intersection);
            tile.upgradeSettlementToCity(this, intersection);
            victoryPoints++;
        } else {
            System.out.println("Cannot upgrade. No valid settlement at this location.");
        }
    }

    public boolean hasSettlementOnTile(HexTile tile) {
        return settlements.containsKey(tile);
    }

    public boolean hasCityOnTile(HexTile tile) {
        return cities.containsKey(tile);
    }

    public int getSettlements() {
        return settlements.size();
    }

    public int getCities() {
        return cities.size();
    }

    // Road Management
    public boolean canBuildRoad() {
        return hasResource(Resource.WOOD, 1) && hasResource(Resource.BRICK, 1);
    }

    public void buildRoad() {
        if (canBuildRoad()) {
            removeResource(Resource.WOOD, 1);
            removeResource(Resource.BRICK, 1);
            roads++;
        } else {
            System.out.println("Insufficient resources to build a road.");
        }
    }
    
 // Road Management
    public void placeRoadWithoutResources() {
        roads++;
        System.out.println("Road placed without consuming resources.");
    }

    public int getRoads() {
        return roads;
    }

    // Victory Points Management
    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void incrementVictoryPoints() {
        victoryPoints++;
    }

    // Longest Road and Largest Army
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

    // Development Cards
    public boolean hasDevelopmentCard(DevelopmentCardType cardType) {
        return developmentCards.getOrDefault(cardType, 0) > 0;
    }

    public void useDevelopmentCard(DevelopmentCardType cardType) {
        int count = developmentCards.getOrDefault(cardType, 0);
        if (count > 0) {
            developmentCards.put(cardType, count - 1);
        } else {
            System.out.println("No " + cardType + " card available to use.");
        }
    }

    public void addDevelopmentCard(DevelopmentCardType cardType) {
        developmentCards.put(cardType, developmentCards.getOrDefault(cardType, 0) + 1);
    }

    public int getVictoryCardsCount() {
        return victoryCardsCount;
    }

    public void addVictoryCard() {
        victoryCardsCount++;
    }

    public void revealVictoryCards() {
        victoryPoints += victoryCardsCount;
        victoryCardsCount = 0;
    }

    public int getKnights() {
        return knights;
    }

    public void incrementKnights() {
        knights++;
    }
}
