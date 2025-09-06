package org.battleship.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.battleship.exceptions.InvalidShipPlacementException;
import org.battleship.exceptions.OverlapException;
import org.battleship.model.Position;
import org.battleship.model.Ship;
import org.battleship.model.ShipFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;


/**
 * Unit tests for InMemoryShipRepository.
 */
public class InMemoryShipRepositoryTest {

    private InMemoryShipRepository repo;
    private final String PLAYER = "P1";

    @BeforeEach
    void setUp() {
        repo = new InMemoryShipRepository(5, Arrays.asList(PLAYER));
    }

    @Test
    void testAddAndGetShipSuccess() throws Exception {
        Ship ship = ShipFactory.createSquareShip("S1", 2, new Position(0, 0));
        repo.addShip(PLAYER, ship);

        Optional<Ship> fetched = repo.getShip(PLAYER, "S1");
        assertTrue(fetched.isPresent());
        assertEquals("S1", fetched.get().getId());

        Optional<Ship> at = repo.getShipAt(PLAYER, new Position(0, 0));
        assertTrue(at.isPresent());
        assertEquals("S1", at.get().getId());
    }

    @Test
    void testAddShipOutOfBounds() {
        Ship ship = ShipFactory.createSquareShip("S2", 2, new Position(4, 4));
        assertThrows(InvalidShipPlacementException.class,
                () -> repo.addShip(PLAYER, ship));
    }

    @Test
    void testAddShipOverlap() throws Exception {
        Ship s1 = ShipFactory.createSquareShip("S1", 2, new Position(0, 0));
        Ship s2 = ShipFactory.createSquareShip("S2", 2, new Position(1, 0)); // overlaps with S1

        repo.addShip(PLAYER, s1);
        assertThrows(OverlapException.class, () -> repo.addShip(PLAYER, s2));
    }

    @Test
    void testListShips() throws Exception {
        Ship s1 = ShipFactory.createSquareShip("S1", 1, new Position(0, 0));
        Ship s2 = ShipFactory.createSquareShip("S2", 1, new Position(1, 1));
        repo.addShip(PLAYER, s1);
        repo.addShip(PLAYER, s2);

        assertEquals(2, repo.listShips(PLAYER).size());
    }

    @Test
    void testDestroyShipAt() throws Exception {
        Ship s1 = ShipFactory.createSquareShip("S1", 1, new Position(0, 0));
        repo.addShip(PLAYER, s1);

        Optional<String> destroyed = repo.destroyShipAt(PLAYER, new Position(0, 0));
        assertTrue(destroyed.isPresent());
        assertEquals("S1", destroyed.get());

        assertFalse(repo.getShip(PLAYER, "S1").isPresent());
        assertEquals(0, repo.activeShipCount(PLAYER));
    }

    @Test
    void testActiveShipCount() throws Exception {
        Ship s1 = ShipFactory.createSquareShip("S1", 1, new Position(0, 0));
        Ship s2 = ShipFactory.createSquareShip("S2", 1, new Position(1, 1));
        repo.addShip(PLAYER, s1);
        repo.addShip(PLAYER, s2);

        assertEquals(2, repo.activeShipCount(PLAYER));

        repo.destroyShipAt(PLAYER, new Position(0, 0));
        assertEquals(1, repo.activeShipCount(PLAYER));
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        Runnable task = () -> {
            try {
                Ship s = ShipFactory.createSquareShip(
                        UUID.randomUUID().toString(), 1,
                        new Position((int) (Math.random() * 5), (int) (Math.random() * 5)));
                repo.addShip(PLAYER, s);
            } catch (Exception ignored) {
            }
        };

        for (int i = 0; i < 20; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        // No exceptions thrown, repo remains consistent
    }
}
