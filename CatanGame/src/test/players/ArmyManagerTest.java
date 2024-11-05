package test.players;

import catan.board.HexTile;
import catan.board.Robber;
import catan.players.ArmyManager;
import catan.players.Player;
import catan.resources.Resource;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArmyManagerTest {
    private Player player1;
    private Player player2;
    private Robber robber;
    private ArmyManager armyManager;
    private HexTile desertTile;
    private HexTile targetTile;

    @Before
    public void setUp() {
        // Initialize players, the army manager, the robber, and tiles
        player1 = new Player();
        player2 = new Player();
        desertTile = new HexTile(Resource.DESERT, 0); // Initial robber location
        targetTile = new HexTile(Resource.WOOD, 5); // Target tile for robber movement
        robber = new Robber(desertTile);
        armyManager = new ArmyManager();
    }

    @Test
    public void testPlayKnightIncrementsKnightCount() {
        // Player1 plays a knight
        armyManager.playKnight(player1, robber, targetTile);

        // Check that player1's knight count has incremented
        assertEquals("Player1 should have 1 knight after playing a knight card", 1, player1.getKnights());
    }

    @Test
    public void testLargestArmyHolderUpdated() {
        // Player1 plays enough knights to take the largest army
        armyManager.playKnight(player1, robber, targetTile); // 1st knight
        armyManager.playKnight(player1, robber, desertTile); // 2nd knight
        armyManager.playKnight(player1, robber, targetTile); // 3rd knight

        // Verify that player1 is now the largest army holder
        assertEquals("Player1 should hold the largest army", player1, armyManager.getLargestArmyHolder());

        // Player2 plays more knights to take over the largest army
        armyManager.playKnight(player2, robber, targetTile); // 1st knight
        armyManager.playKnight(player2, robber, desertTile); // 2nd knight
        armyManager.playKnight(player2, robber, targetTile); // 3rd knight
        armyManager.playKnight(player2, robber, desertTile); // 4th knight

        // Verify that player2 now holds the largest army
        assertEquals("Player2 should hold the largest army", player2, armyManager.getLargestArmyHolder());
    }

    @Test
    public void testRobberMovesWhenKnightPlayed() {
        // Verify initial position of the robber
        assertEquals("Robber should start on the desert tile", desertTile, robber.getCurrentTile());

        // Player1 plays a knight and moves the robber
        armyManager.playKnight(player1, robber, targetTile);

        // Verify that the robber has moved to the target tile
        assertEquals("Robber should be on the target tile after moving", targetTile, robber.getCurrentTile());
    }
}
