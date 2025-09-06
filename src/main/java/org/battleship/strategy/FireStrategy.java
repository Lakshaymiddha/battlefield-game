package org.battleship.strategy;

import org.battleship.model.Position;
import org.battleship.model.Player;

/**
 * Strategy interface to return next firing position given attacker & opponent.
 */
public interface FireStrategy {
    /**
     * Return next Position to fire at (or null if no more). Must avoid duplicates in strategy's own pool.
     */
    Position next(Player attacker, Player opponent);
}

