package org.battleship.strategy;


import org.battleship.model.Position;

import java.util.*;

/**
 * Precomputes all coordinates in given column range and returns them shuffled.
 */
public class RandomFireStrategy implements MissileFireStrategy {
    private final Iterator<Position> iter;
    private final Set<Position> used = new HashSet<>();

    /**
     * @param minCol    inclusive
     * @param maxCol    inclusive
     * @param boardSize board size
     */
    public RandomFireStrategy(int minCol, int maxCol, int boardSize) {
        List<Position> pool = new ArrayList<>();
        for (int x = minCol; x <= maxCol; x++) {
            for (int y = 0; y < boardSize; y++) {
                pool.add(new Position(x, y));
            }
        }
        Collections.shuffle(pool, new Random());
        this.iter = pool.iterator();
    }

    @Override
    public Position next(String attackerId, String defenderId) {
        while (iter.hasNext()) {
            Position candidate = iter.next();
            if (used.add(candidate)) {
                // first time we see this -> valid
                return candidate;
            }
        }
        throw new IllegalStateException("No more targets from strategy");
    }
}

