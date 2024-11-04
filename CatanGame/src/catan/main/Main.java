package catan.main;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user for the number of players
        System.out.println("Welcome to Catan!");
        System.out.print("Enter the number of players (2-4): ");
        int numberOfPlayers = scanner.nextInt();
        
        // Validate the number of players
        while (numberOfPlayers < 2 || numberOfPlayers > 4) {
            System.out.print("Invalid number of players. Please enter a number between 2 and 4: ");
            numberOfPlayers = scanner.nextInt();
        }

        // Initialize the GameController with the specified number of players
        GameController gameController = new GameController(numberOfPlayers);
        
        // Start the game
        gameController.startGame();

        System.out.println("Thank you for playing!");
        scanner.close();
    }
}
