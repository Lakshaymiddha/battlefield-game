package org.battleship.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testValidPlayerConstruction() {
        Player player = new Player.Builder()
                .id("p1")
                .name("John")
                .build();

        assertEquals("p1", player.getId());
        assertEquals("John", player.getName());
    }

    @Test
    void testInvalidPlayerConstruction() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Player.Builder().name("John").build();
        });
    }

    @Test
    void testToString() {
        Player player = new Player.Builder()
                .id("p1")
                .name("John")
                .build();

        assertEquals("John(p1)", player.toString());
    }
}
