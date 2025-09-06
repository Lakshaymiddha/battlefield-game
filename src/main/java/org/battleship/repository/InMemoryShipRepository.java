package org.battleship.repository;

import org.battleship.exceptions.InvalidShipPlacementException;
import org.battleship.exceptions.OverlapException;
import org.battleship.model.Position;
import org.battleship.model.Ship;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe in-memory implementation. Stores ships per player and a cell-to-ship map per player for quick lookup.
 */
public class InMemoryShipRepository implements ShipRepository {
    private final int boardSize;
    private final Map<String, Map<String, Ship>> shipsByPlayer = new ConcurrentHashMap<>();
    private final Map<String, Map<Position, String>> cellToShip = new ConcurrentHashMap<>();
    private final Map<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

    public InMemoryShipRepository(int boardSize, Collection<String> playerIds) {
        this.boardSize = boardSize;
        for (String pid : playerIds) {
            shipsByPlayer.put(pid, new ConcurrentHashMap<>());
            cellToShip.put(pid, new ConcurrentHashMap<>());
            locks.put(pid, new ReentrantReadWriteLock());
        }
    }

    @Override
    public void addShip(String playerId, Ship ship) throws InvalidShipPlacementException, OverlapException {
        ReentrantReadWriteLock lock = locks.get(playerId);
        if (lock == null) throw new IllegalArgumentException("unknown player " + playerId);
        lock.writeLock().lock();
        try {
            // bounds check
            for (Position p : ship.getOccupiedPositions()) {
                if (p.getX() < 0 || p.getY() < 0 || p.getX() >= boardSize || p.getY() >= boardSize)
                    throw new InvalidShipPlacementException("Ship " + ship.getId() + " out of bounds at " + p);
            }
            Map<Position, String> map = cellToShip.get(playerId);
            for (Position p : ship.getOccupiedPositions()) {
                if (map.containsKey(p)) throw new OverlapException("Ship " + ship.getId() + " overlaps at " + p);
            }
            shipsByPlayer.get(playerId).put(ship.getId(), ship);
            for (Position p : ship.getOccupiedPositions()) map.put(p, ship.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Ship> getShip(String playerId, String shipId) {
        Map<String, Ship> m = shipsByPlayer.get(playerId);
        if (m == null) return Optional.empty();
        return Optional.ofNullable(m.get(shipId));
    }

    @Override
    public List<Ship> listShips(String playerId) {
        Map<String, Ship> m = shipsByPlayer.get(playerId);
        if (m == null) return Collections.emptyList();
        return new ArrayList<>(m.values());
    }

    @Override
    public Optional<Ship> getShipAt(String playerId, Position pos) {
        Map<Position, String> m = cellToShip.get(playerId);
        if (m == null) return Optional.empty();
        String sid = m.get(pos);
        if (sid == null) return Optional.empty();
        return Optional.ofNullable(shipsByPlayer.get(playerId).get(sid));
    }

    @Override
    public Optional<String> destroyShipAt(String playerId, Position pos) {
        ReentrantReadWriteLock lock = locks.get(playerId);
        lock.writeLock().lock();
        try {
            Map<Position, String> m = cellToShip.get(playerId);
            String sid = m.get(pos);
            if (sid == null) return Optional.empty();
            Ship s = shipsByPlayer.get(playerId).remove(sid);
            if (s == null) return Optional.empty();
            // remove all cells
            for (Position p : s.getOccupiedPositions()) m.remove(p);
            // mark destroyed on ship object as well
            s.destroy();
            return Optional.of(sid);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int activeShipCount(String playerId) {
        Map<String, Ship> m = shipsByPlayer.get(playerId);
        if (m == null) return 0;
        return (int) m.values().stream().filter(s -> !s.isDestroyed()).count();
    }
}

