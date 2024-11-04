package catan.resources;

import catan.players.Player;

public class Trade {
    public static boolean playerToPlayerTrade(Player player1, Resource give1, int amount1, Player player2, Resource give2, int amount2) {
        if (player1.hasResource(give1, amount1) && player2.hasResource(give2, amount2)) {
            player1.removeResource(give1, amount1);
            player2.addResource(give1, amount1);
            player2.removeResource(give2, amount2);
            player1.addResource(give2, amount2);
            return true;
        }
        return false;
    }

    public static boolean playerToBankTrade(Player player, Resource give, Resource receive) {
        if (player.hasResource(give, 4)) { // 4:1 bank trade ratio
            player.removeResource(give, 4);
            player.addResource(receive, 1);
            return true;
        }
        return false;
    }
}