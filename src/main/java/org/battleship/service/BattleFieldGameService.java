package org.battleship.service;

import org.battleship.event.GameEventListener;
import org.battleship.exceptions.*;
import org.battleship.model.Position;
import org.battleship.model.Player;
import org.battleship.model.Ship;
import org.battleship.model.ShipFactory;
import org.battleship.repository.*;

import org.battleship.strategy.RandomFireStrategy;

import java.util.*;

/**
 * Public facade that glues repositories and engine and exposes the required API:
 * initGame(N), addShip(id,size,ax,ay,bx,by), viewBattleField(), startGame()
 */
public class BattleFieldGameService {
    private GameEngine engine;
    private ShipRepository shipRepo;
    private PlayerRepository playerRepo;
    private ShotRepository shotRepo;
    private boolean initialized = false;
    private String firstPlayerId;
    private String secondPlayerId;

    public void initGame(int N, String firstPlayerId, String secondPlayerId) {
        if (N < 2) throw new IllegalArgumentException("N must be at least or more than 2");
        this.firstPlayerId = firstPlayerId;
        this.secondPlayerId = secondPlayerId;
        this.shipRepo = new InMemoryShipRepository(N, Arrays.asList(this.firstPlayerId, this.secondPlayerId));
        this.playerRepo = new InMemoryPlayerRepository();
        this.shotRepo = new InMemoryShotRepository();
        // register players
        playerRepo.addPlayer(new Player.Builder().id(this.firstPlayerId).name("PlayerA").build());
        playerRepo.addPlayer(new Player.Builder().id(this.secondPlayerId).name("PlayerB").build());
        this.engine = new GameEngine(N, shipRepo, playerRepo, shotRepo);
        // default strategies: A shoots into right half, B shoots into left half
        int mid = N / 2;
        engine.setStrategies(this.firstPlayerId, new RandomFireStrategy(mid, N - 1, N));
        engine.setStrategies(this.secondPlayerId, new RandomFireStrategy(0, mid - 1, N));
        initialized = true;
        System.out.println("Game initialized " + N + "x" + N);
    }

    private void ensureInit() throws GameStateException {
        if (!initialized) throw new GameStateException("Game not initialized; call initGame(N) first");
    }

    /**
     * Adds a ship with top-left coords for both players. Throws checked exceptions on invalid placement/overlap.
     */
    public void addShip(String id, int size, int ax, int ay, int bx, int by)
            throws GameStateException {
        ensureInit();
        // Build ships with factory
        Ship sA = ShipFactory.createSquareShip(id, size, new Position(ax, ay));
        Ship sB = ShipFactory.createSquareShip(id, size, new Position(bx, by));
        // Validate territory (top-left semantics)
        try {
            validateTerritory(sA, firstPlayerId);
            validateTerritory(sB, secondPlayerId);
            // add to repo (repo checks bounds & overlap)
            shipRepo.addShip(firstPlayerId, sA);
            shipRepo.addShip(secondPlayerId, sB);
        } catch (InvalidShipPlacementException | OverlapException e) {
            System.err.println("Failed to place ship: " + e);
            return;
        }
        System.out.println("Ship " + id + " added for both players");
    }

    private void validateTerritory(Ship s, String playerId) throws InvalidShipPlacementException {
        int board = engine.getBoardSize();
        int mid = board / 2;
        for (Position p : s.getOccupiedPositions()) {
            if (p.getX() < 0 || p.getY() < 0 || p.getX() >= board || p.getY() >= board)
                throw new InvalidShipPlacementException(s.getId() + " out of board at " + p);
            if (playerId.equals(firstPlayerId)) {
                if (p.getX() > mid - 1)
                    throw new InvalidShipPlacementException(s.getId() + " not in PlayerA territory: " + p);
            } else {
                if (p.getX() < mid)
                    throw new InvalidShipPlacementException(s.getId() + " not in PlayerB territory: " + p);
            }
        }
    }

    public void viewBattleField() throws GameStateException {
        ensureInit();
        int N = engine.getBoardSize();
        Map<org.battleship.model.Position, String> labels = new HashMap<>();
        shipRepo.listShips(firstPlayerId).forEach(s -> s.getOccupiedPositions().forEach(p -> labels.put(p, "A-" + s.getId())));
        shipRepo.listShips(secondPlayerId).forEach(s -> s.getOccupiedPositions().forEach(p -> labels.putIfAbsent(p, "B-" + s.getId())));
        System.out.println("Battlefield view (top row y=" + (N - 1) + "):");
        for (int y = N - 1; y >= 0; y--) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < N; x++) {
                org.battleship.model.Position pos = new org.battleship.model.Position(x, y);
                sb.append(String.format("%-8s", labels.getOrDefault(pos, ".")));
            }
            System.out.println(sb.toString());
        }
    }

    /**
     * Start game synchronously. Throws checked GameStateException when preconditions fail.
     */
    public void startGame() throws GameStateException {
        ensureInit();
        int a = shipRepo.activeShipCount(firstPlayerId);
        int b = shipRepo.activeShipCount(secondPlayerId);
        if (a == 0 && b == 0) throw new GameStateException("No ships placed");
        if (a != b) throw new GameStateException("Fleets must be equal to start");
        System.out.println("Game started. PlayerA goes first.");

        boolean playerATurn = true;
        while (shipRepo.activeShipCount(firstPlayerId) > 0 && shipRepo.activeShipCount(secondPlayerId) > 0) {
            try {
                if (playerATurn) engine.fireOnce(firstPlayerId, secondPlayerId, engine.getStrategyForPlayer(firstPlayerId));
                else engine.fireOnce(secondPlayerId, firstPlayerId, engine.getStrategyForPlayer(secondPlayerId));
            } catch (DuplicateShotException d) {
                // strategy should avoid duplicates; if occurs, skip turn
                System.out.println("Duplicate shot encountered: " + d.getMessage());
            }
            playerATurn = !playerATurn;
        }

        int aRem = shipRepo.activeShipCount(firstPlayerId);
        int bRem = shipRepo.activeShipCount(secondPlayerId);
        if (aRem == 0 && bRem == 0) System.out.println("GameOver: Draw.");
        else if (bRem == 0) System.out.println("GameOver. PlayerA wins.");
        else if (aRem == 0) System.out.println("GameOver. PlayerB wins.");
        else System.out.println("Game ended unexpectedly.");
    }

    public void addListener(GameEventListener l) {
        engine.addListener(l);
    }

    public ShipRepository getShipRepo() {
        return shipRepo;
    }
}
