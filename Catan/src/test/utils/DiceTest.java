package test.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import utils.Dice;

public class DiceTest {
    private Dice dice;

    @Before
    public void setUp() {
        dice = new Dice();
    }

    @Test
    public void testDiceRollWithinRange() {
        for (int i = 0; i < 1000; i++) { // Test 1000 rolls to ensure all are valid
            int roll = dice.roll();
            assertTrue("Dice roll should be between 2 and 12", roll >= 2 && roll <= 12);
        }
    }

    @Test
    public void testDiceRandomness() {
        int[] rollCounts = new int[13]; // Track the frequency of each possible roll (2-12)

        for (int i = 0; i < 10000; i++) { // Simulate 10,000 rolls
            int roll = dice.roll();
            rollCounts[roll]++;
        }

        // Ensure each roll occurs at least a few times (no number is entirely missing)
        for (int i = 2; i <= 12; i++) {
            assertTrue("Each roll (2-12) should occur at least 10 times in 10,000 rolls", rollCounts[i] > 10);
        }
    }
}
