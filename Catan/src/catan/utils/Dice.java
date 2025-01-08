package catan.utils;

import java.util.Random;

/**
 * Dice class to simulate rolling two 6-sided dice.
 */
public class Dice {
    private Random random;

    public Dice() {
        random = new Random();
    }

    /**
     * Rolls two dice and returns the sum.
     * @return The sum of the two dice rolls, between 2 and 12.
     */
    public int roll() {
        int die1 = random.nextInt(6) + 1; // Roll 1-6
        int die2 = random.nextInt(6) + 1; // Roll 1-6
        return die1 + die2; // Sum the rolls (2-12)
    }
}
