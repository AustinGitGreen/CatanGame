package catan.board;

import catan.players.Player;
import catan.resources.Resource;

public class Port {
    private int tradeRatio;
    private Resource resourceType; // Null if any resource is allowed

    public Port(int tradeRatio, Resource resourceType) {
        this.tradeRatio = tradeRatio;
        this.resourceType = resourceType;
    }

    public boolean trade(Player player, Resource give, Resource receive) {
        if ((resourceType == null || resourceType == give) && player.hasResource(give, tradeRatio)) {
            player.removeResource(give, tradeRatio);
            player.addResource(receive, 1);
            return true;
        }
        return false;
    }
}