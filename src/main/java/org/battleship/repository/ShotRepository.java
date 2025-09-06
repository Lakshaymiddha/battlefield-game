package org.battleship.repository;


import org.battleship.model.Position;

/**
 * Tracks global fired positions to prevent duplicate fires.
 */
public interface ShotRepository {
    boolean alreadyFired(Position pos);

    void recordShot(Position pos);
}
