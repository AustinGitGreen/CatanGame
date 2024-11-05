package catan.players;

import catan.board.HexTile;
import catan.board.Robber;

public class ArmyManager {
    private Player largestArmyHolder;
    private int largestArmySize = 2; // Minimum number of knights to hold the largest army

    // Method to play a knight and move the robber
    public void playKnight(Player player, Robber robber, HexTile newLocation) {
        player.incrementKnights(); // Player plays a knight card

        // Check if the player now has the largest army
        if (player.getKnights() > largestArmySize) {
            setLargestArmyHolder(player);
            largestArmySize = player.getKnights();
        }

        // Move the robber to the specified new location
        robber.move(newLocation); // Move robber to a new tile
    }

    // Getter for the player who holds the largest army
    public Player getLargestArmyHolder() {
        return largestArmyHolder;
    }

    // Setter for updating the largest army holder
    public void setLargestArmyHolder(Player largestArmyHolder) {
        this.largestArmyHolder = largestArmyHolder;
    }
}
