package org.battleship.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ShipFactoryTest {
    @Test
    void testOccupiedPositionsForSquareShip() {
        String shipId = "SH1";
        int size = 4;

        // Create a ship with top-left (0,0)
        Ship ship = new Ship.Builder()
                .id(shipId)
                .size(size)
                .topLeft(new Position(0, 0))
                .build();

        Set<Position> expected = Set.of(
                new Position(0, 0), new Position(0, 1), new Position(0, 2), new Position(0, 3),
                new Position(1, 0), new Position(1, 1), new Position(1, 2), new Position(1, 3),
                new Position(2, 0), new Position(2, 1), new Position(2, 2), new Position(2, 3),
                new Position(3, 0), new Position(3, 1), new Position(3, 2), new Position(3, 3)
        );

        Set<Position> actual = ship.getOccupiedPositions().stream().collect(Collectors.toSet());

        assertEquals(expected, actual, "Occupied positions should match expected 4x4 square");
    }

    @Test
    void testOccupiesMethod() {
        Ship ship = new Ship.Builder()
                .id("SH2")
                .size(3)
                .topLeft(new Position(1, 1))
                .build();

        // Positions that should be occupied
        assertTrue(ship.occupies(new Position(1, 1)));
        assertTrue(ship.occupies(new Position(2, 2)));
        assertTrue(ship.occupies(new Position(3, 3)));

        // Positions outside the ship
        assertFalse(ship.occupies(new Position(0, 0)));
        assertFalse(ship.occupies(new Position(4, 4)));
    }

    @Test
    void testDestroyFlag() {
        Ship ship = new Ship.Builder()
                .id("SH3")
                .size(2)
                .topLeft(new Position(0, 0))
                .build();

        assertFalse(ship.isDestroyed(), "New ship should not be destroyed");
        ship.destroy();
        assertTrue(ship.isDestroyed(), "Ship should be destroyed after calling destroy()");
    }

    @Test
    void testInvalidSizeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ship.Builder()
                    .id("Invalid")
                    .size(0)
                    .topLeft(new Position(0, 0))
                    .build();
        });
    }

}