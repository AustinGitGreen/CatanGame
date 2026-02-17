package catan.main;

import catan.board.Edge;
import catan.board.Intersection;
import catan.components.Road;
import catan.components.Settlement;
import catan.players.Player;
import catan.resources.Resource;
import catan.utils.Dice;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
                runSetupTurn(current);
                continue;
            }

            runNormalTurn(current);
        }

        Player winner = game.getWinningPlayer();
        System.out.println("\nWinner: " + winner.getName() + " with " + winner.getVictoryPoints() + " VP!");
    }

    // =========================
    // SETUP PHASE
    // =========================

    private void runSetupTurn(Player current) {
        System.out.println("\n==============================");
        System.out.println("SETUP PHASE | Round " + (game.getSetupRound() + 1));
        System.out.println("Player: " + current.getName());
        System.out.println("Step: " + game.getSetupStep());
        System.out.println("==============================");

        boolean done = false;
        while (!done && game.isSetupPhase() && game.getCurrentPlayer() == current) {
            printSetupMenu();
            int choice = promptInt("Choose an option: ", 1, 6);

            switch (choice) {
                case 1:
                    if (game.getSetupStep() == Game.SetupStep.PLACE_SETTLEMENT) {
                        setupSettlementFlow(current);
                    } else {
                        setupRoadFlow(current);
                        // after a setup road, the Game advances setup order; end this turn loop
                        done = true;
                    }
                    break;

                case 2:
                    viewMyInfo(current);
                    break;

                case 3:
                    viewAllPlayers();
                    break;

                case 4:
                    listIntersectionsWithOccupancy();
                    break;

                case 5:
                    listEdgesWithOccupancy();
                    break;

                case 6:
                    viewBoardSummary();
                    break;

                default:
                    System.out.println("Unknown option.");
                    break;
            }
        }
    }

    private void printSetupMenu() {
        System.out.println("\n--- Setup Menu ---");
        if (game.getSetupStep() == Game.SetupStep.PLACE_SETTLEMENT) {
            System.out.println("1) Place Settlement (only valid options shown)");
        } else {
            System.out.println("1) Place Road (must touch your just-placed settlement; only valid options shown)");
        }
        System.out.println("2) View My Info");
        System.out.println("3) View All Players");
        System.out.println("4) List Intersections");
        System.out.println("5) List Edges");
        System.out.println("6) Board Summary");
    }

    private void setupSettlementFlow(Player current) {
        List<Integer> valid = game.getValidSettlementPlacements(current, true);

        if (valid.isEmpty()) {
            System.out.println("No valid settlement placements.");
            return;
        }

        System.out.println("\nValid settlement placements:");
        for (int k = 0; k < valid.size(); k++) {
            int idx = valid.get(k);
            Intersection in = game.getBoard().getIntersections().get(idx);
            System.out.println(k + ") #" + idx + " (" + in.getX() + "," + in.getY() + ")");
        }

        int pick = promptInt("Choose option (0-" + (valid.size() - 1) + "): ", 0, valid.size() - 1);
        int chosenIntersectionIndex = valid.get(pick);

        try {
            Settlement s = game.placeSetupSettlement(current, chosenIntersectionIndex);
            Intersection loc = s.getLocation();
            System.out.println("✅ Setup settlement placed at (" + loc.getX() + "," + loc.getY() + ")");
            System.out.println("Next: place a road touching that settlement.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't place setup settlement: " + ex.getMessage());
        }
    }

    private void setupRoadFlow(Player current) {
        List<Integer> valid = game.getValidRoadPlacements(current, true);

        if (valid.isEmpty()) {
            System.out.println("No valid road placements (setup).");
            return;
        }

        System.out.println("\nValid setup road placements:");
        for (int k = 0; k < valid.size(); k++) {
            int idx = valid.get(k);
            Edge e = game.getBoard().getEdges().get(idx);
            System.out.println(k + ") #" + idx + " " + formatEdge(e));
        }

        int pick = promptInt("Choose option (0-" + (valid.size() - 1) + "): ", 0, valid.size() - 1);
        int chosenEdgeIndex = valid.get(pick);

        try {
            Road r = game.placeSetupRoad(current, chosenEdgeIndex);
            System.out.println("✅ Setup road placed on edge " + formatEdge(r.getEdge()));

            if (!game.isSetupPhase()) {
                System.out.println("\n✅ Setup complete! Entering NORMAL play.");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't place setup road: " + ex.getMessage());
        }
    }

    // =========================
    // NORMAL TURN
    // =========================

    private void runNormalTurn(Player current) {
        System.out.println("\n==============================");
        System.out.println("Turn: " + current.getName());
        System.out.println("Victory Points: " + current.getVictoryPoints());
        System.out.println("==============================");

        // 1) ROLL
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

    private int handleRollPhase() {
        System.out.println("Press ENTER to roll dice...");
        scanner.nextLine();
        return dice.roll();
    }

    private void handleActionPhase(Player current) {
        boolean done = false;

        while (!done) {
            printActionMenu();
            int choice = promptInt("Choose an action: ", 1, 9);

            switch (choice) {
                case 1:
                    buildRoadFlow(current);
                    break;

                case 2:
                    buildSettlementFlow(current);
                    break;

                case 3:
                    viewMyInfo(current);
                    break;

                case 4:
                    viewAllPlayers();
                    break;

                case 5:
                    listIntersectionsWithOccupancy();
                    break;

                case 6:
                    listEdgesWithOccupancy();
                    break;

                case 7:
                    viewBoardSummary();
                    break;

                case 8:
                    viewValidMovesSummary(current);
                    break;

                case 9:
                    done = true;
                    break;

                default:
                    System.out.println("Unknown option.");
                    break;
            }
        }
    }

    private void printActionMenu() {
        System.out.println("\n--- Action Phase ---");
        System.out.println("1) Build Road (only valid options shown)");
        System.out.println("2) Build Settlement (only valid options shown)");
        System.out.println("3) View My Info");
        System.out.println("4) View All Players");
        System.out.println("5) List Intersections");
        System.out.println("6) List Edges");
        System.out.println("7) Board Summary");
        System.out.println("8) Show Valid Moves Summary");
        System.out.println("9) End Turn");
    }

    private void buildRoadFlow(Player current) {
        List<Integer> valid = game.getValidRoadPlacements(current, false);
        if (valid.isEmpty()) {
            System.out.println("No valid road placements right now.");
            return;
        }

        System.out.println("\nValid road placements:");
        for (int k = 0; k < valid.size(); k++) {
            int idx = valid.get(k);
            Edge e = game.getBoard().getEdges().get(idx);
            System.out.println(k + ") #" + idx + " " + formatEdge(e));
        }

        int pick = promptInt("Choose option (0-" + (valid.size() - 1) + "): ", 0, valid.size() - 1);
        int chosenEdgeIndex = valid.get(pick);

        try {
            Road road = game.buildRoad(current, chosenEdgeIndex);
            System.out.println("✅ Built road on edge " + formatEdge(road.getEdge()));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't build road: " + ex.getMessage());
        }
    }

    private void buildSettlementFlow(Player current) {
        List<Integer> valid = game.getValidSettlementPlacements(current, false);
        if (valid.isEmpty()) {
            System.out.println("No valid settlement placements right now.");
            return;
        }

        System.out.println("\nValid settlement placements:");
        for (int k = 0; k < valid.size(); k++) {
            int idx = valid.get(k);
            Intersection in = game.getBoard().getIntersections().get(idx);
            System.out.println(k + ") #" + idx + " (" + in.getX() + "," + in.getY() + ")");
        }

        int pick = promptInt("Choose option (0-" + (valid.size() - 1) + "): ", 0, valid.size() - 1);
        int chosenIntersectionIndex = valid.get(pick);

        try {
            Settlement settlement = game.buildSettlement(current, chosenIntersectionIndex);
            Intersection loc = settlement.getLocation();
            System.out.println("✅ Built settlement at (" + loc.getX() + "," + loc.getY() + ")");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println("❌ Can't build settlement: " + ex.getMessage());
        }
    }

    private void viewValidMovesSummary(Player current) {
        List<Integer> roads = game.getValidRoadPlacements(current, false);
        List<Integer> settlements = game.getValidSettlementPlacements(current, false);

        System.out.println("\n--- Valid Moves Summary (" + current.getName() + ") ---");
        System.out.println("Valid roads: " + roads.size());
        System.out.println("Valid settlements: " + settlements.size());
        System.out.println("Resources: " + current.getInventory());
    }

    // =========================
    // ROBBER (7)
    // =========================

    private void handleRobberOnSeven(Player current) {
        System.out.println("\n⚠ Rolled a 7! Robber activated.");

        // 1) Discard
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player p = game.getPlayers().get(i);
            if (!game.mustDiscardOnSeven(p)) continue;

            int mustDiscard = game.getDiscardCountOnSeven(p);
            System.out.println("\n" + p.getName() + " has " + p.getInventory().getTotalResourceCards()
                    + " cards and must discard " + mustDiscard + ".");

            Map<Resource, Integer> discard = promptDiscardMap(p, mustDiscard);
            while (true) {
                try {
                    game.discardResourcesToBank(p, discard);
                    System.out.println("✅ " + p.getName() + " discarded " + mustDiscard + ". Remaining: " + p.getInventory());
                    break;
                } catch (IllegalArgumentException ex) {
                    System.out.println("❌ Discard failed: " + ex.getMessage());
                    discard = promptDiscardMap(p, mustDiscard);
                }
            }
        }

        // 2) Move robber (only valid hex destinations)
        moveRobberFlow();

        // 3) Steal (only from robbable players)
        List<Player> victims = game.getRobbablePlayers(current);
        if (victims.isEmpty()) {
            System.out.println("No players adjacent to the robber to steal from (or they have no cards).");
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

    private void moveRobberFlow() {
        List<Integer> valid = game.getValidRobberHexDestinations();
        if (valid.isEmpty()) {
            System.out.println("No valid robber destinations.");
            return;
        }

        System.out.println("\nValid robber destinations (hex index -> resource(token)):");
        for (int k = 0; k < valid.size(); k++) {
            int hexIdx = valid.get(k);
            catan.board.Hex h = game.getBoard().getHexes().get(hexIdx);
            System.out.println(k + ") #" + hexIdx + " " + h.getResource() + " (" + h.getNumberToken() + ")");
        }

        int pick = promptInt("Choose option (0-" + (valid.size() - 1) + "): ", 0, valid.size() - 1);
        int chosenHexIndex = valid.get(pick);

        while (true) {
            try {
                game.moveRobberToHex(chosenHexIndex);
                System.out.println("✅ Robber moved to hex #" + chosenHexIndex + ".");
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println("❌ " + ex.getMessage());
                pick = promptInt("Choose option (0-" + (valid.size() - 1) + "): ", 0, valid.size() - 1);
                chosenHexIndex = valid.get(pick);
            }
        }
    }

    private Map<Resource, Integer> promptDiscardMap(Player player, int mustDiscard) {
        System.out.println("Current resources: " + player.getInventory());
        System.out.println("Enter how many of each to discard. Must total " + mustDiscard + ".");

        Map<Resource, Integer> discard = new EnumMap<Resource, Integer>(Resource.class);
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
            System.out.println("⚠ Discard total not met (remaining " + remaining + "). You'll be prompted again if invalid.");
        }

        return discard;
    }

    // =========================
    // VIEW HELPERS
    // =========================

    private void viewMyInfo(Player p) {
        System.out.println("\n--- My Info ---");
        System.out.println("Name: " + p.getName());
        System.out.println("Victory Points: " + p.getVictoryPoints());
        System.out.println("Settlements: " + p.getSettlements().size());
        System.out.println("Cities: " + p.getCities().size());
        System.out.println("Roads: " + p.getRoads().size());
        System.out.println("Resources: " + p.getInventory());
        System.out.println("Cards total: " + p.getInventory().getTotalResourceCards());
    }

    private void viewAllPlayers() {
        System.out.println("\n--- Players ---");
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player p = game.getPlayers().get(i);
            System.out.println(p.getName()
                    + " | VP: " + p.getVictoryPoints()
                    + " | S:" + p.getSettlements().size()
                    + " C:" + p.getCities().size()
                    + " R:" + p.getRoads().size()
                    + " | Cards: " + p.getInventory().getTotalResourceCards());
        }
    }

    private void listIntersectionsWithOccupancy() {
        List<Intersection> ints = game.getBoard().getIntersections();
        System.out.println("\n--- Intersections ---");
        for (int i = 0; i < ints.size(); i++) {
            Intersection in = ints.get(i);
            String occ = (game.getSettlementAt(in) != null || game.getBoard().getCityAt(in) != null) ? " (occupied)" : "";
            System.out.println(i + ") (" + in.getX() + "," + in.getY() + ")" + occ);
        }
    }

    private void listEdgesWithOccupancy() {
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
        System.out.println("Robber hex index: " + game.getRobberHexIndex());
    }

    // =========================
    // INPUT + FORMAT
    // =========================

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
