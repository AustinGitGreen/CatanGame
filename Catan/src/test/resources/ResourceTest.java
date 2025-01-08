package test.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import catan.resources.Resource;

public class ResourceTest {

    @Test
    public void testResourceValues() {
        // Ensure all resource types are present
        Resource[] resources = Resource.values();
        assertEquals("There should be 6 resource types", 6, resources.length);

        // Check for specific resource types
        assertTrue("Resource enum should include WOOD", contains(resources, Resource.WOOD));
        assertTrue("Resource enum should include BRICK", contains(resources, Resource.BRICK));
        assertTrue("Resource enum should include WHEAT", contains(resources, Resource.WHEAT));
        assertTrue("Resource enum should include ORE", contains(resources, Resource.ORE));
        assertTrue("Resource enum should include SHEEP", contains(resources, Resource.SHEEP));
        assertTrue("Resource enum should include DESERT", contains(resources, Resource.DESERT));
    }

    @Test
    public void testResourceName() {
        // Check the name of a specific resource
        assertEquals("Resource name should be WOOD", "WOOD", Resource.WOOD.name());
        assertEquals("Resource name should be BRICK", "BRICK", Resource.BRICK.name());
        assertEquals("Resource name should be WHEAT", "WHEAT", Resource.WHEAT.name());
        assertEquals("Resource name should be ORE", "ORE", Resource.ORE.name());
        assertEquals("Resource name should be SHEEP", "SHEEP", Resource.SHEEP.name());
        assertEquals("Resource name should be DESERT", "DESERT", Resource.DESERT.name());
    }

    @Test
    public void testResourceOrdinal() {
        // Verify the ordinal values (position in enum declaration)
        assertEquals("WOOD should have ordinal 0", 0, Resource.WOOD.ordinal());
        assertEquals("BRICK should have ordinal 1", 1, Resource.BRICK.ordinal());
        assertEquals("WHEAT should have ordinal 2", 2, Resource.WHEAT.ordinal());
        assertEquals("ORE should have ordinal 3", 3, Resource.ORE.ordinal());
        assertEquals("SHEEP should have ordinal 4", 4, Resource.SHEEP.ordinal());
        assertEquals("DESERT should have ordinal 5", 5, Resource.DESERT.ordinal());
    }

    // Helper method to check if a specific resource exists in the array
    private boolean contains(Resource[] resources, Resource target) {
        for (Resource resource : resources) {
            if (resource == target) {
                return true;
            }
        }
        return false;
    }
}
