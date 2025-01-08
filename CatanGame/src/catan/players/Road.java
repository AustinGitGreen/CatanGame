package catan.players;

import java.util.HashSet;
import java.util.Set;

public class Road {
    private Player owner;
    private Set<Road> connectedRoads; // Dynamic list of connected roads

    public Road(Player owner) {
        this.owner = owner;
        this.connectedRoads = new HashSet<>();
    }

    // Get the player who owns the road
    public Player getOwner() {
        return owner;
    }

    // Set or change the owner of the road
    public void setOwner(Player player) {
        this.owner = player;
    }

    // Add a connected road (enforces bidirectional connectivity)
    public void connectRoad(Road road) {
        if (road != null && !connectedRoads.contains(road)) {
            connectedRoads.add(road);
            road.connectRoad(this); // Ensure the connection is bidirectional
        }
    }

    // Get all connected roads
    public Set<Road> getConnectedRoads() {
        return connectedRoads;
    }

    // Check if this road is connected to another specific road
    public boolean isConnectedTo(Road road) {
        return connectedRoads.contains(road);
    }
}
