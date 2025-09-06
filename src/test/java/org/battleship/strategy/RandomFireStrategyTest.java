package org.battleship.strategy;

import org.battleship.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class RandomFireStrategyTest {
    private RandomFireStrategy strategy;
    private static final int BOARD_SIZE = 10;
    private static final String ATTACKER = "player1";
    private static final String DEFENDER = "player2";

    @BeforeEach
    void setUp() {
        strategy = new RandomFireStrategy(0, 5, BOARD_SIZE);
    }

    @Test
    void testNextPositionWithinBounds() {
        Position pos = strategy.next(ATTACKER, DEFENDER);
        assertNotNull(pos);
        assertTrue(pos.getX() >= 0 && pos.getX() <= 5);
        assertTrue(pos.getY() >= 0 && pos.getY() < BOARD_SIZE);
    }
}
