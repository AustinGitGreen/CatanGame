package catan.board;

import resources.Resource;

/**
 * Represents a port on the game board where players can trade resources.
 */
public class Port {
    private Resource resource;
    private int tradeRatio;

    /**
     * Constructs a Port with a resource type and trade ratio.
     * @param resource The resource type for the port, or null for a generic port.
     * @param tradeRatio The trade ratio (e.g., 3:1 or 2:1).
     */
    public Port(Resource resource, int tradeRatio) {
        this.resource = resource;
        this.tradeRatio = tradeRatio;
    }

    /**
     * Gets the resource type for the port.
     * @return The resource type, or null for a generic port.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Gets the trade ratio for the port.
     * @return The trade ratio.
     */
    public int getTradeRatio() {
        return tradeRatio;
    }

    /**
     * Determines if the port is a generic port (3:1 trade ratio for any resource).
     * @return True if the port is generic, false otherwise.
     */
    public boolean isGenericPort() {
        return resource == null;
    }
}
