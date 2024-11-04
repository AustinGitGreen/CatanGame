package catan.players;

public class Road {
    private Player owner;
    private Road connectedRoad1; // First connected road for calculating longest road
    private Road connectedRoad2; // Second connected road for calculating longest road

    public Road(Player owner) {
        this.owner = owner;
    }

    // Get the player who owns the road
    public Player getOwner() {
        return owner;
    }

    // Set or change the owner of the road (e.g., during road building)
    public void setOwner(Player player) {
        this.owner = player;
    }

    // Set the first connected road (used for longest road calculations)
    public void setConnectedRoad1(Road road) {
        this.connectedRoad1 = road;
    }

    // Set the second connected road (used for longest road calculations)
    public void setConnectedRoad2(Road road) {
        this.connectedRoad2 = road;
    }

    // Get the first connected road
    public Road getConnectedRoad1() {
        return connectedRoad1;
    }

    // Get the second connected road
    public Road getConnectedRoad2() {
        return connectedRoad2;
    }

    // Check if this road is connected to another specific road
    public boolean isConnectedTo(Road road) {
        return connectedRoad1 == road || connectedRoad2 == road;
    }
}
