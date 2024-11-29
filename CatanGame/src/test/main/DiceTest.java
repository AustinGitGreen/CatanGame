package test.main;

import static org.junit.Assert.*;

import catan.main.Dice;
import org.junit.Test;

public class DiceTest {

    @Test
    public void testDiceRollRange() {
        // Roll the dice multiple times to ensure it always falls within the valid range
        for (int i = 0; i < 100; i++) { // Test multiple rolls for reliability
            int roll = Dice.roll();
            assertTrue("Dice roll should be between 2 and 12", roll >= 2 && roll <= 12);
        }
    }

    @Test
    public void testDiceRollDistribution() {
        // Roll the dice many times to test that the distribution is plausible
        int[] rollCounts = new int[13]; // Index 2 to 12

        for (int i = 0; i < 10000; i++) {
            int roll = Dice.roll();
            rollCounts[roll]++;
        }

        // Ensure that each possible dice roll occurred at least once
        for (int i = 2; i <= 12; i++) {
            assertTrue("Each possible dice roll should occur at least once in a large sample", rollCounts[i] > 0);
        }

        // Optional: Print distribution (useful for debugging)
        // for (int i = 2; i <= 12; i++) {
        //     System.out.println("Roll " + i + ": " + rollCounts[i]);
        // }
    }
}
