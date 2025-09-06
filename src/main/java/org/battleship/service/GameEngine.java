package org.battleship.service;

import org.battleship.exceptions.DuplicateShotException;
import org.battleship.model.Ship;
import org.battleship.model.Position;
import org.battleship.repository.PlayerRepository;
import org.battleship.repository.ShipRepository;
import org.battleship.repository.ShotRepository;
import org.battleship.strategy.MissileFireStrategy;
import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;

import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Does not expose repository details. Uses strategies and repositories to run game and Observer pattern to notify listeners.
 */
public class GameEngine {
    private final ShipRepository shipRepo;
    private final PlayerRepository playerRepo;
    private final ShotRepository shotRepo;
    private final int boardSize;
    private final Set<GameEventListener> listeners = ConcurrentHashMap.newKeySet();
    private final Map<String, MissileFireStrategy> strategies = new HashMap<>();

    public GameEngine(int boardSize, ShipRepository shipRepo, PlayerRepository playerRepo, ShotRepository shotRepo) {
        this.boardSize = boardSize;
        this.shipRepo = shipRepo;
        this.playerRepo = playerRepo;
        this.shotRepo = shotRepo;
    }

    public void setStrategies(String playerId, MissileFireStrategy strategy) {
        this.strategies.put(playerId, strategy);
    }

    public void addListener(GameEventListener l) {
        listeners.add(l);
    }

    public void removeListener(GameEventListener l) {
        listeners.remove(l);
    }

    private void notifyAll(GameEvent e) {
        listeners.forEach(l -> l.onEvent(e));
    }

    /**
     * Runs one firing turn: attackerId fires at opponentId using the attacker's strategy.
     * Returns the event generated.
     */
    public GameEvent fireOnce(String attackerId, String defenderId, MissileFireStrategy strategy) throws DuplicateShotException {
        Position target = strategy.next(attackerId, defenderId);
        if (target == null) throw new IllegalStateException("No more targets from strategy");
        if (shotRepo.alreadyFired(target)) throw new DuplicateShotException("Already fired at " + target);
        shotRepo.recordShot(target);

        Optional<Ship> hitShip = shipRepo.getShipAt(defenderId, target);
        if (hitShip.isPresent()) {
            Optional<String> destroyedId = shipRepo.destroyShipAt(defenderId, target);
            String sid = destroyedId.orElse(hitShip.get().getId());
            GameEvent ev = new GameEvent(GameEvent.Type.HIT, attackerId, target, sid);
            notifyAll(ev);
            return ev;
        } else {
            GameEvent ev = new GameEvent(GameEvent.Type.MISS, attackerId, target, null);
            notifyAll(ev);
            return ev;
        }
    }

    public int activeShipCount(String playerId) {
        return shipRepo.activeShipCount(playerId);
    }

    public int getBoardSize() {
        return boardSize;
    }

    public MissileFireStrategy getStrategyForPlayer(String playerId) {
        return strategies.get(playerId);
    }
}
