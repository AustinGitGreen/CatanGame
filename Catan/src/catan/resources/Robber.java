package catan.resources;

import catan.board.Hex;

/**
 * Represents the robber in the game, which blocks resource production on a hex.
 */
public class Robber {
    private Hex currentHex;

    /**
     * Constructs a Robber starting on a specific hex.
     * @param startingHex The hex where the robber starts.
     */
    public Robber(Hex startingHex) {
        this.currentHex = startingHex;
    }

    /**
     * Gets the current hex where the robber is located.
     * @return The current hex of the robber.
     */
    public Hex getCurrentHex() {
        return currentHex;
    }

    /**
     * Moves the robber to a new hex.
     * @param newHex The hex where the robber will be moved.
     */
    public void moveTo(Hex newHex) {
        this.currentHex = newHex;
    }

    /**
     * Checks if the robber is blocking resource production on a given hex.
     * @param hex The hex to check.
     * @return True if the robber is blocking resource production on the given hex, false otherwise.
     */
    public boolean blocksResourceProduction(Hex hex) {
        return currentHex.equals(hex);
    }
}