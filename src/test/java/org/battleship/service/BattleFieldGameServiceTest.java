package org.battleship.service;

import org.battleship.exceptions.GameStateException;
import org.battleship.exceptions.InvalidShipPlacementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class BattleFieldGameServiceTest {
    private BattleFieldGameService service;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        service = new BattleFieldGameService();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }

    @Test
    void callingBeforeInitThrows() {
        assertThrows(GameStateException.class, () -> service.addShip("S1", 2, 0, 0, 3, 3));
        assertThrows(GameStateException.class, () -> service.viewBattleField());
        assertThrows(GameStateException.class, () -> service.startGame());
    }

    @Test
    public void testAddShipWithoutInitThrowsGameStateException() {
        assertThrows(GameStateException.class, () -> {
            service.addShip("S5", 2, 0, 0, 1, 1);
        });
    }

    @Test
    void initGameCreatesBoard() {
        service.initGame(6, "P1", "P2");
        assertTrue(outContent.toString().contains("Game initialized 6x6"));
    }

    @Test
    void addShipValidPlacement() throws Exception {
        service.initGame(6, "P1", "P2");
        service.addShip("S1", 2, 0, 0, 3, 0);
        assertTrue(outContent.toString().contains("Ship S1 added for both players"));
    }

    @Test
    void addShipInvalidTerritoryForPlayerA() throws GameStateException {
        service.initGame(6, "P1", "P2");
        // X=4 is outside PlayerA’s half (0..2)
        service.addShip("S1", 2, 4, 0, 3, 0);

        assertTrue(service.getShipRepo().getShip("P1", "S1").isEmpty(),
                "Player A should not have any ships when placement is invalid");
        assertTrue(service.getShipRepo().getShip("P2", "S1").isEmpty(),
                "Player B should not have any ships when placement is invalid");
    }

    @Test
    void addShipInvalidTerritoryForPlayerB() throws GameStateException {
        service.initGame(6, "P1", "P2");
        // X=1 is outside PlayerB’s half (3..5)
        service.addShip("S1", 2, 0, 0, 1, 0);

        assertTrue(service.getShipRepo().getShip("P1", "S1").isEmpty(),
                "Player A should not have any ships when placement is invalid");
        assertTrue(service.getShipRepo().getShip("P2", "S1").isEmpty(),
                "Player B should not have any ships when placement is invalid");
    }

    @Test
    void viewBattleFieldPrintsShips() throws Exception {
        service.initGame(6, "P1", "P2");
        service.addShip("S1", 2, 0, 0, 3, 0);
        service.viewBattleField();
        String output = outContent.toString();
        assertTrue(output.contains("A-S1"));
        assertTrue(output.contains("B-S1"));
    }

    @Test
    void startGameFailsWithNoShips() {
        service.initGame(6, "P1", "P2");
        assertThrows(GameStateException.class, () -> service.startGame());
    }

    @Test
    void startGameRunsUntilWinner() throws Exception {
        service.initGame(6, "P1", "P2");
        service.addShip("S1", 2, 0, 0, 3, 0);
        service.addShip("S2", 1, 2, 1, 4, 2);

        service.startGame();

        String output = outContent.toString();
        assertTrue(output.contains("GameOver"));
    }

    @Test
    void eventListenerReceivesCallbacks() throws Exception {
        service.initGame(6, "P1", "P2");
        service.addShip("S1", 2, 0, 0, 3, 0);
        service.addShip("S2", 1, 2, 1, 4, 2);

        final boolean[] called = {false};
        service.addListener((event) -> called[0] = true);

        service.startGame();
        assertTrue(called[0], "Listener should have been invoked");
    }

    @Test
    void testInitAndViewBattleField() {
        BattleFieldGameService service = new BattleFieldGameService();
        service.initGame(5, "P1", "P2");
        assertDoesNotThrow(() -> service.viewBattleField());
    }

    @Test
    void testEnsureInit_addShip_throwsGameStateException() {
        BattleFieldGameService service = new BattleFieldGameService();
        GameStateException ex = assertThrows(GameStateException.class,
                () -> service.addShip("SH1", 1, 0, 0, 1, 1));
        assertTrue(ex.getMessage().contains("Game not initialized"));
    }
}
