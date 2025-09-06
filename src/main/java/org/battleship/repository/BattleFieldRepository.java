//package org.battleship.repository;
//
//import org.battleship.exceptions.InvalidShipPlacementException;
//import org.battleship.exceptions.OverlapException;
//import org.battleship.model.Position;
//import org.battleship.model.Player;
//import org.battleship.model.Ship;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
///**
// * Per-player ship management with cell->ship mapping for quick checks.
// */
//public class BattleFieldRepository {
//    private final int boardSize;
//    private final Map<String, Player> players = new ConcurrentHashMap<>();
//    private final Map<String, Map<Position, String>> cellMap = new ConcurrentHashMap<>();
//    private final Map<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
//
//    public BattleFieldRepository(int boardSize) {
//        this.boardSize = boardSize;
//    }
//
//    public void registerPlayer(Player p) {
//        players.put(p.getName(), p);
//        cellMap.put(p.getName(), new HashMap<>());
//        locks.put(p.getName(), new ReentrantReadWriteLock());
//    }
//
//    public void addShip(String playerName, Ship ship) throws InvalidShipPlacementException, OverlapException {
//        Player p = players.get(playerName);
//        if (p == null) throw new IllegalArgumentException("unknown player " + playerName);
//        ReentrantReadWriteLock lock = locks.get(playerName);
//        lock.writeLock().lock();
//        try {
//            // bounds check
//            for (Position pos : ship.getOccupiedPositions()) {
//                if (pos.getX() < 0 || pos.getY() < 0 || pos.getX() >= boardSize || pos.getY() >= boardSize) {
//                    throw new InvalidShipPlacementException("ship " + ship.getId() + " out of bounds at " + pos);
//                }
//            }
//            Map<Position, String> map = cellMap.get(playerName);
//            for (Position pos : ship.getOccupiedPositions()) {
//                if (map.containsKey(pos)) throw new OverlapException("overlap at " + pos);
//            }
////            p.addShip(ship);
//            for (Position pos : ship.getOccupiedPositions()) map.put(pos, ship.getId());
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    public boolean hasShipAt(String playerName, Position pos) {
//        ReentrantReadWriteLock lock = locks.get(playerName);
//        lock.readLock().lock();
//        try {
//            return cellMap.get(playerName).containsKey(pos);
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    public String destroyShipAt(String playerName, Position pos) {
//        ReentrantReadWriteLock lock = locks.get(playerName);
//        lock.writeLock().lock();
//        try {
//            Map<Position, String> map = cellMap.get(playerName);
//            String sid = map.get(pos);
//            if (sid == null) return null;
//            // remove ship from player and clear its cells
//            Player p = players.get(playerName);
//            Optional<Ship> shipOpt = p.getShips().stream().filter(s -> s.getId().equals(sid)).findFirst();
//            if (shipOpt.isEmpty()) return null;
//            Ship s = shipOpt.get();
//            p.removeShip(sid);
//            for (Position pp : s.getOccupiedPositions()) map.remove(pp);
//            return sid;
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    public int getActiveShipsCount(String playerName) {
//        return players.get(playerName).getShips().size();
//    }
//
//    public Collection<Ship> getShips(String playerName) {
//        return players.get(playerName).getShips();
//    }
//
//    // helpers used by tests
//    public Player getPlayer(String name) {
//        return players.get(name);
//    }
//
//    public void clearPlayer(String name) {
//        ReentrantReadWriteLock lock = locks.get(name);
//        lock.writeLock().lock();
//        try {
//            players.get(name).getShips().forEach(s -> {
//            });
//            players.get(name).getShips().clear(); // not ideal since we expose unmodifiable, but for tests assume possible
//            cellMap.get(name).clear();
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//}
//
