package org.battleship.repository;

import org.battleship.model.Position;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe global shot tracking.
 */
public class InMemoryShotRepository implements ShotRepository {
    private final Set<Position> fired = ConcurrentHashMap.newKeySet();

    @Override
    public boolean alreadyFired(Position pos) {
        return fired.contains(pos);
    }

    @Override
    public void recordShot(Position pos) {
        fired.add(pos);
    }
}

