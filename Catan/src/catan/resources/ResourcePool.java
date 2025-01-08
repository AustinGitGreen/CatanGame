package catan.resources;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a pool of resources that can be used in the game.
 */
public class ResourcePool {
    private Map<Resource, Integer> resourceCounts;

    /**
     * Constructs a ResourcePool with all resources initialized to 0.
     */
    public ResourcePool() {
        resourceCounts = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            resourceCounts.put(resource, 0);
        }
    }

    /**
     * Gets the count of a specific resource in the pool.
     * @param resource The resource to get the count for.
     * @return The count of the specified resource.
     */
    public int getResourceCount(Resource resource) {
        return resourceCounts.getOrDefault(resource, 0);
    }

    /**
     * Adds a specified amount of a resource to the pool.
     * @param resource The resource to add.
     * @param amount The amount to add.
     */
    public void addResource(Resource resource, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to add cannot be negative");
        }
        resourceCounts.put(resource, resourceCounts.get(resource) + amount);
    }

    /**
     * Removes a specified amount of a resource from the pool.
     * @param resource The resource to remove.
     * @param amount The amount to remove.
     */
    public void removeResource(Resource resource, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to remove cannot be negative");
        }
        int currentCount = resourceCounts.get(resource);
        if (currentCount < amount) {
            throw new IllegalArgumentException("Not enough resources to remove");
        }
        resourceCounts.put(resource, currentCount - amount);
    }

    /**
     * Checks if the pool has at least a specified amount of a resource.
     * @param resource The resource to check.
     * @param amount The amount to check for.
     * @return True if the pool has enough of the resource, false otherwise.
     */
    public boolean hasEnoughResource(Resource resource, int amount) {
        return resourceCounts.getOrDefault(resource, 0) >= amount;
    }
}
