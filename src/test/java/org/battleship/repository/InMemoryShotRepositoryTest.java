package org.battleship.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.battleship.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class InMemoryShotRepositoryTest {

    private InMemoryShotRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryShotRepository();
    }

    @Test
    void testAlreadyFiredInitiallyFalse() {
        Position p = new Position(2, 3);
        assertFalse(repo.alreadyFired(p), "New position should not be marked as fired");
    }

    @Test
    void testRecordShotThenAlreadyFiredTrue() {
        Position p = new Position(1, 1);
        repo.recordShot(p);
        assertTrue(repo.alreadyFired(p), "Position should be marked as fired after recordShot");
    }

    @Test
    void testDuplicateShotDoesNotThrow() {
        Position p = new Position(0, 0);
        repo.recordShot(p);
        repo.recordShot(p); // calling again should be fine
        assertTrue(repo.alreadyFired(p), "Position should remain fired even if recorded twice");
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        Position p = new Position(5, 5);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                repo.recordShot(p);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertTrue(repo.alreadyFired(p), "Position should be fired even under concurrent updates");
    }
}
