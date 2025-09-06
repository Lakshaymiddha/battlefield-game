package org.battleship.strategy;


import org.battleship.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RandomFireStrategyTest {
    @Test
    void noDuplicatesAndExhaustsPool() {
        RandomFireStrategy strat = new RandomFireStrategy(3, 5, 6);
        int count = 0;
        Position p;
        while ((p = strat.next(null, null)) != null) count++;
        assertEquals((5 - 3 + 1) * 6, count);
        assertNull(strat.next(null, null));
    }
}

