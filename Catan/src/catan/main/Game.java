package catan.main;

import catan.board.Board;
import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Road;
import catan.components.Settlement;
import catan.players.Player;
import catan.resources.ResourcePool;
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

    /** Game phases. */
    public enum GamePhase { SETUP, NORMAL }

    /** Setup step phases (Catan initial placement). */
    public enum SetupStep { PLACE_SETTLEMENT, PLACE_ROAD }

    private GamePhase phase = GamePhase.SETUP;
    private SetupStep setupStep = SetupStep.PLACE_SETTLEMENT;
    private boolean setupForward = true; // forward in round 1, reverse in round 2
    private int setupRound = 0; // 0 = first pass, 1 = second pass
    private Intersection pendingSetupRoadAnchor = null; // settlement just placed; next road must touch this

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

        // Setup phase starts immediately with Player 1 placing a settlement.
        phase = GamePhase.SETUP;
        setupStep = SetupStep.PLACE_SETTLEMENT;
        setupForward = true;
        setupRound = 0;
        pendingSetupRoadAnchor = null;

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

    /** Convenience: occupancy lookup for UI/debug. */
    public Settlement getSettlementAt(Intersection intersection) {
        return board.getSettlementAt(intersection);
    }

    /** Convenience: occupancy lookup for UI/debug. */
    public Road getRoadAt(Edge edge) {
        return board.getRoadAt(edge);
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

    public GamePhase getPhase() {
        return phase;
    }

    public boolean isSetupPhase() {
        return phase == GamePhase.SETUP;
    }

    public SetupStep getSetupStep() {
        return setupStep;
    }

    public int getSetupRound() {
        return setupRound;
    }

    /**
     * During setup, after a settlement is placed, the road must touch that intersection.
     */
    public Intersection getPendingSetupRoadAnchor() {
        return pendingSetupRoadAnchor;
    }

    /**
     * Ends the current player's turn and moves to the next player.
     */
    public void endTurn() {
        if (phase != GamePhase.NORMAL) {
            throw new IllegalStateException("Cannot end turn during SETUP phase.");
        }
        turnManager.nextTurn();
        System.out.println("It is now " + getCurrentPlayer().getName() + "'s turn.");
    }

    // -------------------- Placement + Validation --------------------

    /**
     * Normal-play settlement build (requires NORMAL rules).
     */
    public Settlement buildSettlement(Player player, int intersectionIndex) {
        ensureNormalPhase("buildSettlement");
        return placeSettlementInternal(player, intersectionIndex, false);
    }

    /**
     * Normal-play road build (requires NORMAL rules).
     */
    public Road buildRoad(Player player, int edgeIndex) {
        ensureNormalPhase("buildRoad");
        return placeRoadInternal(player, edgeIndex, false);
    }

    /**
     * Setup placement: place a settlement (relaxed connectivity rules, but distance rule still applies).
     */
    public Settlement placeSetupSettlement(Player player, int intersectionIndex) {
        ensureSetupPhase("placeSetupSettlement");
        if (setupStep != SetupStep.PLACE_SETTLEMENT) {
            throw new IllegalStateException("Setup step is " + setupStep + ", expected PLACE_SETTLEMENT.");
        }

        Settlement s = placeSettlementInternal(player, intersectionIndex, true);
        pendingSetupRoadAnchor = s.getLocation();
        setupStep = SetupStep.PLACE_ROAD;
        return s;
    }

    /**
     * Setup placement: place a road that must touch the previously placed settlement.
     */
    public Road placeSetupRoad(Player player, int edgeIndex) {
        ensureSetupPhase("placeSetupRoad");
        if (setupStep != SetupStep.PLACE_ROAD) {
            throw new IllegalStateException("Setup step is " + setupStep + ", expected PLACE_ROAD.");
        }
        if (pendingSetupRoadAnchor == null) {
            throw new IllegalStateException("No pending setup settlement anchor found.");
        }

        Road r = placeRoadInternal(player, edgeIndex, true);

        // Advance setup turn order and step.
        pendingSetupRoadAnchor = null;
        setupStep = SetupStep.PLACE_SETTLEMENT;
        advanceSetupTurnOrderAfterRoad();
        return r;
    }

    private Settlement placeSettlementInternal(Player player, int intersectionIndex, boolean isSetup) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        List<Intersection> ints = board.getIntersections();
        if (intersectionIndex < 0 || intersectionIndex >= ints.size()) {
            throw new IllegalArgumentException("Intersection index out of range.");
        }
        Intersection loc = ints.get(intersectionIndex);

        if (!Validator.isValidSettlementPlacement(board, player, loc, isSetup)) {
            throw new IllegalArgumentException("Invalid settlement placement.");
        }

        Settlement s = new Settlement(player, loc);
        board.placeSettlement(s);
        player.addSettlement(s);
        return s;
    }

    private Road placeRoadInternal(Player player, int edgeIndex, boolean isSetup) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        List<Edge> es = board.getEdges();
        if (edgeIndex < 0 || edgeIndex >= es.size()) {
            throw new IllegalArgumentException("Edge index out of range.");
        }
        Edge edge = es.get(edgeIndex);

        if (isSetup) {
            if (!Validator.isValidSetupRoadPlacement(board, player, edge, pendingSetupRoadAnchor)) {
                throw new IllegalArgumentException("Invalid setup road placement.");
            }
        } else {
            if (!Validator.isValidRoadPlacement(board, player, edge)) {
                throw new IllegalArgumentException("Invalid road placement.");
            }
        }

        Road r = new Road(player, edge);
        board.placeRoad(r);
        player.addRoad(r);
        return r;
    }

    private void ensureSetupPhase(String action) {
        if (phase != GamePhase.SETUP) {
            throw new IllegalStateException(action + " is only allowed during SETUP phase.");
        }
    }

    private void ensureNormalPhase(String action) {
        if (phase != GamePhase.NORMAL) {
            throw new IllegalStateException(action + " is only allowed during NORMAL phase.");
        }
    }

    private void advanceSetupTurnOrderAfterRoad() {
        int n = turnManager.getPlayerCount();
        int idx = turnManager.getCurrentPlayerIndex();

        if (setupForward) {
            if (idx == n - 1) {
                // End of first pass: switch to reverse, stay on last player.
                setupForward = false;
                setupRound = 1;
                // Do not change idx
            } else {
                turnManager.nextTurn();
            }
        } else {
            if (idx == 0) {
                // End of second pass: setup complete -> NORMAL phase
                phase = GamePhase.NORMAL;
                // In normal play, Player 1 starts (standard rule).
                turnManager.setCurrentPlayerIndex(0);
            } else {
                turnManager.previousTurn();
            }
        }
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
     * Starts the game loop, interacting with the console.
     * NOTE: This is legacy (kept for reference). Prefer ConsoleGameController.
     */
    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Starting the game...");
        while (!checkVictory()) {
            Player currentPlayer = getCurrentPlayer();
            System.out.println(currentPlayer.getName() + ", it's your turn.");

            // Example of interaction: Roll dice (simulate with random numbers)
            System.out.println("Rolling the dice...");
            int diceRoll = (int) (Math.random() * 6 + 1) + (int) (Math.random() * 6 + 1);
            System.out.println("You rolled: " + diceRoll);

            // End turn
            System.out.println("Enter any key to end your turn.");
            scanner.nextLine();
            endTurn();
        }

        Player winner = getWinningPlayer();
        System.out.println("Congratulations, " + winner.getName() + "! You have won the game with " + winner.getVictoryPoints() + " victory points!");

        scanner.close();
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
            // Use the console controller (setup + normal phases).
            Scanner scanner = new Scanner(System.in);
            ConsoleGameController controller = new ConsoleGameController(game, scanner);
            controller.run();
            scanner.close();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
