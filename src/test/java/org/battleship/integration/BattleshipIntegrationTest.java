package org.battleship.integration;

import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;
import org.battleship.service.BattleFieldGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the full Battleship game flow.
 */
public class BattleshipIntegrationTest {

    private BattleFieldGameService service;
    private List<GameEvent> capturedEvents;

    @BeforeEach
    void setup() {
        service = new BattleFieldGameService();
        capturedEvents = new ArrayList<>();

        // Must initialize engine first
        service.initGame(6);

        // Add listener to capture events
        service.addListener(new GameEventListener() {
            @Override
            public void onEvent(GameEvent event) {
                capturedEvents.add(event);
                System.out.println("[TEST EVENT] " + event.getByPlayerId() +
                        " -> " + event.getType() + " at " + event.getPosition() +
                        (event.getDestroyedShipId() != null ? " destroyed:" + event.getDestroyedShipId() : ""));
            }
        });
    }

    @Test
    void testFullGameFlow() throws Exception {
        // Place ships for both players
        assertDoesNotThrow(() -> {
            service.addShip("SH1", 2, 1, 4, 4, 4);
            service.addShip("SH2", 1, 0, 0, 5, 0);
        });

        // View battlefield
        service.viewBattleField();

        // Start game loop
        service.startGame();

        // Assert that events were generated
        assertFalse(capturedEvents.isEmpty(), "No events were captured");

        // Check at least one HIT and one MISS
        assertTrue(
                capturedEvents.stream().anyMatch(e -> e.getType() == GameEvent.Type.HIT),
                "Expected at least one HIT event"
        );
        assertTrue(
                capturedEvents.stream().anyMatch(e -> e.getType() == GameEvent.Type.MISS),
                "Expected at least one MISS event"
        );

        // Ensure destroyed ship was reported at some point
        assertTrue(
                capturedEvents.stream().anyMatch(e -> e.getDestroyedShipId() != null),
                "Expected at least one ship to be destroyed"
        );
    }
}
