package catan.main;

import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Road;
import catan.components.Settlement;
import catan.players.Player;
import catan.resources.Resource;
import catan.utils.Dice;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.Scanner;

public class ConsoleGameController {

    private final Game game;
    private final Scanner scanner;
    private final Dice dice;

    public ConsoleGameController(Game game, Scanner scanner) {
        this.game = game;
        this.scanner = scanner;
        this.dice = new Dice();
    }

    public void run() {
        System.out.println("Starting Catan (console controller)...");

        while (!game.checkVictory()) {
            Player current = game.getCurrentPlayer();

            if (game.isSetupPhase()) {
                System.out.println("\n==============================");
                System.out.println("SETUP PHASE | Round " + (game.getSetupRound() + 1));
                System.out.println("Player: " + current.getName());
                System.out.println("Step: " + game.getSetupStep());
                System.out.println("==============================");

                handleSetupPhase(current);
                continue;
            }

            System.out.println("\n==============================");
            System.out.println("Turn: " + current.getName());
            System.out.println("Victory Points: " + current.getVictoryPoints());
            System.out.println("==============================");

            // 1) ROLL (mandatory)
            int roll = handleRollPhase();
            System.out.println("Rolled: " + roll);

            if (roll == 7) {
                handleRobberOnSeven(current);
            } else {
                System.out.println(game.distributeResourcesForRoll(roll));
            }

            // 2) ACTION PHASE
            handleActionPhase(current);

            // 3) END TURN
            game.endTurn();
        }

        Player winner = game.getWinningPlayer();
        System.out.println("\n🎉 Winner: " + winner.getName() + " with " + winner.getVictoryPoints() + " VP!");
    }

    private int handleRollPhase() {
        System.out.println("Press ENTER to roll dice...");
        scanner.nextLine();
        return dice.roll();
    }

    // -------------------- SETUP --------------------

    private void handleSetupPhase(Player current) {
        boolean done = false;
        while (!done && game.isSetupPhase() && game.getCurrentPlayer() == current) {
            printSetupMenu();
            int choice = promptInt("Choose an action: ", 1, 6);

            switch (choice) {
                case 1 : {
                    if (game.getSetupStep() == Game.SetupStep.PLACE_SETTLEMENT) {
                        done = setupSettlementFlow(current);
                    } else {
                        done = setupRoadFlow(current);
                    }
                }
                case 2 : viewMyInfo(current);
                case 3 : viewAllPlayers();
                case 4 : listIntersections();
                case 5 : listEdges();
                case 6 : viewBoardSummary();
                default : System.out.println("Unknown option.");
            }
        }
    }

    private void printSetupMenu() {
        System.out.println("\n--- Setup Phase ---");
        if (game.getSetupStep() == Game.SetupStep.PLACE_SETTLEMENT) {
            System.out.println("1) Place Settlement (setup)");
        } else {
            System.out.println("1) Place Road (setup)");
        }
        System.out.println("2) View My Info");
        System.out.println("3) View All Players (VP + counts)");
        System.out.println("4) List Intersections");
        System.out.println("5) List Edges");
        System.out.println("6) View Board Summary");
    }

    private boolean setupSettlementFlow(Player current) {
        listIntersections();
        int idx = promptInt("Intersection index to place settlement on: ", 0, game.getBoard().getIntersections().size() - 1);

        try {
            Settlement settlement = game.placeSetupSettlement(current, idx);
            Intersection loc = settlement.getLocation();
            System.out.println("✅ (Setup) Built settlement for " + settlement.getOwner().getName()
                    + " at (" + loc.getX() + "," + loc.getY() + ")");
            System.out.println("Now place a road touching that settlement.");
            return true;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't place setup settlement: " + ex.getMessage());
            return false;
        }
    }

    private boolean setupRoadFlow(Player current) {
        listEdges();
        int idx = promptInt("Edge index to place road on: ", 0, game.getBoard().getEdges().size() - 1);

        try {
            Road road = game.placeSetupRoad(current, idx);
            System.out.println("✅ (Setup) Built road for " + road.getOwner().getName() + " on edge " + formatEdge(road.getEdge()));
            return true;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't place setup road: " + ex.getMessage());
            return false;
        }
    }

    // -------------------- NORMAL ACTION PHASE --------------------

