package catan.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import catan.board.Board;
import catan.board.HexTile;
import catan.board.Robber;
import catan.players.Player;
import catan.resources.Bank;
import catan.resources.DevelopmentCardType;
import catan.resources.Resource;

public class Game {
    private List<Player> players;
    private Board board;
    private Bank bank;
    private Robber robber;
    private TurnManager turnManager;
    private int currentPlayerIndex;
    private static final int WINNING_POINTS = 10;
    private Scanner scanner;

    public Game(int numberOfPlayers) {
        players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player());
        }
        board = new Board();
        bank = new Bank();
        robber = new Robber(board.getDesertTile());
        turnManager = new TurnManager(players, board, bank, robber);
        currentPlayerIndex = 0;
        scanner = new Scanner(System.in);

        // Perform initial placement using GameSetup
        GameSetup gameSetup = new GameSetup();
        gameSetup.initialPlacement(players, board);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players; // Returns the list of all players
    }

    public Player getPlayerById(int playerId) {
        if (playerId >= 0 && playerId < players.size()) {
            return players.get(playerId); // Returns the player with the corresponding ID
        } else {
            System.out.println("Player ID not found.");
            return null; // Returns null if the ID is invalid
        }
    }

    public boolean checkVictory() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= WINNING_POINTS) {
                System.out.println(player + " wins with " + player.getVictoryPoints() + " points!");
                return true;
            }
        }
        return false;
    }

    public void startTurn() {
        Player currentPlayer = getCurrentPlayer();
        System.out.println(currentPlayer + "'s turn starts.");

        int diceRoll = rollDice();
        System.out.println(currentPlayer + " rolled a " + diceRoll);
        if (diceRoll == 7) {
            triggerRobber(currentPlayer);
        } else {
            distributeResources(diceRoll);
        }
    }

    public void endTurn() {
        System.out.println(getCurrentPlayer() + "'s turn ends.");
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private int rollDice() {
        Random random = new Random();
        return random.nextInt(6) + 1 + random.nextInt(6) + 1;
    }

    private void distributeResources(int diceRoll) {
        for (HexTile tile : board.getHexTiles()) {
            if (tile.getNumberToken() == diceRoll && !tile.isDesert() && tile != robber.getCurrentTile()) {
                for (Player player : players) {
                    if (player.hasSettlementOnTile(tile)) {
                        int resourceAmount = player.hasCityOnTile(tile) ? 2 : 1;
                        Resource resource = tile.getResourceType();
                        player.addResource(resource, resourceAmount);
                        System.out.println(player + " receives " + resourceAmount + " " + resource);
                    }
                }
            }
        }
    }

    private void triggerRobber(Player currentPlayer) {
        System.out.println("Robber activated! " + currentPlayer + " must move the robber.");

        for (Player player : players) {
            if (player.getTotalResources() > 7) {
                player.discardHalfResources();
                System.out.println(player + " discards half of their resources.");
            }
        }

        HexTile newLocation = selectTileForRobber();
        robber.move(newLocation);
        System.out.println("Robber moved to tile with resource: " + newLocation.getResourceType());

        Player targetPlayer = selectPlayerToStealFrom(newLocation, currentPlayer);
        if (targetPlayer != null) {
            Resource stolenResource = targetPlayer.stealRandomResource();
            if (stolenResource != null) {
                currentPlayer.addResource(stolenResource, 1);
                System.out.println(currentPlayer + " stole " + stolenResource + " from " + targetPlayer);
            } else {
                System.out.println(targetPlayer + " has no resources to steal.");
            }
        } else {
            System.out.println("No players to steal from on the new robber location.");
        }
    }

    private Player selectPlayerToStealFrom(HexTile newLocation, Player currentPlayer) {
        for (Player player : players) {
            if (player != currentPlayer && player.hasSettlementOnTile(newLocation)) {
                return player;
            }
        }
        return null;
    }

    private HexTile selectTileForRobber() {
        while (true) {
            System.out.println("Select a tile for the robber (0 - " + (board.getHexTiles().size() - 1) + "): ");
            int tileIndex = scanner.nextInt();
            scanner.nextLine();

            if (tileIndex >= 0 && tileIndex < board.getHexTiles().size()) {
                HexTile selectedTile = board.getHexTiles().get(tileIndex);
                if (!selectedTile.isDesert()) {
                    return selectedTile;
                } else {
                    System.out.println("You cannot place the robber on the desert tile.");
                }
            } else {
                System.out.println("Invalid tile index.");
            }
        }
    }

    public boolean bankTrade(Player player, Resource giveResource, Resource receiveResource, int ratio) {
        if (player.hasResource(giveResource, ratio)) {
            player.removeResource(giveResource, ratio);
            player.addResource(receiveResource, 1);
            return true;
        }
        return false;
    }

    public boolean playerTrade(Player player, Player targetPlayer, Resource giveResource, Resource receiveResource) {
        if (player.hasResource(giveResource, 1) && targetPlayer.hasResource(receiveResource, 1)) {
            player.removeResource(giveResource, 1);
            targetPlayer.addResource(giveResource, 1);
            targetPlayer.removeResource(receiveResource, 1);
            player.addResource(receiveResource, 1);
            return true;
        }
        return false;
    }

    public boolean playDevelopmentCard(Player player, DevelopmentCardType cardType) {
        if (player.hasDevelopmentCard(cardType)) {
            switch (cardType) {
                case KNIGHT:
                    player.incrementKnights();
                    triggerRobber(player);
                    break;
                case ROAD_BUILDING:
                    player.buildRoad();
                    player.buildRoad();
                    break;
                case YEAR_OF_PLENTY:
                    Resource resource1 = selectResource();
                    Resource resource2 = selectResource();
                    player.addResource(resource1, 1);
                    player.addResource(resource2, 1);
                    break;
                case MONOPOLY:
                    Resource monopolyResource = selectResource();
                    int totalCollected = 0;
                    for (Player otherPlayer : players) {
                        if (otherPlayer != player) {
                            int amount = otherPlayer.getResource(monopolyResource);
                            totalCollected += amount;
                            otherPlayer.removeResource(monopolyResource, amount);
                        }
                    }
                    player.addResource(monopolyResource, totalCollected);
                    break;
                default:
                    return false;
            }
            player.useDevelopmentCard(cardType);
            return true;
        }
        return false;
    }

    private Resource selectResource() {
        System.out.println("Enter resource (WOOD, BRICK, SHEEP, WHEAT, ORE): ");
        String resourceName = scanner.nextLine().toUpperCase();
        return Resource.valueOf(resourceName);
    }
}
