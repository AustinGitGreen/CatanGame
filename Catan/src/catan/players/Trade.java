package catan.players;

import catan.resources.Resource;
import catan.resources.ResourcePool;
import java.util.Map;

/**
 * Represents trade actions in the game, including player-to-player and player-to-bank trades.
 */
public class Trade {
    private ResourcePool resourcePool;

    /**
     * Constructs a Trade object with a resource pool for bank trades.
     * @param resourcePool The resource pool to use for bank trades.
     */
    public Trade(ResourcePool resourcePool) {
        if (resourcePool == null) {
            throw new IllegalArgumentException("ResourcePool cannot be null");
        }
        this.resourcePool = resourcePool;
    }

    /**
     * Executes a player-to-player trade.
     * @param player1Inventory The inventory of the first player.
     * @param player1Offer The resources offered by the first player.
     * @param player2Inventory The inventory of the second player.
     * @param player2Offer The resources offered by the second player.
     * @return True if the trade is successful, false otherwise.
     */
    public boolean playerToPlayerTrade(Inventory player1Inventory, Map<Resource, Integer> player1Offer,
                                       Inventory player2Inventory, Map<Resource, Integer> player2Offer) {
        if (!hasSufficientResources(player1Inventory, player1Offer) ||
            !hasSufficientResources(player2Inventory, player2Offer)) {
            return false;
        }

        // Execute the trade
        for (Map.Entry<Resource, Integer> entry : player1Offer.entrySet()) {
            player1Inventory.removeResource(entry.getKey(), entry.getValue());
            player2Inventory.addResource(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Resource, Integer> entry : player2Offer.entrySet()) {
            player2Inventory.removeResource(entry.getKey(), entry.getValue());
            player1Inventory.addResource(entry.getKey(), entry.getValue());
        }

        return true;
    }

    /**
     * Executes a player-to-bank trade.
     * @param playerInventory The inventory of the player.
     * @param resourcePool The resource pool of the bank.
     * @param offerResource The resource the player is offering.
     * @param offerAmount The amount of the offered resource.
     * @param requestResource The resource the player is requesting.
     * @param requestAmount The amount of the requested resource.
     * @return True if the trade is successful, false otherwise.
     */
    public boolean playerToBankTrade(Inventory playerInventory, ResourcePool resourcePool,
                                      Resource offerResource, int offerAmount,
                                      Resource requestResource, int requestAmount) {
        if (!playerInventory.hasEnoughResource(offerResource, offerAmount) ||
            !resourcePool.hasEnoughResource(requestResource, requestAmount)) {
            return false;
        }

        // Execute the trade
        playerInventory.removeResource(offerResource, offerAmount);
        resourcePool.addResource(offerResource, offerAmount);

        resourcePool.removeResource(requestResource, requestAmount);
        playerInventory.addResource(requestResource, requestAmount);

        return true;
    }

    /**
     * Checks if a player's inventory has sufficient resources for a trade.
     * @param inventory The inventory to check.
     * @param offer The resources being offered.
     * @return True if the inventory has enough resources, false otherwise.
     */
    private boolean hasSufficientResources(Inventory inventory, Map<Resource, Integer> offer) {
        for (Map.Entry<Resource, Integer> entry : offer.entrySet()) {
            if (!inventory.hasEnoughResource(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}