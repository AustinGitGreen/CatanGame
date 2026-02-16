package catan.resources;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a pool of resources that can be used in the game.
 */
public class ResourcePool {
    private Map<Resource, Integer> resourceCounts;

    /**
     * Bank starts with classic Catan counts: 19 of each resource card.
     */
    public ResourcePool() {
        resourceCounts = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            if (resource.name().equalsIgnoreCase("DESERT")) {
                resourceCounts.put(resource, 0);
            } else {
                resourceCounts.put(resource, 19);
            }
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

    public void addResources(Map<Resource, Integer> resources) {
        for (Map.Entry<Resource, Integer> e : resources.entrySet()) {
            addResource(e.getKey(), e.getValue());
        }
    }

    public void removeResources(Map<Resource, Integer> resources) {
        for (Map.Entry<Resource, Integer> e : resources.entrySet()) {
            removeResource(e.getKey(), e.getValue());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Resource r : Resource.values()) {
            if (r.name().equalsIgnoreCase("DESERT")) continue;
            if (!first) sb.append(", ");
            sb.append(r).append(": ").append(getResourceCount(r));
            first = false;
        }
        return sb.toString();
    }
}
