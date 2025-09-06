package org.battleship.model;

import java.util.*;
import java.util.List;

/**
 * Player with a name and collection of ships.
 * Uses a builder for clean construction.
 */
public class Player {
    private final String name;
    private final Set<Position> firedPositions = new HashSet<>();

    public static final Player PLAYER_A = new Player("PlayerA");
    public static final Player PLAYER_B = new Player("PlayerB");

    private Player(String name) { this.name = name; }

    public String getName() { return name; }

    public boolean alreadyFiredAt(Position pos) {
        return firedPositions.contains(pos);
    }

    public void recordFire(Position pos) {
        firedPositions.add(pos);
    }
}

