package test.resources;

import catan.resources.Resource;
import org.junit.Test;
import static org.junit.Assert.*;

public class ResourceTest {

    @Test
    public void testEnumValuesExist() {
        // Verify each enum value exists
        assertNotNull("WOOD should exist", Resource.valueOf("WOOD"));
        assertNotNull("BRICK should exist", Resource.valueOf("BRICK"));
        assertNotNull("SHEEP should exist", Resource.valueOf("SHEEP"));
        assertNotNull("WHEAT should exist", Resource.valueOf("WHEAT"));
        assertNotNull("ORE should exist", Resource.valueOf("ORE"));
        assertNotNull("DESERT should exist", Resource.valueOf("DESERT"));
    }

    @Test
    public void testEnumValueCount() {
        // Ensure the enum has exactly 6 values
        assertEquals("There should be 6 resource types", 6, Resource.values().length);
    }

    @Test
    public void testEnumContainsAllTypes() {
        // Check each expected value is in the enum
        Resource[] expectedResources = {
            Resource.WOOD,
            Resource.BRICK,
            Resource.SHEEP,
            Resource.WHEAT,
            Resource.ORE,
            Resource.DESERT
        };
        
        for (Resource resource : expectedResources) {
            assertTrue("Resource should contain " + resource, containsResource(resource));
        }
    }

    private boolean containsResource(Resource resource) {
        for (Resource res : Resource.values()) {
            if (res == resource) return true;
        }
        return false;
    }
}
