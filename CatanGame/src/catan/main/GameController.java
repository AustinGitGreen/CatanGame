package catan.main;

import java.util.Scanner;

public class GameController {
    private Game game;

    public GameController(int numberOfPlayers) {
        // Initialize Game with the specified number of players
        this.game = new Game(numberOfPlayers);
    }

    public void startGame() {
        System.out.println("Starting the game...");
        
        while (true) {
            game.startTurn();  // Begin the current player's turn

            // Placeholder for player actions: building, trading, playing cards
            handlePlayerActions();

            game.endTurn();  // End the current player's turn
            
            // Check for a winner after each turn
            if (game.checkVictory()) {
                System.out.println("Game over! We have a winner.");
                break;
            }
        }
    }

    private void handlePlayerActions() {
        // Placeholder for handling player actions (build, trade, etc.)
        // In a fully interactive game, you could add commands for each action.
        // Example:
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter action (e.g., 'build', 'trade', 'end turn'): ");
        String action = scanner.nextLine();
        
        // Based on action, call respective methods in Game or Player
        // Here, you could implement specific actions for building, trading, etc.
    }
}
