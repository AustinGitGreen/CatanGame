package catan.main;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers = 0;

        System.out.println("=================================");
        System.out.println("      Welcome to Catan!          ");
        System.out.println("=================================");

        // Prompt the user for the number of players with input validation
        while (true) {
            try {
                System.out.print("Enter the number of players (2-4): ");
                numberOfPlayers = Integer.parseInt(scanner.nextLine());

                if (numberOfPlayers >= 2 && numberOfPlayers <= 4) {
                    break; // Valid number of players, exit loop
                } else {
                    System.out.println("Invalid number of players. Please enter a number between 2 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer between 2 and 4.");
            }
        }

        // Initialize the GameController with the specified number of players
        System.out.println("Setting up the game for " + numberOfPlayers + " players...");
        try {
            GameController gameController = new GameController(numberOfPlayers);

            // Confirm setup and start the game
            System.out.println("Game setup complete. Let's play!");
            gameController.startGame();

        } catch (Exception e) {
            System.err.println("An error occurred while starting the game: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Thank you for playing!");
            scanner.close();
        }
    }
}
