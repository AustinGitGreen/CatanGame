package catan.players;

import catan.resources.Resource;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a player's inventory of resources in the game.
 */
public class Inventory {
    private Map<Resource, Integer> resourceCounts;

    public Inventory() {
        resourceCounts = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            resourceCounts.put(resource, 0);
        }
    }

    public int getResourceCount(Resource resource) {
        return resourceCounts.getOrDefault(resource, 0);
    }

    public void addResource(Resource resource, int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount to add cannot be negative");
        resourceCounts.put(resource, resourceCounts.get(resource) + amount);
    }

    public void removeResource(Resource resource, int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount to remove cannot be negative");
        int currentCount = resourceCounts.get(resource);
        if (currentCount < amount) throw new IllegalArgumentException("Not enough resources to remove");
        resourceCounts.put(resource, currentCount - amount);
    }

    public boolean hasEnoughResource(Resource resource, int amount) {
        return resourceCounts.getOrDefault(resource, 0) >= amount;
    }

    public boolean hasEnoughResources(Map<Resource, Integer> requiredResources) {
        for (Map.Entry<Resource, Integer> entry : requiredResources.entrySet()) {
            if (!hasEnoughResource(entry.getKey(), entry.getValue())) return false;
        }
        return true;
    }

    public void addResources(Map<Resource, Integer> resources) {
        for (Map.Entry<Resource, Integer> e : resources.entrySet()) {
            addResource(e.getKey(), e.getValue());
        }
    }

    public void removeResources(Map<Resource, Integer> resources) {
        if (!hasEnoughResources(resources)) {
            throw new IllegalArgumentException("Not enough resources to remove");
        }
        for (Map.Entry<Resource, Integer> e : resources.entrySet()) {
            removeResource(e.getKey(), e.getValue());
        }
    }

    /** Total number of resource cards in hand (excludes DESERT). */
    public int getTotalResourceCards() {
        int total = 0;
        for (Map.Entry<Resource, Integer> e : resourceCounts.entrySet()) {
            Resource r = e.getKey();
            if (r == Resource.DESERT) continue;
            total += e.getValue();
        }
        return total;
    }

    /** True if the player has at least one resource card (excludes DESERT). */
    public boolean hasAnyResources() {
        return getTotalResourceCards() > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;
            if (!first) sb.append(", ");
            sb.append(r).append(": ").append(getResourceCount(r));
            first = false;
        }
        return sb.toString();
    }
}
