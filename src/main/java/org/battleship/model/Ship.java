package org.battleship.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Square ship with top-left position and size (size x size).
 * A hit destroys the ship immediately (per original requirements).
 */
public final class Ship {
    private final String id;
    private final int size;
    private final Position topLeft;
    private boolean destroyed;
    private final Set<Position> occupied;

    private Ship(Builder b) {
        this.id = b.id;
        this.size = b.size;
        this.topLeft = b.topLeft;
        this.destroyed = false;
        this.occupied = computeOccupied(topLeft, size);
    }

    private static Set<Position> computeOccupied(Position topLeft, int size) {
        Set<Position> set = new HashSet<>();
        for (int dx = 0; dx < size; dx++)
            for (int dy = 0; dy < size; dy++)
                set.add(new Position(topLeft.getX() + dx, topLeft.getY() + dy));
        return Collections.unmodifiableSet(set);
    }

    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public Position getTopLeft() {
        return topLeft;
    }

    public Set<Position> getOccupiedPositions() {
        return occupied;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * mark the ship destroyed (single-hit destroy)
     */
    public void destroy() {
        destroyed = true;
    }

    public boolean occupies(Position p) {
        return occupied.contains(p);
    }

    @Override
    public String toString() {
        return id;
    }

    public static class Builder {
        private String id;
        private int size;
        private Position topLeft;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder topLeft(Position p) {
            this.topLeft = p;
            return this;
        }

        public Ship build() {
            Objects.requireNonNull(id, "id required");
            Objects.requireNonNull(topLeft, "topLeft required");
            if (size <= 0) throw new IllegalArgumentException("size>0 required");
            return new Ship(this);
        }
    }
}
