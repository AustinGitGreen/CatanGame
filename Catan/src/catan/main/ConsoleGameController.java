package catan.main;

import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Road;
import catan.components.Settlement;
import catan.players.Player;
import catan.utils.Dice;

import java.util.List;
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

        // Main loop: setup first, then normal play until victory
        while (!game.checkVictory()) {

            if (game.isSetupPhase()) {
                runSetupTurn();
                continue;
            }

            runNormalTurn();
        }

        Player winner = game.getWinningPlayer();
        System.out.println("\n🎉 Winner: " + winner.getName() + " with " + winner.getVictoryPoints() + " VP!");
    }

    // -------------------- SETUP --------------------

    private void runSetupTurn() {
        Player current = game.getCurrentPlayer();

        System.out.println("\n==============================");
        System.out.println("SETUP PHASE | Round " + (game.getSetupRound() + 1));
        System.out.println("Player: " + current.getName());
        System.out.println("Step: " + game.getSetupStep());
        System.out.println("==============================");

        boolean done = false;
        while (!done && game.isSetupPhase()) {
            printSetupMenu();
            int choice = promptInt("Choose an option: ", 1, 6);

            switch (choice) {
                case 1 : {
                    if (game.getSetupStep() != Game.SetupStep.PLACE_SETTLEMENT) {
                        System.out.println("❌ Not time to place a settlement. Current step: " + game.getSetupStep());
                        break;
                    }
                    placeSetupSettlementFlow(current);
                    // If settlement succeeds, step becomes PLACE_ROAD; keep looping.
                }
                case 2 : {
                    if (game.getSetupStep() != Game.SetupStep.PLACE_ROAD) {
                        System.out.println("❌ Not time to place a road. Current step: " + game.getSetupStep());
                        break;
                    }
                    placeSetupRoadFlow(current);
                    // If road succeeds, Game advances setup order; exit this player's setup loop.
                    done = true;
                }
                case 3 : viewMyInfo(current);
                case 4 : listIntersections();
                case 5 : listEdges();
                case 6 : {
                    // In setup you normally can't "skip" — but allow viewing-only exit.
                    System.out.println("Returning to setup prompt...");
                    done = true;
                }
                default : System.out.println("Unknown option.");
            }
        }
    }

    private void printSetupMenu() {
        System.out.println("\n--- Setup Menu ---");
        System.out.println("1) Place Settlement");
        System.out.println("2) Place Road (must touch your just-placed settlement)");
        System.out.println("3) View My Info");
        System.out.println("4) List Intersections");
        System.out.println("5) List Edges");
        System.out.println("6) Back");
    }

    private void placeSetupSettlementFlow(Player current) {
        listIntersections();
        int idx = promptInt("Intersection index to place settlement on: ",
                0, game.getBoard().getIntersections().size() - 1);

        try {
            Settlement s = game.placeSetupSettlement(current, idx);
            Intersection loc = s.getLocation();
            System.out.println("✅ Setup settlement placed at (" + loc.getX() + "," + loc.getY() + ")");
            System.out.println("Next: place a road that touches this settlement.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't place setup settlement: " + ex.getMessage());
        }
    }

    private void placeSetupRoadFlow(Player current) {
        listEdges();
        int idx = promptInt("Edge index to place road on: ",
                0, game.getBoard().getEdges().size() - 1);

        try {
            Road r = game.placeSetupRoad(current, idx);
            System.out.println("✅ Setup road placed on edge " + formatEdge(r.getEdge()));

            // The game may transition to NORMAL after the last setup road
            if (!game.isSetupPhase()) {
                System.out.println("\n✅ Setup complete! Entering NORMAL play.");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't place setup road: " + ex.getMessage());
        }
    }

    // -------------------- NORMAL --------------------

    private void runNormalTurn() {
        Player current = game.getCurrentPlayer();

        System.out.println("\n==============================");
        System.out.println("Turn: " + current.getName());
        System.out.println("Victory Points: " + current.getVictoryPoints());
        System.out.println("==============================");

        // 1) ROLL (mandatory)
        int roll = handleRollPhase();
        System.out.println("Rolled: " + roll);

        // (Later) game.distributeResources(roll) and print a report.

        // 2) ACTION PHASE
        handleActionPhase(current);

        // 3) END TURN
        game.endTurn();
    }

    private int handleRollPhase() {
        System.out.println("Press ENTER to roll dice...");
        scanner.nextLine();
        return dice.roll();
    }

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
        int idx = promptInt("Edge index to place road on: ",
                0, game.getBoard().getEdges().size() - 1);

        try {
            Road road = game.buildRoad(current, idx);
            System.out.println("✅ Built road for " + road.getOwner().getName()
                    + " on edge " + formatEdge(road.getEdge()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't build road: " + ex.getMessage());
        }
    }

    private void buildSettlementFlow(Player current) {
        listIntersections();
        int idx = promptInt("Intersection index to place settlement on: ",
                0, game.getBoard().getIntersections().size() - 1);

        try {
            Settlement settlement = game.buildSettlement(current, idx);
            Intersection loc = settlement.getLocation();
            System.out.println("✅ Built settlement for " + settlement.getOwner().getName()
                    + " at (" + loc.getX() + "," + loc.getY() + ")"
                    + " | VP now: " + current.getVictoryPoints());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't build settlement: " + ex.getMessage());
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

        // When you wire Inventory into Player:
        // System.out.println("Resources: " + p.getInventory());
    }

    private void viewAllPlayers() {
        System.out.println("\n--- Players ---");
        for (Player p : game.getPlayers()) {
            System.out.println(p.getName()
                    + " | VP: " + p.getVictoryPoints()
                    + " | S:" + p.getSettlements().size()
                    + " C:" + p.getCities().size()
                    + " R:" + p.getRoads().size());
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
