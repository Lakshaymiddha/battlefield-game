package org.battleship.strategy;

import org.battleship.model.Position;

/**
 * Strategy interface. Given attacker & defender info, return next target (or null if none).
 */
public interface MissileFireStrategy {
    /**
     * Return next Position to fire at (or null if no more). Must avoid duplicates in strategy's own pool.
     */
    Position next(String attackerId, String defenderId);
}