    private void handleActionPhase(Player current) {
        boolean done = false;

        while (!done) {
            printActionMenu();
            int choice = promptInt("Choose an action: ", 1, 8);

            switch (choice) {
                case 1 : buildRoadFlow(current);
                case 2 : buildSettlementFlow(current);
                case 3 : viewMyInfo(current);
                case 4 : viewAllPlayers();
                case 5 : listIntersections();
                case 6 : listEdges();
                case 7 : viewBoardSummary();
                case 8 : done = true;
                default : System.out.println("Unknown option.");
            }
        }
    }

    private void printActionMenu() {
        System.out.println("\n--- Action Phase ---");
        System.out.println("1) Build Road");
        System.out.println("2) Build Settlement");
        System.out.println("3) View My Info");
        System.out.println("4) View All Players (VP + counts)");
        System.out.println("5) List Intersections");
        System.out.println("6) List Edges");
        System.out.println("7) View Board Summary");
        System.out.println("8) End Turn");
    }

    private void buildRoadFlow(Player current) {
        listEdges();
        int idx = promptInt("Edge index to place road on: ", 0, game.getBoard().getEdges().size() - 1);

        try {
            Road road = game.buildRoad(current, idx);
            System.out.println("✅ Built road for " + road.getOwner().getName() + " on edge " + formatEdge(road.getEdge()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't build road: " + ex.getMessage());
        }
    }

    private void buildSettlementFlow(Player current) {
        listIntersections();
        int idx = promptInt("Intersection index to place settlement on: ", 0, game.getBoard().getIntersections().size() - 1);

        try {
            Settlement settlement = game.buildSettlement(current, idx);
            Intersection loc = settlement.getLocation();
            System.out.println("✅ Built settlement for " + settlement.getOwner().getName()
                    + " at (" + loc.getX() + "," + loc.getY() + ")");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't build settlement: " + ex.getMessage());
        }
    }

    // -------------------- ROBBER (roll of 7) --------------------

    private void handleRobberOnSeven(Player current) {
        System.out.println("\n⚠ Rolled a 7! Robber activated.");

        // 1) Discard
        for (Player p : game.getPlayers()) {
            if (!game.mustDiscardOnSeven(p)) continue;

            int mustDiscard = game.getDiscardCountOnSeven(p);
            System.out.println("\n" + p.getName() + " has " + p.getInventory().getTotalResourceCards()
                    + " cards and must discard " + mustDiscard + ".");

            Map<Resource, Integer> discard = promptDiscardMap(p, mustDiscard);
            try {
                game.discardResourcesToBank(p, discard);
                System.out.println("✅ " + p.getName() + " discarded " + mustDiscard + ". Remaining: " + p.getInventory());
            } catch (IllegalArgumentException ex) {
                System.out.println("❌ Discard failed: " + ex.getMessage());
                discard = promptDiscardMap(p, mustDiscard);
                game.discardResourcesToBank(p, discard);
                System.out.println("✅ " + p.getName() + " discarded " + mustDiscard + ". Remaining: " + p.getInventory());
            }
        }

        // 2) Move robber
        moveRobberFlow();

        // 3) Steal
        List<Player> victims = game.getRobbablePlayers(current);
        if (victims.isEmpty()) {
            System.out.println("No players adjacent to the robber to steal from.");
            return;
        }

        System.out.println("\nChoose a player to steal from:");
        for (int i = 0; i < victims.size(); i++) {
            Player v = victims.get(i);
            System.out.println(i + ") " + v.getName() + " (cards: " + v.getInventory().getTotalResourceCards() + ")");
        }

        int choice = promptInt("Victim index: ", 0, victims.size() - 1);
        Player victim = victims.get(choice);

        Resource stolen = game.stealRandomResource(current, victim);
        if (stolen == null) {
            System.out.println("Tried to steal, but " + victim.getName() + " had no resources.");
        } else {
            System.out.println("✅ " + current.getName() + " stole 1 " + stolen + " from " + victim.getName() + ".");
            System.out.println(current.getName() + " now has: " + current.getInventory());
        }
    }

    private Map<Resource, Integer> promptDiscardMap(Player player, int mustDiscard) {
        System.out.println("Current resources: " + player.getInventory());
        System.out.println("Enter how many of each to discard. Must total " + mustDiscard + ".");

        Map<Resource, Integer> discard = new EnumMap<>(Resource.class);
        int remaining = mustDiscard;

        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;

            int have = player.getInventory().getResourceCount(r);
            if (have <= 0) continue;

            int max = Math.min(have, remaining);
            int amt = promptInt("Discard " + r + " (0-" + max + "): ", 0, max);
            if (amt > 0) {
                discard.put(r, amt);
                remaining -= amt;
            }
            if (remaining == 0) break;
        }

        if (remaining != 0) {
            System.out.println("⚠ Discard total not met (remaining " + remaining + ").");
        }
        return discard;
    }

    private void moveRobberFlow() {
        System.out.println("\nMove the robber to a hex.");

        int currentIdx = game.getRobberHexIndex();
        List<catan.board.Hex> hexes = game.getBoard().getHexes();

        for (int i = 0; i < hexes.size(); i++) {
            catan.board.Hex h = hexes.get(i);
            String mark = (i == currentIdx) ? "  <robber>" : "";
            System.out.println(i + ") " + h.getResource() + " (" + h.getNumberToken() + ")" + mark);
        }

        int idx = promptInt("Hex index (0-" + (hexes.size() - 1) + "): ", 0, hexes.size() - 1);
        try {
            game.moveRobberToHex(idx);
            System.out.println("✅ Robber moved to hex " + idx + ".");
        } catch (IllegalArgumentException ex) {
            System.out.println("❌ " + ex.getMessage());
            idx = promptInt("Choose a different hex index: ", 0, hexes.size() - 1);
            game.moveRobberToHex(idx);
            System.out.println("✅ Robber moved to hex " + idx + ".");
        }
    }

    // -------------------- VIEW HELPERS --------------------

    private void viewMyInfo(Player p) {
        System.out.println("\n--- My Info ---");
        System.out.println("Name: " + p.getName());
        System.out.println("Victory Points: " + p.getVictoryPoints());
        System.out.println("Settlements: " + p.getSettlements().size());
        System.out.println("Cities: " + p.getCities().size());
        System.out.println("Roads: " + p.getRoads().size());
        System.out.println("Resources: " + p.getInventory());
    }

    private void viewAllPlayers() {
        System.out.println("\n--- Players ---");
        for (Player p : game.getPlayers()) {
            System.out.println(p.getName()
                    + " | VP: " + p.getVictoryPoints()
                    + " | S:" + p.getSettlements().size()
                    + " C:" + p.getCities().size()
                    + " R:" + p.getRoads().size()
                    + " | Cards: " + p.getInventory().getTotalResourceCards());
        }
    }

    private void listIntersections() {
        List<Intersection> ints = game.getBoard().getIntersections();
        System.out.println("\n--- Intersections ---");
        for (int i = 0; i < ints.size(); i++) {
            Intersection in = ints.get(i);
            String occ = (game.getSettlementAt(in) != null || game.getBoard().getCityAt(in) != null) ? " (occupied)" : "";
            System.out.println(i + ") (" + in.getX() + "," + in.getY() + ")" + occ);
        }
    }

    private void listEdges() {
        List<Edge> edges = game.getBoard().getEdges();
        System.out.println("\n--- Edges ---");
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            String occ = game.getRoadAt(e) != null ? " (occupied)" : "";
            System.out.println(i + ") " + formatEdge(e) + occ);
        }
    }

    private void viewBoardSummary() {
        System.out.println("\n--- Board Summary ---");
        System.out.println("Hexes: " + game.getBoard().getHexes().size());
        System.out.println("Intersections: " + game.getBoard().getIntersections().size());
        System.out.println("Edges: " + game.getBoard().getEdges().size());
        System.out.println("Robber on hex index: " + game.getRobberHexIndex());
    }

    // -------------------- INPUT + FORMAT --------------------

    private int promptInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Enter a number from " + min + " to " + max + ".");
                continue;
            }
            try {
                int val = Integer.parseInt(line);
                if (val < min || val > max) {
                    System.out.println("Enter a number from " + min + " to " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private String formatEdge(Edge e) {
        return "(" + e.getStart().getX() + "," + e.getStart().getY() + ") -> ("
                + e.getEnd().getX() + "," + e.getEnd().getY() + ")";
    }
}
