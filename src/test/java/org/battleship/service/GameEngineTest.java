package org.battleship.service;

import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;
import org.battleship.exceptions.DuplicateShotException;
import org.battleship.model.Position;
import org.battleship.model.Ship;
import org.battleship.model.ShipFactory;
import org.battleship.repository.*;
import org.battleship.strategy.FireStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameEngine
 */
public class GameEngineTest {

    private GameEngine engine;
    private ShipRepository shipRepo;
    private PlayerRepository playerRepo;
    private ShotRepository shotRepo;

    private final String ATTACKER = "P1";
    private final String DEFENDER = "P2";

    @BeforeEach
    void setUp() {
        shipRepo = new InMemoryShipRepository(5, Arrays.asList(ATTACKER, DEFENDER));
        playerRepo = new InMemoryPlayerRepository();
        shotRepo = new InMemoryShotRepository();

        engine = new GameEngine(5, shipRepo, playerRepo, shotRepo);
    }

    @Test
    void testSetAndGetStrategy() {
        FireStrategy strategy = (a, d) -> new Position(0, 0);
        engine.setStrategies(ATTACKER, strategy);
        assertEquals(strategy, engine.getStrategyForPlayer(ATTACKER));
    }

    @Test
    void testFireOnceMiss() throws Exception {
        // Fire at empty board
        FireStrategy strategy = (a, d) -> new Position(1, 1);

        GameEvent event = engine.fireOnce(ATTACKER, DEFENDER, strategy);

        assertEquals(GameEvent.Type.MISS, event.getType());
        assertEquals(ATTACKER, event.getByPlayerId());
        assertEquals(new Position(1, 1), event.getPosition());
    }

    @Test
    void testFireOnceHitAndDestroy() throws Exception {
        // Place ship for defender
        Ship ship = ShipFactory.createSquareShip("S1", 1, new Position(2, 2));
        shipRepo.addShip(DEFENDER, ship);

        FireStrategy strategy = (a, d) -> new Position(2, 2);

        AtomicReference<GameEvent> received = new AtomicReference<>();
        engine.addListener(received::set);

        GameEvent event = engine.fireOnce(ATTACKER, DEFENDER, strategy);

        assertEquals(GameEvent.Type.HIT, event.getType());
        assertEquals("S1", event.getDestroyedShipId());
        assertEquals(0, shipRepo.activeShipCount(DEFENDER));
        assertNotNull(received.get()); // listener notified
    }

    @Test
    void testDuplicateShotThrowsException() throws Exception {
        Position pos = new Position(0, 0);

        FireStrategy strategy = (a, d) -> pos;

        // First shot
        engine.fireOnce(ATTACKER, DEFENDER, strategy);

        // Duplicate
        assertThrows(DuplicateShotException.class,
                () -> engine.fireOnce(ATTACKER, DEFENDER, strategy));
    }

    @Test
    void testRemoveListener() throws Exception {
        AtomicBoolean notified = new AtomicBoolean(false);
        GameEventListener listener = e -> notified.set(true);

        engine.addListener(listener);
        engine.removeListener(listener);

        FireStrategy strategy = (a, d) -> new Position(1, 1);
        engine.fireOnce(ATTACKER, DEFENDER, strategy);

        assertFalse(notified.get(), "Listener should not be notified after removal");
    }

    @Test
    void testActiveShipCount() throws Exception {
        assertEquals(0, engine.activeShipCount(DEFENDER));

        Ship ship = ShipFactory.createSquareShip("S1", 1, new Position(3, 3));
        shipRepo.addShip(DEFENDER, ship);

        assertEquals(1, engine.activeShipCount(DEFENDER));
    }

    @Test
    void testGetBoardSize() {
        assertEquals(5, engine.getBoardSize());
    }
}
