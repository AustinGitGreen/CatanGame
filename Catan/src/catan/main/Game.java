package catan.main;

import catan.board.Board;
import catan.players.Player;
import catan.main.TurnManager;
import catan.resources.ResourcePool;
import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Road;
import catan.components.Settlement;
import catan.utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the main game logic for Catan.
 */
public class Game {
    private Board board;
    private List<Player> players;
    private ResourcePool resourcePool;
    private TurnManager turnManager;

    /**
     * Initializes the game with the specified number of players.
     * @param numberOfPlayers The number of players in the game.
     */
    public void initializeGame(int numberOfPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");
        }

        // Initialize the board, resource pool, and players
        board = new Board();
        board.initializeBoard();
        resourcePool = new ResourcePool();

        players = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            players.add(new Player("Player " + i));
        }

        turnManager = new TurnManager(players); // Use TurnManager to handle player turns

        System.out.println("Game initialized with " + numberOfPlayers + " players.");
    }

    /**
     * Gets the list of players in the game.
     * @return The list of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the game board.
     * @return The game board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the resource pool.
     * @return The resource pool.
     */
    public ResourcePool getResourcePool() {
        return resourcePool;
    }

    /**
     * Gets the current player.
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return turnManager.getCurrentPlayer();
    }

    /**
     * Ends the current player's turn and moves to the next player.
     */
    public void endTurn() {
        turnManager.nextTurn();
        System.out.println("It is now " + getCurrentPlayer().getName() + "'s turn.");
    }

    /**
     * Checks if a player has won the game.
     * @return True if a player has won, false otherwise.
     */
    public boolean checkVictory() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= 10) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the winning player, if there is one.
     * @return The winning player, or null if no player has won yet.
     */
    public Player getWinningPlayer() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= 10) {
                return player;
            }
        }
        return null;
    }
    
    /**
     * Finds an existing settlement at an intersection, if any.
     */
    public Settlement getSettlementAt(Intersection intersection) {
        for (Player p : players) {
            for (Settlement s : p.getSettlements()) {
                if (s.getLocation().equals(intersection)) return s;
            }
            // (Later) also check cities if you store them similarly
        }
        return null;
    }

    /**
     * Finds an existing road on an edge, if any.
     */
    public Road getRoadAt(Edge edge) {
        for (Player p : players) {
            for (Road r : p.getRoads()) {
                Edge re = r.getEdge();
                // Edge has no equals(), so compare endpoints
                boolean sameDir = re.getStart().equals(edge.getStart()) && re.getEnd().equals(edge.getEnd());
                boolean oppDir  = re.getStart().equals(edge.getEnd()) && re.getEnd().equals(edge.getStart());
                if (sameDir || oppDir) return r;
            }
        }
        return null;
    }

    /**
     * Builds a settlement for player at the given intersection index.
     * NOTE: This currently checks only "unoccupied". Distance rule, costs, adjacency-to-road come later.
     */
    public Settlement buildSettlement(Player player, int intersectionIndex) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");

        List<Intersection> ints = board.getIntersections();
        if (intersectionIndex < 0 || intersectionIndex >= ints.size()) {
            throw new IllegalArgumentException("Intersection index out of range.");
        }

        Intersection loc = ints.get(intersectionIndex);
        Settlement existing = getSettlementAt(loc);

        if (!Validator.isValidSettlementPlacement(loc, existing)) {
            throw new IllegalArgumentException("Intersection is invalid or already occupied.");
        }

        Settlement s = new Settlement(player, loc);
        player.addSettlement(s);
        return s;
    }

    /**
     * Builds a road for player at the given edge index.
     * NOTE: This currently checks only "edge exists and not occupied". Connectivity & costs come later.
     */
    public Road buildRoad(Player player, int edgeIndex) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");

        List<Edge> edges = board.getEdges();
        if (edgeIndex < 0 || edgeIndex >= edges.size()) {
            throw new IllegalArgumentException("Edge index out of range.");
        }

        Edge e = edges.get(edgeIndex);
        if (getRoadAt(e) != null) {
            throw new IllegalArgumentException("Edge already has a road.");
        }

        if (!Validator.isValidRoadPlacement(e, player)) {
            throw new IllegalArgumentException("Invalid road placement.");
        }

        Road r = new Road(player, e);
        player.addRoad(r);
        return r;
    }

    /**
     * Main entry point for running the game from the command line.
     * Accepts an optional first argument for the number of players (2-4).
     * If no argument is provided, the user is prompted for the value.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Game game = new Game();
        int numberOfPlayers;

        if (args.length > 0) {
            try {
                numberOfPlayers = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of players: " + args[0]);
                System.out.println("Usage: java catan.main.Game [2-4]");
                return;
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter number of players (2-4):");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a whole number between 2 and 4.");
                scanner.close();
                return;
            }

            numberOfPlayers = scanner.nextInt();
            scanner.nextLine();
        }

        try {
            game.initializeGame(numberOfPlayers);
            Scanner scanner = new Scanner(System.in);
            ConsoleGameController controller = new ConsoleGameController(game, scanner);
            controller.run();
            scanner.close();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
