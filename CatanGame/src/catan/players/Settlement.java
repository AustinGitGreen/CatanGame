package catan.players;

import catan.board.HexTile; // Assuming HexTile represents locations on the board

public class Settlement {
    private Player owner;
    private boolean isCity; // Indicates if the settlement has been upgraded to a city
    private HexTile location; // Location on the board where the settlement is placed

    public Settlement(Player owner, HexTile location) {
        this.owner = owner;
        this.location = location;
        this.isCity = false; // Settlements start as settlements, not cities
    }

    // Get the player who owns the settlement
    public Player getOwner() {
        return owner;
    }

    // Get the location of the settlement on the board
    public HexTile getLocation() {
        return location;
    }

    // Check if the settlement has been upgraded to a city
    public boolean isCity() {
        return isCity;
    }

    // Upgrade the settlement to a city
    public void upgradeToCity() {
        if (!isCity) {
            isCity = true;
            owner.incrementVictoryPoints(); // Add an extra point for upgrading to a city
        }
    }
}
