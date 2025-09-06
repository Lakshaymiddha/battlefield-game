package org.battleship.model;

/**
 * Factory for Ship creation - central place in case we add different ship types later.
 */
public final class ShipFactory {
    private ShipFactory() {
    }

    public static Ship createSquareShip(String id, int size, Position topLeft) {
        return new Ship.Builder().id(id).size(size).topLeft(topLeft).build();
    }
}
