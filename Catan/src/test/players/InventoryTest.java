package test.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import catan.players.Inventory;
import resources.Resource;

import java.util.HashMap;
import java.util.Map;

public class InventoryTest {
    private Inventory inventory;

    @Before
    public void setUp() {
        inventory = new Inventory();
    }

    @Test
    public void testInitialResourceCount() {
        for (Resource resource : Resource.values()) {
            assertEquals("Initial resource count should be 0", 0, inventory.getResourceCount(resource));
        }
    }

    @Test
    public void testAddResource() {
        inventory.addResource(Resource.WOOD, 5);
        assertEquals("WOOD count should be 5", 5, inventory.getResourceCount(Resource.WOOD));
    }

    @Test
    public void testRemoveResource() {
        inventory.addResource(Resource.BRICK, 5);
        inventory.removeResource(Resource.BRICK, 3);
        assertEquals("BRICK count should be 2", 2, inventory.getResourceCount(Resource.BRICK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveResourceThrowsException() {
        inventory.addResource(Resource.WHEAT, 1);
        inventory.removeResource(Resource.WHEAT, 2); // Should throw exception
    }

    @Test
    public void testHasEnoughResources() {
        inventory.addResource(Resource.WOOD, 4);
        inventory.addResource(Resource.BRICK, 3);

        Map<Resource, Integer> requiredResources = new HashMap<>();
        requiredResources.put(Resource.WOOD, 3);
        requiredResources.put(Resource.BRICK, 2);

        assertTrue("Should have enough resources", inventory.hasEnoughResources(requiredResources));

        requiredResources.put(Resource.BRICK, 4);
        assertFalse("Should not have enough resources", inventory.hasEnoughResources(requiredResources));
    }
}
