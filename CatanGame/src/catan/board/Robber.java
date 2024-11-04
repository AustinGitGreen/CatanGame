package catan.board;

public class Robber {
    private HexTile currentTile;

    public Robber(HexTile startingTile) {
        this.currentTile = startingTile;
    }

    public void move(HexTile newTile) {
        this.currentTile = newTile;
    }

    public HexTile getCurrentTile() { return currentTile; }
}