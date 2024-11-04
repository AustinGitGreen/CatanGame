package test.board;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import catan.board.Board;
import catan.board.HexTile;
import catan.board.Port;
import catan.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board(); // Initialize Board

        // Manually set up ports, simulating GameSetup behavior
        List<Port> ports = new ArrayList<>();
        ports.add(new Port(2, Resource.WOOD));  // Wood-specific port
        ports.add(new Port(2, Resource.BRICK)); // Brick-specific port
        ports.add(new Port(2, Resource.SHEEP)); // Sheep-specific port
        ports.add(new Port(2, Resource.WHEAT)); // Wheat-specific port
        ports.add(new Port(2, Resource.ORE));   // Ore-specific port
        ports.add(new Port(3, null));           // Generic port (3:1)
        ports.add(new Port(3, null));           // Generic port (3:1)
        ports.add(new Port(3, null));           // Generic port (3:1)
        ports.add(new Port(3, null));           // Generic port (3:1)

        board.setPorts(ports); // Assign this list to the board
    }

    @Test
    public void testHexTileInitialization() {
        List<HexTile> hexTiles = board.getHexTiles();
        
        // Check the number of hex tiles
        assertNotNull("Hex tiles should not be null", hexTiles);
        assertEquals("The board should have 19 hex tiles", 19, hexTiles.size());

        // Check that each resource type has the correct number of tiles
        long woodCount = hexTiles.stream().filter(tile -> tile.getResourceType() == Resource.WOOD).count();
        long brickCount = hexTiles.stream().filter(tile -> tile.getResourceType() == Resource.BRICK).count();
        long sheepCount = hexTiles.stream().filter(tile -> tile.getResourceType() == Resource.SHEEP).count();
        long wheatCount = hexTiles.stream().filter(tile -> tile.getResourceType() == Resource.WHEAT).count();
        long oreCount = hexTiles.stream().filter(tile -> tile.getResourceType() == Resource.ORE).count();
        long desertCount = hexTiles.stream().filter(HexTile::isDesert).count();

        assertEquals("The board should have 4 wood tiles", 4, woodCount);
        assertEquals("The board should have 3 brick tiles", 3, brickCount);
        assertEquals("The board should have 4 sheep tiles", 4, sheepCount);
        assertEquals("The board should have 4 wheat tiles", 4, wheatCount);
        assertEquals("The board should have 3 ore tiles", 3, oreCount);
        assertEquals("The board should have 1 desert tile", 1, desertCount);
    }


    @Test
    public void testDesertTile() {
        HexTile desertTile = board.getDesertTile();

        assertNotNull("Desert tile should not be null", desertTile);
        assertTrue("The identified desert tile should be marked as desert", desertTile.isDesert());
        assertEquals("The desert tile should have the resource type DESERT", Resource.DESERT, desertTile.getResourceType());
    }


    @Test
    public void testPortInitialization() {
        List<Port> ports = board.getPorts();

        assertNotNull("Ports should not be null", ports);
        assertEquals("The board should have 9 ports", 9, ports.size());

        // Check for expected types of ports (2:1 for each resource type and 3:1 generic ports)
        long woodPortCount = ports.stream().filter(port -> port.getResourceType() == Resource.WOOD && port.getTradeRatio() == 2).count();
        long brickPortCount = ports.stream().filter(port -> port.getResourceType() == Resource.BRICK && port.getTradeRatio() == 2).count();
        long sheepPortCount = ports.stream().filter(port -> port.getResourceType() == Resource.SHEEP && port.getTradeRatio() == 2).count();
        long wheatPortCount = ports.stream().filter(port -> port.getResourceType() == Resource.WHEAT && port.getTradeRatio() == 2).count();
        long orePortCount = ports.stream().filter(port -> port.getResourceType() == Resource.ORE && port.getTradeRatio() == 2).count();
        long genericPortCount = ports.stream().filter(port -> port.getResourceType() == null && port.getTradeRatio() == 3).count();

        assertEquals("The board should have 1 wood port with 2:1 trade ratio", 1, woodPortCount);
        assertEquals("The board should have 1 brick port with 2:1 trade ratio", 1, brickPortCount);
        assertEquals("The board should have 1 sheep port with 2:1 trade ratio", 1, sheepPortCount);
        assertEquals("The board should have 1 wheat port with 2:1 trade ratio", 1, wheatPortCount);
        assertEquals("The board should have 1 ore port with 2:1 trade ratio", 1, orePortCount);
        assertEquals("The board should have 4 generic ports with 3:1 trade ratio", 4, genericPortCount);
    }
}
