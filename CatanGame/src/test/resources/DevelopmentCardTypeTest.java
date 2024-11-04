package test.resources;

import catan.resources.DevelopmentCardType;
import org.junit.Test;
import static org.junit.Assert.*;

public class DevelopmentCardTypeTest {

    @Test
    public void testEnumValuesExist() {
        // Verify each enum value exists
        assertNotNull("KNIGHT should exist", DevelopmentCardType.valueOf("KNIGHT"));
        assertNotNull("VICTORY_POINT should exist", DevelopmentCardType.valueOf("VICTORY_POINT"));
        assertNotNull("ROAD_BUILDING should exist", DevelopmentCardType.valueOf("ROAD_BUILDING"));
        assertNotNull("YEAR_OF_PLENTY should exist", DevelopmentCardType.valueOf("YEAR_OF_PLENTY"));
        assertNotNull("MONOPOLY should exist", DevelopmentCardType.valueOf("MONOPOLY"));
    }

    @Test
    public void testEnumValueCount() {
        // Ensure the enum has exactly 5 values
        assertEquals("There should be 5 development card types", 5, DevelopmentCardType.values().length);
    }

    @Test
    public void testEnumContainsAllTypes() {
        // Check each expected value is in the enum
        DevelopmentCardType[] expectedTypes = {
            DevelopmentCardType.KNIGHT,
            DevelopmentCardType.VICTORY_POINT,
            DevelopmentCardType.ROAD_BUILDING,
            DevelopmentCardType.YEAR_OF_PLENTY,
            DevelopmentCardType.MONOPOLY
        };
        
        for (DevelopmentCardType type : expectedTypes) {
            assertTrue("DevelopmentCardType should contain " + type, containsType(type));
        }
    }

    private boolean containsType(DevelopmentCardType type) {
        for (DevelopmentCardType cardType : DevelopmentCardType.values()) {
            if (cardType == type) return true;
        }
        return false;
    }
}
