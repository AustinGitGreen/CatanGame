package test.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import resources.Resource;
import resources.ResourcePool;

public class ResourcePoolTest {
    private ResourcePool resourcePool;

    @Before
    public void setUp() {
        resourcePool = new ResourcePool();
    }

    @Test
    public void testInitialResourceCount() {
        // Verify that all resources are initialized to 0
        for (Resource resource : Resource.values()) {
            assertEquals("Initial resource count should be 0", 0, resourcePool.getResourceCount(resource));
        }
    }

    @Test
    public void testAddResource() {
        resourcePool.addResource(Resource.WOOD, 5);
        assertEquals("Resource count for WOOD should be 5", 5, resourcePool.getResourceCount(Resource.WOOD));

        resourcePool.addResource(Resource.WOOD, 3);
        assertEquals("Resource count for WOOD should now be 8", 8, resourcePool.getResourceCount(Resource.WOOD));
    }

    @Test
    public void testRemoveResource() {
        resourcePool.addResource(Resource.BRICK, 5);
        resourcePool.removeResource(Resource.BRICK, 3);
        assertEquals("Resource count for BRICK should be 2", 2, resourcePool.getResourceCount(Resource.BRICK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveResourceThrowsException() {
        resourcePool.addResource(Resource.WHEAT, 2);
        resourcePool.removeResource(Resource.WHEAT, 3); // Should throw an exception
    }

    @Test
    public void testHasEnoughResource() {
        resourcePool.addResource(Resource.ORE, 10);
        assertTrue("Should have enough ORE", resourcePool.hasEnoughResource(Resource.ORE, 5));
        assertFalse("Should not have enough ORE", resourcePool.hasEnoughResource(Resource.ORE, 15));
    }
}
