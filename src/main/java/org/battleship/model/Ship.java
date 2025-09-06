package org.battleship.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Ship value object built with Builder. Square-shaped (size x size) using top-left position.
 * Immutable except for destroyed flag tracked externally (repository removes ship on destruction).
 */
public class Ship {

    private final String id;
    private final int size;
    private final Player owner;
    private final Position topLeft;
    private final Set<Position> occupiedPositions;
    private final Set<Position> hits = new HashSet<>();

    private Ship(Builder builder) {
        this.id = builder.id;
        this.size = builder.size;
        this.owner = builder.owner;
        this.topLeft = builder.topLeft;
        this.occupiedPositions = calculateOccupiedPositions();
    }

    private Set<Position> calculateOccupiedPositions() {
        Set<Position> positions = new HashSet<>();
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                positions.add(new Position(topLeft.getX() + dx, topLeft.getY() + dy));
            }
        }
        return positions;
    }

    public boolean occupies(Position pos) {
        return occupiedPositions.contains(pos);
    }

    public void hitAt(Position pos) {
        if (occupies(pos)) {
            hits.add(pos);
        }
    }

    public boolean isDestroyed() {
        return hits.containsAll(occupiedPositions);
    }

    public String getId() { return id; }
    public Player getOwner() { return owner; }
    public Set<Position> getOccupiedPositions() { return occupiedPositions; }

    @Override
    public String toString() {
        return (owner == Player.PLAYER_A ? "A-" : "B-") + id;
    }

    public static class Builder {
        private String id;
        private int size;
        private Player owner;
        private Position topLeft;

        public Builder id(String id) { this.id = id; return this; }
        public Builder size(int size) { this.size = size; return this; }
        public Builder owner(Player owner) { this.owner = owner; return this; }
        public Builder topLeft(Position topLeft) { this.topLeft = topLeft; return this; }

        public Ship build() {
            if (id == null || id.isEmpty()) throw new IllegalArgumentException("Ship ID cannot be null/empty");
            if (size <= 0) throw new IllegalArgumentException("Ship size must be > 0");
            if (topLeft == null) throw new IllegalArgumentException("Ship position cannot be null");
            if (owner == null) throw new IllegalArgumentException("Ship owner cannot be null");
            return new Ship(this);
        }
    }
}
