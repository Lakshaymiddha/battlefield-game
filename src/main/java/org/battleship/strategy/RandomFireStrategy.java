package org.battleship.strategy;


import org.battleship.model.Position;
import org.battleship.model.Player;

import java.util.*;

/**
 * Randomized precomputed pool for no-duplicate firing.
 */
public class RandomFireStrategy implements FireStrategy {
    private final Iterator<Position> poolIter;

    public RandomFireStrategy(int minCol, int maxCol, int boardSize) {
        List<Position> pool = new ArrayList<>();
        for (int x = minCol; x <= maxCol; x++) for (int y = 0; y < boardSize; y++) pool.add(new Position(x, y));
        Collections.shuffle(pool, new Random());
        poolIter = pool.iterator();
    }

    @Override
    public Position next(Player attacker, Player opponent) {
        if (!poolIter.hasNext()) return null;
        return poolIter.next();
    }
}

