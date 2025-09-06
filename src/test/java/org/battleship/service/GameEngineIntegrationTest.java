package org.battleship.service;

import org.battleship.exceptions.GameException;
import org.battleship.exceptions.InvalidShipPlacementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineIntegrationTest {
    @Test
    void runFullGame() {
        GameEngine engine = new GameEngine();
        engine.initGame(6);
        try {
            engine.addShip("SH1", 2, 1, 4, 4, 4);
            engine.addShip("SH2", 1, 0, 0, 5, 0);
        } catch (GameException e) {
            fail("Ship placement failed: " + e.getMessage());
        }
        assertDoesNotThrow(() -> engine.viewBattleField());
        assertDoesNotThrow(() -> engine.startGame());
    }

    @Test
    void invalidPlacementThrows() {
        GameEngine engine = new GameEngine();
        engine.initGame(6);
        assertThrows(InvalidShipPlacementException.class, () -> engine.addShip("X", 2, 5, 5, 4, 4));
    }
}
