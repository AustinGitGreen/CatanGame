package test.components;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import catan.components.DevelopmentCard;

public class DevelopmentCardTest {
    private DevelopmentCard knightCard;
    private DevelopmentCard victoryPointCard;

    @Before
    public void setUp() {
        knightCard = new DevelopmentCard(DevelopmentCard.CardType.KNIGHT);
        victoryPointCard = new DevelopmentCard(DevelopmentCard.CardType.VICTORY_POINT);
    }

    @Test
    public void testDevelopmentCardType() {
        assertEquals("Card type should be KNIGHT", DevelopmentCard.CardType.KNIGHT, knightCard.getType());
        assertEquals("Card type should be VICTORY_POINT", DevelopmentCard.CardType.VICTORY_POINT, victoryPointCard.getType());
    }
}
