package catan.main;

import catan.players.Player;

public class VictoryPointManager {
    public int calculateVictoryPoints(Player player) {
        int points = player.getSettlements() + player.getCities() * 2;
        if (player.hasLongestRoad()) points += 2;
        if (player.hasLargestArmy()) points += 2;
        points += player.getVictoryCardsCount(); // For Victory Point cards
        return points;
    }
}