package catan.main;

import java.util.List;
import java.util.Random;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Robber;
import catan.players.Player;
import catan.resources.Bank;
import catan.resources.Resource;

public class TurnManager {
    private List<Player> players;
    private int currentPlayerIndex;
    private Board board;
    private Bank bank;
    private Robber robber;
    private Random random;

    public TurnManager(List<Player> players, Board board, Bank bank, Robber robber) {
        this.players = players;
        this.board = board;
        this.bank = bank;
        this.robber = robber;
        this.currentPlayerIndex = 0;
        this.random = new Random();
    }

    // Starts a player's turn
    public void startTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);

        // Roll dice
        int diceRoll = rollDice();
        System.out.println("Player " + currentPlayerIndex + " rolled a " + diceRoll);

        // Check if roll is 7, triggering the robber
        if (diceRoll == 7) {
            triggerRobber(currentPlayer);
        } else {
            distributeResources(diceRoll);
        }

        // Player actions (build, trade, play development cards, etc.)
        // Placeholder: Allow player to take actions based on your game loop design
        // Example: currentPlayer.takeActions();
    }

    // Rolls two six-sided dice and returns the result
    private int rollDice() {
        return random.nextInt(6) + 1 + random.nextInt(6) + 1;
    }

    // Distributes resources to players based on the dice roll
    private void distributeResources(int diceRoll) {
        for (HexTile tile : board.getHexTiles()) {
            if (tile.getNumberToken() == diceRoll && !tile.isDesert() && tile != robber.getCurrentTile()) {
                for (Player player : players) {
                    if (playerHasSettlementOnTile(player, tile)) { // Assuming such a method exists
                        player.addResource(Resource.valueOf(tile.getResourceType().toUpperCase()), 1);
                        System.out.println("Player " + players.indexOf(player) + " receives 1 " + tile.getResourceType());
                    }
                }
            }
        }
    }

    // Handles triggering the robber when a 7 is rolled
    private void triggerRobber(Player currentPlayer) {
        System.out.println("Robber is triggered! Player " + currentPlayerIndex + " moves the robber.");

        // Implement logic for players with too many resources discarding half
        for (Player player : players) {
            int totalResources = player.getTotalResources(); // Assuming such a method exists
            if (totalResources > 7) {
                player.discardHalfResources();
            }
        }

        // Move the robber to a new tile
        HexTile newLocation = selectTileForRobber(); // Implement method for player selection
        robber.move(newLocation);

        // Optionally allow the current player to steal a resource from a player with a settlement on the new tile
        Player targetPlayer = selectPlayerToStealFrom(newLocation, currentPlayer); // Implement as needed
        if (targetPlayer != null) {
            Resource stolenResource = targetPlayer.stealRandomResource(); // Assuming this method exists
            currentPlayer.addResource(stolenResource, 1);
            System.out.println("Player " + currentPlayerIndex + " stole " + stolenResource + " from Player " + players.indexOf(targetPlayer));
        }
    }

    // Ends the player's turn and moves to the next player
    public void endTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // Selects a tile for the robber to move to (placeholder for actual implementation)
    private HexTile selectTileForRobber() {
        // Logic for selecting tile, typically chosen by the current player
        // Placeholder: Just return the first non-desert tile
        for (HexTile tile : board.getHexTiles()) {
            if (!tile.isDesert()) {
                return tile;
            }
        }
        return null;
    }

    // Placeholder method to select a player to steal from on a given tile
    private Player selectPlayerToStealFrom(HexTile tile, Player currentPlayer) {
        // Logic to select a player who has a settlement on the tile
        // Placeholder: Just return the first player found on the tile, other than current player
        for (Player player : players) {
            if (playerHasSettlementOnTile(player, tile) && player != currentPlayer) {
                return player;
            }
        }
        return null;
    }

    // Placeholder method to check if a player has a settlement on a tile
    private boolean playerHasSettlementOnTile(Player player, HexTile tile) {
        // Implement based on how settlements are tracked on tiles
        return false; // Placeholder
    }
}
