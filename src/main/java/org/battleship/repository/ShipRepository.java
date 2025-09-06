package org.battleship.repository;

import org.battleship.exceptions.InvalidShipPlacementException;
import org.battleship.exceptions.OverlapException;
import org.battleship.model.Position;
import org.battleship.model.Ship;

import java.util.List;
import java.util.Optional;

public interface ShipRepository {
    void addShip(String playerId, Ship ship) throws InvalidShipPlacementException, OverlapException;

    Optional<Ship> getShip(String playerId, String shipId);

    List<Ship> listShips(String playerId);

    Optional<Ship> getShipAt(String playerId, Position pos);

    /**
     * Destroy ship at position and return destroyed ship id (or empty if none).
     * Should atomically remove ship occupancy mapping.
     */
    Optional<String> destroyShipAt(String playerId, Position pos);

    int activeShipCount(String playerId);
}

