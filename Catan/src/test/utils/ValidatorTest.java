package test.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import board.Edge;
import board.Intersection;
import components.Settlement;
import players.Player;
import utils.Validator;

public class ValidatorTest {

    @Test
    public void testValidSettlementPlacement() {
        Intersection intersection = new Intersection(1, 2);
        Settlement settlement = null; // No settlement at this intersection

        assertTrue("Settlement placement should be valid", Validator.isValidSettlementPlacement(intersection, settlement));
    }

    @Test
    public void testInvalidSettlementPlacement() {
        Intersection intersection = new Intersection(1, 2);
        Settlement settlement = new Settlement(new Player("Alice"), intersection); // Already occupied

        assertFalse("Settlement placement should be invalid", Validator.isValidSettlementPlacement(intersection, settlement));
    }

    @Test
    public void testValidRoadPlacement() {
        Edge edge = new Edge(new Intersection(1, 1), new Intersection(1, 2));
        Player player = new Player("Bob");

        assertTrue("Road placement should be valid", Validator.isValidRoadPlacement(edge, player));
    }

    @Test
    public void testInvalidRoadPlacement() {
        Edge edge = null; // Invalid edge
        Player player = new Player("Bob");

        assertFalse("Road placement should be invalid", Validator.isValidRoadPlacement(edge, player));
    }

    @Test
    public void testValidTrade() {
        int offerAmount = 4; // Player offers 4 wood
        int requestAmount = 1; // Player requests 1 ore

        assertTrue("Trade should be valid", Validator.isValidTrade(offerAmount, requestAmount));
    }

    @Test
    public void testInvalidTrade() {
        int offerAmount = 0; // Invalid trade: no resources offered
        int requestAmount = 1;

        assertFalse("Trade should be invalid", Validator.isValidTrade(offerAmount, requestAmount));
    }
}
