package org.battleship.event;

import org.battleship.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameEvent
 */
public class GameEventTest {

    @Test
    void testGameEventHit() {
        Position pos = new Position(2, 3);
        GameEvent event = new GameEvent(GameEvent.Type.HIT, "PlayerA", pos, "S1");

        assertEquals(GameEvent.Type.HIT, event.getType());
        assertEquals("PlayerA", event.getByPlayerId());
        assertEquals(pos, event.getPosition());
        assertEquals("S1", event.getDestroyedShipId());
    }

    @Test
    void testGameEventMiss() {
        Position pos = new Position(1, 1);
        GameEvent event = new GameEvent(GameEvent.Type.MISS, "PlayerB", pos, null);

        assertEquals(GameEvent.Type.MISS, event.getType());
        assertEquals("PlayerB", event.getByPlayerId());
        assertEquals(pos, event.getPosition());
        assertNull(event.getDestroyedShipId(), "Destroyed ship id should be null on MISS");
    }

    @Test
    void testDifferentEventsNotEqual() {
        Position pos = new Position(0, 0);

        GameEvent hit = new GameEvent(GameEvent.Type.HIT, "P1", pos, "S1");
        GameEvent miss = new GameEvent(GameEvent.Type.MISS, "P1", pos, null);

        // No equals() override, so only reference equality applies
        assertNotEquals(hit, miss);
        assertNotSame(hit, miss);
    }

    @Test
    void testImmutability() {
        Position pos = new Position(2, 2);
        GameEvent event = new GameEvent(GameEvent.Type.HIT, "PlayerX", pos, "S99");

        // Fields should remain unchanged
        assertEquals("PlayerX", event.getByPlayerId());
        assertEquals("S99", event.getDestroyedShipId());

        // Ensure new Position doesn't affect stored value
        Position newPos = new Position(9, 9);
        assertNotEquals(newPos, event.getPosition());
    }
}
