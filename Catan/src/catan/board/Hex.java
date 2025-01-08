package catan.board;

import resources.Resource;

/**
 * Represents a hex tile on the game board.
 */
public class Hex {
    private Resource resource;
    private int numberToken;

    /**
     * Constructs a Hex with a resource type and a number token.
     * @param resource The resource type of the hex.
     * @param numberToken The number token associated with the hex.
     */
    public Hex(Resource resource, int numberToken) {
        this.resource = resource;
        this.numberToken = numberToken;
    }

    /**
     * Gets the resource type of the hex.
     * @return The resource type.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets the resource type of the hex.
     * @param resource The new resource type.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Gets the number token of the hex.
     * @return The number token.
     */
    public int getNumberToken() {
        return numberToken;
    }

    /**
     * Sets the number token of the hex.
     * @param numberToken The new number token.
     */
    public void setNumberToken(int numberToken) {
        this.numberToken = numberToken;
    }
}
