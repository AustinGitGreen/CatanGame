package catan.main;

import catan.board.Board;
import catan.board.Edge;
import catan.board.Intersection;
import catan.components.City;
import catan.components.Road;
import catan.components.Settlement;
import catan.players.Player;
import catan.resources.ResourcePool;
import catan.utils.Validator;
import catan.resources.Resource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;
import java.util.Collections;

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
    private boolean setupForward = true;
    private int setupRound = 0;
    private Intersection pendingSetupRoadAnchor = null;

    private static final Map<Resource, Integer> ROAD_COST = cost(Resource.WOOD, 1, Resource.BRICK, 1);
    private static final Map<Resource, Integer> SETTLEMENT_COST = cost(Resource.WOOD, 1, Resource.BRICK, 1, Resource.WHEAT, 1, Resource.SHEEP, 1);
    private static final Map<Resource, Integer> CITY_COST = cost(Resource.ORE, 3, Resource.WHEAT, 2);

    private final Random rng = new Random();

    public void initializeGame(int numberOfPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");
        }

        board = new Board();
        board.initializeBoard();
        resourcePool = new ResourcePool();

        players = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            players.add(new Player("Player " + i));
        }

        turnManager = new TurnManager(players);

        phase = GamePhase.SETUP;
        setupStep = SetupStep.PLACE_SETTLEMENT;
        setupForward = true;
        setupRound = 0;
        pendingSetupRoadAnchor = null;

        System.out.println("Game initialized with " + numberOfPlayers + " players.");
    }

    public List<Player> getPlayers() { return players; }
    public Board getBoard() { return board; }
    public ResourcePool getResourcePool() { return resourcePool; }
    public Player getCurrentPlayer() { return turnManager.getCurrentPlayer(); }

    public GamePhase getPhase() { return phase; }
    public boolean isSetupPhase() { return phase == GamePhase.SETUP; }
    public SetupStep getSetupStep() { return setupStep; }
    public int getSetupRound() { return setupRound; }
    public Intersection getPendingSetupRoadAnchor() { return pendingSetupRoadAnchor; }

    public Settlement getSettlementAt(Intersection intersection) { return board.getSettlementAt(intersection); }
    public City getCityAt(Intersection intersection) { return board.getCityAt(intersection); }
    public Road getRoadAt(Edge edge) { return board.getRoadAt(edge); }

    public void endTurn() {
        if (phase != GamePhase.NORMAL) throw new IllegalStateException("Cannot end turn during SETUP phase.");
        turnManager.nextTurn();
        System.out.println("It is now " + getCurrentPlayer().getName() + "'s turn.");
    }

    // -------------------- Build actions --------------------

    public Settlement buildSettlement(Player player, int intersectionIndex) {
        ensureNormalPhase("buildSettlement");
        ensureCurrentPlayer(player, "buildSettlement");
        payCostToBank(player, SETTLEMENT_COST);
        try {
            return placeSettlementInternal(player, intersectionIndex, false);
        } catch (RuntimeException ex) {
            refundFromBank(player, SETTLEMENT_COST);
            throw ex;
        }
    }

    public Road buildRoad(Player player, int edgeIndex) {
        ensureNormalPhase("buildRoad");
        ensureCurrentPlayer(player, "buildRoad");
        payCostToBank(player, ROAD_COST);
        try {
            return placeRoadInternal(player, edgeIndex, false);
        } catch (RuntimeException ex) {
            refundFromBank(player, ROAD_COST);
            throw ex;
        }
    }

    public City buildCity(Player player, int intersectionIndex) {
        ensureNormalPhase("buildCity");
        ensureCurrentPlayer(player, "buildCity");
        payCostToBank(player, CITY_COST);
        try {
            return upgradeSettlementToCityInternal(player, intersectionIndex);
        } catch (RuntimeException ex) {
            refundFromBank(player, CITY_COST);
            throw ex;
        }
    }

    public Settlement placeSetupSettlement(Player player, int intersectionIndex) {
        ensureSetupPhase("placeSetupSettlement");
        ensureCurrentPlayer(player, "placeSetupSettlement");
        if (setupStep != SetupStep.PLACE_SETTLEMENT) {
            throw new IllegalStateException("Setup step is " + setupStep + ", expected PLACE_SETTLEMENT.");
        }

        Settlement s = placeSettlementInternal(player, intersectionIndex, true);

        if (setupRound == 1) {
            grantStartingResourcesFromSecondSettlement(s);
        }

        pendingSetupRoadAnchor = s.getLocation();
        setupStep = SetupStep.PLACE_ROAD;
        return s;
    }

    public Road placeSetupRoad(Player player, int edgeIndex) {
        ensureSetupPhase("placeSetupRoad");
        ensureCurrentPlayer(player, "placeSetupRoad");
        if (setupStep != SetupStep.PLACE_ROAD) {
            throw new IllegalStateException("Setup step is " + setupStep + ", expected PLACE_ROAD.");
        }
        if (pendingSetupRoadAnchor == null) {
            throw new IllegalStateException("No pending setup settlement anchor found.");
        }

        Road r = placeRoadInternal(player, edgeIndex, true);

        pendingSetupRoadAnchor = null;
        setupStep = SetupStep.PLACE_SETTLEMENT;
        advanceSetupTurnOrderAfterRoad();
        return r;
    }

    // -------------------- Resource distribution --------------------

    public String distributeResourcesForRoll(int roll) {
        ensureNormalPhase("distributeResourcesForRoll");

        if (roll == 7) {
            return "Rolled 7: resolve robber (discard/move/steal).";
        }

        Map<Player, Map<Resource, Integer>> requested = new HashMap<>();
        Map<Resource, Integer> totalsByResource = new EnumMap<>(Resource.class);

        for (catan.board.Hex hex : board.getHexes()) {
            if (hex.getNumberToken() != roll) continue;

            // Robber blocks production on its hex
            if (board.getRobberHex() != null && board.getRobberHex() == hex) continue;

            Resource res = hex.getResource();
            if (res == null) continue;
            if (res == Resource.DESERT) continue;

            for (Intersection corner : board.getCornersForHex(hex)) {
                catan.components.City city = board.getCityAt(corner);
                if (city != null) {
                    Player owner = city.getOwner();
                    requested.computeIfAbsent(owner, k -> new EnumMap<>(Resource.class)).merge(res, 2, Integer::sum);
                    totalsByResource.merge(res, 2, Integer::sum);
                    continue;
                }
                Settlement settlement = board.getSettlementAt(corner);
                if (settlement != null) {
                    Player owner = settlement.getOwner();
                    requested.computeIfAbsent(owner, k -> new EnumMap<>(Resource.class)).merge(res, 1, Integer::sum);
                    totalsByResource.merge(res, 1, Integer::sum);
                }
            }
        }

        if (requested.isEmpty()) return "No settlements/cities produced resources on " + roll + ".";

        // Bank-shortage rule: if bank can't cover a resource type fully, nobody gets that resource this roll.
        for (Map.Entry<Resource, Integer> e : totalsByResource.entrySet()) {
            Resource res = e.getKey();
            int needed = e.getValue();
            if (!resourcePool.hasEnoughResource(res, needed)) {
                for (Map<Resource, Integer> m : requested.values()) m.remove(res);
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("Resource distribution for roll ").append(roll).append(":\n");
        boolean any = false;

        for (Map.Entry<Player, Map<Resource, Integer>> pe : requested.entrySet()) {
            Player p = pe.getKey();
            Map<Resource, Integer> give = pe.getValue();
            if (give.isEmpty()) continue;

            any = true;
            report.append("- ").append(p.getName()).append(" gets ");
            boolean first = true;

            for (Map.Entry<Resource, Integer> re : give.entrySet()) {
                Resource res = re.getKey();
                int amt = re.getValue();
                if (!first) report.append(", ");
                report.append(amt).append(" ").append(res);
                first = false;

                resourcePool.removeResource(res, amt);
                p.getInventory().addResource(res, amt);
            }
            report.append(".\n");
        }

        if (!any) return "Bank could not cover payouts for this roll (no resources distributed).";
        return report.toString();
    }

    // -------------------- Robber (roll of 7) --------------------

    public boolean mustDiscardOnSeven(Player player) {
        if (player == null) return false;
        return player.getInventory().getTotalResourceCards() > 7;
    }

    public int getDiscardCountOnSeven(Player player) {
        int total = player.getInventory().getTotalResourceCards();
        return (total > 7) ? (total / 2) : 0;
    }

    public void discardResourcesToBank(Player player, Map<Resource, Integer> discard) {
        ensureNormalPhase("discardResourcesToBank");
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (discard == null) throw new IllegalArgumentException("Discard map cannot be null");

        int required = getDiscardCountOnSeven(player);
        if (required <= 0) return;

        int sum = 0;
        for (Map.Entry<Resource, Integer> e : discard.entrySet()) {
            Resource r = e.getKey();
            int amt = (e.getValue() == null) ? 0 : e.getValue();
            if (r == null) continue;
            if (r == Resource.DESERT) throw new IllegalArgumentException("Cannot discard DESERT");
            if (amt < 0) throw new IllegalArgumentException("Discard amounts cannot be negative");
            if (!player.getInventory().hasEnoughResource(r, amt)) throw new IllegalArgumentException("Not enough " + r + " to discard");
            sum += amt;
        }
        if (sum != required) throw new IllegalArgumentException("Must discard exactly " + required + " cards (you entered " + sum + ")");

        player.getInventory().removeResources(discard);
        resourcePool.addResources(discard);
    }

    public int getRobberHexIndex() {
        catan.board.Hex rh = board.getRobberHex();
        if (rh == null) return -1;
        return board.getHexes().indexOf(rh);
    }

    public void moveRobberToHex(int hexIndex) {
        ensureNormalPhase("moveRobberToHex");
        if (hexIndex < 0 || hexIndex >= board.getHexes().size()) throw new IllegalArgumentException("Hex index out of range");
        int currentIdx = getRobberHexIndex();
        if (currentIdx == hexIndex) throw new IllegalArgumentException("Robber must be moved to a different hex");
        board.moveRobberTo(board.getHexes().get(hexIndex));
    }

    public List<Player> getRobbablePlayers(Player currentPlayer) {
        catan.board.Hex rh = board.getRobberHex();
        if (rh == null) return Collections.emptyList();

        Map<String, Player> uniq = new HashMap<>();
        for (Intersection corner : board.getCornersForHex(rh)) {
            catan.components.City city = board.getCityAt(corner);
            if (city != null) {
                Player owner = city.getOwner();
                if (owner != null && owner != currentPlayer && owner.getInventory().hasAnyResources()) uniq.put(owner.getName(), owner);
                continue;
            }
            Settlement s = board.getSettlementAt(corner);
            if (s != null) {
                Player owner = s.getOwner();
                if (owner != null && owner != currentPlayer && owner.getInventory().hasAnyResources()) uniq.put(owner.getName(), owner);
            }
        }
        return new ArrayList<>(uniq.values());
    }

    public Resource stealRandomResource(Player thief, Player victim) {
        ensureNormalPhase("stealRandomResource");
        if (thief == null || victim == null) throw new IllegalArgumentException("Players cannot be null");
        if (!victim.getInventory().hasAnyResources()) return null;

        int total = victim.getInventory().getTotalResourceCards();
        int pick = rng.nextInt(total) + 1;

        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;
            int c = victim.getInventory().getResourceCount(r);
            if (c <= 0) continue;
            pick -= c;
            if (pick <= 0) {
                victim.getInventory().removeResource(r, 1);
                thief.getInventory().addResource(r, 1);
                return r;
            }
        }
        return null;
    }

    // -------------------- Internals --------------------

    private Settlement placeSettlementInternal(Player player, int intersectionIndex, boolean isSetup) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        List<Intersection> ints = board.getIntersections();
        if (intersectionIndex < 0 || intersectionIndex >= ints.size()) throw new IllegalArgumentException("Intersection index out of range.");
        Intersection loc = ints.get(intersectionIndex);

        if (!Validator.isValidSettlementPlacement(board, player, loc, isSetup)) throw new IllegalArgumentException("Invalid settlement placement.");

        Settlement s = new Settlement(player, loc);
        board.placeSettlement(s);
        player.addSettlement(s);
        return s;
    }
    
    private City upgradeSettlementToCityInternal(Player player, int intersectionIndex) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        List<Intersection> ints = board.getIntersections();
        if (intersectionIndex < 0 || intersectionIndex >= ints.size()) throw new IllegalArgumentException("Intersection index out of range.");

        Intersection loc = ints.get(intersectionIndex);
        Settlement settlement = board.getSettlementAt(loc);
        if (settlement == null) throw new IllegalArgumentException("No settlement exists at this intersection.");
        if (settlement.getOwner() != player) throw new IllegalArgumentException("You can only upgrade your own settlement.");

        City city = new City(player, loc);
        board.upgradeSettlementToCity(city);
        player.upgradeSettlementToCity(settlement, city);
        return city;
    }

    public List<Integer> getValidSettlementPlacements(Player player, boolean isSetup) {
        List<Integer> valid = new ArrayList<>();
        List<Intersection> ints = board.getIntersections();
        for (int i = 0; i < ints.size(); i++) {
            if (Validator.isValidSettlementPlacement(board, player, ints.get(i), isSetup)) {
                valid.add(i);
            }
        }
        return valid;
    }
    
    public List<Integer> getValidCityPlacements(Player player) {
        List<Integer> valid = new ArrayList<>();
        if (player == null) return valid;

        List<Intersection> ints = board.getIntersections();
        for (int i = 0; i < ints.size(); i++) {
            Intersection intersection = ints.get(i);
            Settlement settlement = board.getSettlementAt(intersection);
            if (settlement != null && settlement.getOwner() == player) {
                valid.add(i);
            }
        }
        return valid;
    }

    public List<Integer> getValidRoadPlacements(Player player, boolean isSetup) {
        List<Integer> valid = new ArrayList<>();
        List<Edge> es = board.getEdges();
        for (int i = 0; i < es.size(); i++) {
            Edge e = es.get(i);
            boolean ok = isSetup
                    ? Validator.isValidSetupRoadPlacement(board, player, e, pendingSetupRoadAnchor)
                    : Validator.isValidRoadPlacement(board, player, e);
            if (ok) valid.add(i);
        }
        return valid;
    }
    
    public List<Integer> getValidRobberHexDestinations() {
        List<Integer> valid = new ArrayList<>();
        int current = getRobberHexIndex();
        for (int i = 0; i < board.getHexes().size(); i++) {
            if (i != current) valid.add(i);
        }
        return valid;
    }



    private Road placeRoadInternal(Player player, int edgeIndex, boolean isSetup) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        List<Edge> es = board.getEdges();
        if (edgeIndex < 0 || edgeIndex >= es.size()) throw new IllegalArgumentException("Edge index out of range.");
        Edge edge = es.get(edgeIndex);

        if (isSetup) {
            if (!Validator.isValidSetupRoadPlacement(board, player, edge, pendingSetupRoadAnchor)) throw new IllegalArgumentException("Invalid setup road placement.");
        } else {
            if (!Validator.isValidRoadPlacement(board, player, edge)) throw new IllegalArgumentException("Invalid road placement.");
        }

        Road r = new Road(player, edge);
        board.placeRoad(r);
        player.addRoad(r);
        return r;
    }

    private void ensureSetupPhase(String action) {
        if (phase != GamePhase.SETUP) throw new IllegalStateException(action + " is only allowed during SETUP phase.");
    }

    private void ensureNormalPhase(String action) {
        if (phase != GamePhase.NORMAL) throw new IllegalStateException(action + " is only allowed during NORMAL phase.");
    }

    private void ensureCurrentPlayer(Player player, String action) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        if (turnManager == null) throw new IllegalStateException("Game has not been initialized.");
        if (player != turnManager.getCurrentPlayer()) {
            throw new IllegalStateException(action + " can only be performed by the current player.");
        }
    }

    private void advanceSetupTurnOrderAfterRoad() {
        int n = turnManager.getPlayerCount();
        int idx = turnManager.getCurrentPlayerIndex();

        if (setupForward) {
            if (idx == n - 1) {
                setupForward = false;
                setupRound = 1;
            } else {
                turnManager.nextTurn();
            }
        } else {
            if (idx == 0) {
                phase = GamePhase.NORMAL;
                turnManager.setCurrentPlayerIndex(0);
            } else {
                turnManager.previousTurn();
            }
        }
    }

    public boolean checkVictory() {
        for (Player player : players) if (player.getVictoryPoints() >= 10) return true;
        return false;
    }

    public Player getWinningPlayer() {
        for (Player player : players) if (player.getVictoryPoints() >= 10) return player;
        return null;
    }

    // -------------------- Economy helpers --------------------

    private static Map<Resource, Integer> cost(Object... kv) {
        Map<Resource, Integer> m = new EnumMap<>(Resource.class);
        for (int i = 0; i < kv.length; i += 2) {
            Resource r = (Resource) kv[i];
            int v = (Integer) kv[i + 1];
            m.put(r, v);
        }
        return m;
    }

    private void payCostToBank(Player player, Map<Resource, Integer> cost) {
        if (!player.getInventory().hasEnoughResources(cost)) throw new IllegalArgumentException("Not enough resources to build.");
        player.getInventory().removeResources(cost);
        resourcePool.addResources(cost);
    }

    private void refundFromBank(Player player, Map<Resource, Integer> cost) {
        resourcePool.removeResources(cost);
        player.getInventory().addResources(cost);
    }

    private void grantStartingResourcesFromSecondSettlement(Settlement settlement) {
        Intersection loc = settlement.getLocation();
        Player owner = settlement.getOwner();

        for (catan.board.Hex hex : board.getHexesTouchingIntersection(loc)) {
            Resource res = hex.getResource();
            if (res == null) continue;
            if (res == Resource.DESERT) continue;

            if (resourcePool.hasEnoughResource(res, 1)) {
                resourcePool.removeResource(res, 1);
                owner.getInventory().addResource(res, 1);
            }
        }
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting the game...");
        while (!checkVictory()) {
            Player currentPlayer = getCurrentPlayer();
            System.out.println(currentPlayer.getName() + ", it's your turn.");
            System.out.println("Rolling the dice...");
            int diceRoll = (int) (Math.random() * 6 + 1) + (int) (Math.random() * 6 + 1);
            System.out.println("You rolled: " + diceRoll);
            System.out.println("Enter any key to end your turn.");
            scanner.nextLine();
            endTurn();
        }
        Player winner = getWinningPlayer();
        System.out.println("Congratulations, " + winner.getName() + "! You have won the game with " + winner.getVictoryPoints() + " victory points!");
        scanner.close();
    }

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
