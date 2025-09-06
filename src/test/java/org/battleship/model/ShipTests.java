package org.battleship.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShipTests {
    @Test
    void buildValidShip() {
        Ship s = new Ship.Builder().id("S1").size(2).topLeft(new Position(1,1)).build();
        assertEquals("S1", s.getId());
        assertEquals(4, s.getOccupiedPositions().size());
    }

    @Test
    void buildInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> new Ship.Builder().id("S2").size(0).topLeft(new Position(0,0)).build());
    }

    @Test
    void factoryCreatesShip() {
        Ship s = ShipFactory.createSquareShip("SF", 1, new Position(0,0));
        assertEquals("SF", s.getId());
    }
}
