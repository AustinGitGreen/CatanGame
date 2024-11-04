package test.resources;

import catan.resources.DevelopmentCard;
import catan.resources.DevelopmentCardType;
import org.junit.Test;

import static org.junit.Assert.*;

public class DevelopmentCardTest {

    @Test
    public void testDevelopmentCardInitialization() {
        // Test initialization with KNIGHT type
        DevelopmentCard knightCard = new DevelopmentCard(DevelopmentCardType.KNIGHT);
        assertEquals("The card type should be KNIGHT", DevelopmentCardType.KNIGHT, knightCard.getType());

        // Test initialization with VICTORY_POINT type
        DevelopmentCard victoryCard = new DevelopmentCard(DevelopmentCardType.VICTORY_POINT);
        assertEquals("The card type should be VICTORY_POINT", DevelopmentCardType.VICTORY_POINT, victoryCard.getType());

        // Test initialization with ROAD_BUILDING type
        DevelopmentCard roadCard = new DevelopmentCard(DevelopmentCardType.ROAD_BUILDING);
        assertEquals("The card type should be ROAD_BUILDING", DevelopmentCardType.ROAD_BUILDING, roadCard.getType());

        // Test initialization with YEAR_OF_PLENTY type
        DevelopmentCard plentyCard = new DevelopmentCard(DevelopmentCardType.YEAR_OF_PLENTY);
        assertEquals("The card type should be YEAR_OF_PLENTY", DevelopmentCardType.YEAR_OF_PLENTY, plentyCard.getType());

        // Test initialization with MONOPOLY type
        DevelopmentCard monopolyCard = new DevelopmentCard(DevelopmentCardType.MONOPOLY);
        assertEquals("The card type should be MONOPOLY", DevelopmentCardType.MONOPOLY, monopolyCard.getType());
    }
}
