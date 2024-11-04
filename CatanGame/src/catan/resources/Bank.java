package catan.resources;

import catan.players.Player;
import java.util.EnumMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {
    private Map<Resource, Integer> resourcePool;
    private List<DevelopmentCard> developmentDeck;

    public Bank() {
        resourcePool = new EnumMap<>(Resource.class);
        initializeResources();
        initializeDevelopmentDeck();
    }

    // Initializes resource quantities for the bank
    private void initializeResources() {
        for (Resource resource : Resource.values()) {
            resourcePool.put(resource, 19); // Standard initial quantity for Catan resources
        }
    }

    // Initializes the development card deck with the correct card types and quantities
    private void initializeDevelopmentDeck() {
        developmentDeck = new ArrayList<>();
        // Add specific quantities for each type of development card
        for (int i = 0; i < 14; i++) developmentDeck.add(new DevelopmentCard(DevelopmentCardType.KNIGHT));
        for (int i = 0; i < 5; i++) developmentDeck.add(new DevelopmentCard(DevelopmentCardType.VICTORY_POINT));
        for (int i = 0; i < 2; i++) developmentDeck.add(new DevelopmentCard(DevelopmentCardType.ROAD_BUILDING));
        for (int i = 0; i < 2; i++) developmentDeck.add(new DevelopmentCard(DevelopmentCardType.YEAR_OF_PLENTY));
        for (int i = 0; i < 2; i++) developmentDeck.add(new DevelopmentCard(DevelopmentCardType.MONOPOLY));

        // Shuffle the deck for random draws
        Collections.shuffle(developmentDeck);
    }

    // Method to draw a development card from the deck
    public DevelopmentCard drawDevelopmentCard() {
        if (!developmentDeck.isEmpty()) {
            return developmentDeck.remove(0); // Draw from the top of the deck
        }
        return null; // Deck is empty
    }

    // Method for player-to-bank trade (typically 4:1 for any resource or 3:1/2:1 if using ports)
    public boolean tradeWithPlayer(Player player, Resource give, Resource receive, int ratio) {
        if (player.hasResource(give, ratio) && resourcePool.get(receive) > 0) {
            player.removeResource(give, ratio);
            player.addResource(receive, 1);
            resourcePool.put(give, resourcePool.get(give) + ratio);
            resourcePool.put(receive, resourcePool.get(receive) - 1);
            return true;
        }
        return false;
    }

    // Method to check if the bank has a specific amount of a resource
    public boolean hasResource(Resource resource, int amount) {
        return resourcePool.getOrDefault(resource, 0) >= amount;
    }

    // Method to add resources to the bank, e.g., after a trade or robber action
    public void addResource(Resource resource, int amount) {
        resourcePool.put(resource, resourcePool.get(resource) + amount);
    }

    // Method to remove resources from the bank (typically for resource distribution)
    public void removeResource(Resource resource, int amount) {
        if (hasResource(resource, amount)) {
            resourcePool.put(resource, resourcePool.get(resource) - amount);
        }
    }
}
