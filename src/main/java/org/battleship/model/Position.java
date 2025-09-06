package org.battleship.model;

import java.util.Objects;

/**
 * Immutable position value object (x,y). Top-left semantics used for ships.
 */
public final class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        if (x < 0 || y < 0) throw new IllegalArgumentException("Coordinates must be non-negative");
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
