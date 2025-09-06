package org.battleship.service;

import org.battleship.exceptions.DuplicateShotException;
import org.battleship.exceptions.GameStateException;
import org.battleship.exceptions.InvalidShipPlacementException;
import org.battleship.exceptions.OverlapException;
import org.battleship.model.Ship;
import org.battleship.model.Player;
import org.battleship.model.ShipFactory;
import org.battleship.model.Position;
import org.battleship.repository.PlayerRepository;
import org.battleship.repository.ShipRepository;
import org.battleship.repository.ShotRepository;
import org.battleship.strategy.FireStrategy;
import org.battleship.strategy.RandomFireStrategy;
import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;

import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Public facade. Exposes initGame, addShip, viewBattleField, startGame.
 * Uses Strategy pattern for firing. Observer pattern to notify listeners.
 */

/**
 * Does not expose repository details. Uses strategies and repositories to run game.
 */
public class GameEngine {
    private final ShipRepository shipRepo;
    private final PlayerRepository playerRepo;
    private final ShotRepository shotRepo;
    private final int boardSize;
//    private FireStrategy strategyA;
//    private FireStrategy strategyB;
    private final Set<GameEventListener> listeners = ConcurrentHashMap.newKeySet();
    private final Map<String, FireStrategy> strategies = new HashMap<>();

    public GameEngine(int boardSize, ShipRepository shipRepo, PlayerRepository playerRepo, ShotRepository shotRepo) {
        this.boardSize = boardSize;
        this.shipRepo = shipRepo;
        this.playerRepo = playerRepo;
        this.shotRepo = shotRepo;
    }

    public void setStrategies(String playerId, FireStrategy strategy) {
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
    public GameEvent fireOnce(String attackerId, String defenderId, FireStrategy strategy) throws DuplicateShotException {
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

    public FireStrategy getStrategyForPlayer(String playerId) {
        return strategies.get(playerId);
    }
}
