package catan.board;

import catan.players.Player;
import catan.resources.Resource;

public class Port {
    private final int tradeRatio;
    private final Resource resourceType; // Null if any resource is allowed (3:1 ports)

    public Port(int tradeRatio, Resource resourceType) {
        this.tradeRatio = tradeRatio;
        this.resourceType = resourceType;
    }

    // Facilitates a trade for the player at this port
    public boolean trade(Player player, Resource give, Resource receive) {
        if (canTrade(give) && player.hasResource(give, tradeRatio)) {
            player.removeResource(give, tradeRatio);
            player.addResource(receive, 1);
            System.out.println("Trade successful: Traded " + tradeRatio + " " + give + " for 1 " + receive + " at port.");
            return true;
        }
        System.out.println("Trade failed: Insufficient resources or incorrect resource type.");
        return false;
    }

    // Checks if the port allows trading the specified resource
    private boolean canTrade(Resource give) {
        return resourceType == null || resourceType == give;
    }

    public int getTradeRatio() {
        return tradeRatio;
    }

    public Resource getResourceType() {
        return resourceType;
    }

    @Override
    public String toString() {
        if (resourceType == null) {
            return tradeRatio + ":1 Port (Any resource)";
        } else {
            return tradeRatio + ":1 Port (" + resourceType + ")";
        }
    }
}
