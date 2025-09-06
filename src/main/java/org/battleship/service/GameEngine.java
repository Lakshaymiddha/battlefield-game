package org.battleship.service;

import org.battleship.exceptions.GameStateException;
import org.battleship.exceptions.InvalidShipPlacementException;
import org.battleship.exceptions.OverlapException;
import org.battleship.model.Ship;
import org.battleship.model.Player;
import org.battleship.model.ShipFactory;
import org.battleship.model.Position;
import org.battleship.repository.BattleFieldRepository;
import org.battleship.strategy.FireStrategy;
import org.battleship.strategy.RandomFireStrategy;
import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Public facade. Exposes initGame, addShip, viewBattleField, startGame.
 * Uses Strategy pattern for firing. Observer pattern to notify listeners.
 */
public class GameEngine {
    private final Set<GameEventListener> listeners = ConcurrentHashMap.newKeySet();
    private BattleFieldRepository repo;
    private int N;
    private boolean initialized = false;
    private String playerAName = "PlayerA";
    private String playerBName = "PlayerB";
    private FireStrategy strategyA, strategyB;
    private final Set<String> firedGlobal = ConcurrentHashMap.newKeySet();
    private int leftMax, rightMin;

    public GameEngine() {
    }

    public void addListener(GameEventListener l) {
        listeners.add(l);
    }

    public void removeListener(GameEventListener l) {
        listeners.remove(l);
    }

    private void notify(GameEvent e) {
        listeners.forEach(l -> l.onEvent(e));
    }

    /**
     * initGame: N must be even and between 4..20 (as earlier tests expect)
     */
    public void initGame(int N) {
        if (N < 4 || N > 20) throw new IllegalArgumentException("N must be between 4 and 20");
        if (N % 2 != 0) throw new IllegalArgumentException("N must be even");
        this.N = N;
        int mid = N / 2;
        leftMax = mid - 1;
        rightMin = mid;
        repo = new BattleFieldRepository(N);
        // players
        repo.registerPlayer(new Player.Builder().name(playerAName).build());
        repo.registerPlayer(new Player.Builder().name(playerBName).build());
        // default strategies
        this.strategyA = new RandomFireStrategy(rightMin, N - 1, N);
        this.strategyB = new RandomFireStrategy(0, leftMax, N);
        initialized = true;
        System.out.println("Game initialized " + N + "x" + N);
    }

    private void ensureInit() throws GameStateException {
        if (!initialized) throw new GameStateException("initGame first");
    }

    /**
     * addShip: builds ships via ShipFactory and adds into repo for both players.
     * Throws checked exceptions when placement invalid - caller can catch and continue.
     */
    public void addShip(String id, int size, int ax, int ay, int bx, int by)
            throws InvalidShipPlacementException, OverlapException, GameStateException {
        ensureInit();
        // construct ships using factory
        Ship sA = ShipFactory.createSquareShip("A-" + id, size, new Position(ax, ay));
        Ship sB = ShipFactory.createSquareShip("B-" + id, size, new Position(bx, by));
        // validate territory for A
        for (Position p : sA.getOccupiedPositions()) {
            if (p.getX() < 0 || p.getY() < 0 || p.getX() >= N || p.getY() >= N)
                throw new InvalidShipPlacementException("A-" + id + " out of board " + p);
            if (p.getX() > leftMax)
                throw new InvalidShipPlacementException("A-" + id + " not in PlayerA territory: " + p);
        }
        for (Position p : sB.getOccupiedPositions()) {
            if (p.getX() < 0 || p.getY() < 0 || p.getX() >= N || p.getY() >= N)
                throw new InvalidShipPlacementException("B-" + id + " out of board " + p);
            if (p.getX() < rightMin)
                throw new InvalidShipPlacementException("B-" + id + " not in PlayerB territory: " + p);
        }
        // add to repository (does overlap + bounds check internally)
        repo.addShip(playerAName, sA);
        repo.addShip(playerBName, sB);
        System.out.println("Added ship " + id + " to both players");
    }

    public void viewBattleField() throws GameStateException {
        ensureInit();
        // build label map from repo
        Map<Position, String> labels = new HashMap<>();
        repo.getShips(playerAName).forEach(s -> s.getOccupiedPositions().forEach(p -> labels.put(p, s.getId())));
        repo.getShips(playerBName).forEach(s -> s.getOccupiedPositions().forEach(p -> labels.putIfAbsent(p, s.getId())));
        System.out.println("Battlefield view (top row y=" + (N - 1) + "):");
        for (int y = N - 1; y >= 0; y--) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < N; x++) {
                Position p = new Position(x, y);
                sb.append(String.format("%-8s", labels.getOrDefault(p, ".")));
            }
            System.out.println(sb.toString());
        }
    }

    /**
     * startGame - PlayerA starts. Throws GameStateException for invalid preconditions (unequal fleets etc).
     * Not a long-running background job — runs synchronously for tests.
     */
    public void startGame() throws GameStateException {
        ensureInit();
        int a = repo.getActiveShipsCount(playerAName);
        int b = repo.getActiveShipsCount(playerBName);
        if (a == 0 && b == 0) throw new GameStateException("No ships placed");
        if (a != b) throw new GameStateException("Fleets must be equal to start");
        System.out.println("Game started. PlayerA goes first.");

        boolean playerATurn = true;
        FireStrategy currentStrategy = strategyA;
        while (repo.getActiveShipsCount(playerAName) > 0 && repo.getActiveShipsCount(playerBName) > 0) {
            Position target = currentStrategy.next(repo.getPlayer(playerATurn ? playerAName : playerBName),
                    repo.getPlayer(playerATurn ? playerBName : playerAName));
            if (target == null) {
                System.out.println("No more targets for current strategy. Ending.");
                break;
            }
            String key = target.toString();
            if (!firedGlobal.add(key)) {
                System.out.println("Duplicate global shot " + target + " - skipping");
            } else {
                boolean hit = false;
                String destroyedId = null;
                if (playerATurn) {
                    if (repo.hasShipAt(playerBName, target)) {
                        destroyedId = repo.destroyShipAt(playerBName, target);
                        hit = true;
                    }
                } else {
                    if (repo.hasShipAt(playerAName, target)) {
                        destroyedId = repo.destroyShipAt(playerAName, target);
                        hit = true;
                    }
                }
                // notify observers
                GameEvent.Type type = hit ? GameEvent.Type.HIT : GameEvent.Type.MISS;
                GameEvent ev = new GameEvent(type, playerATurn ? playerAName : playerBName, target, destroyedId);
                notify(ev);

                // print outcome
                if (hit) {
                    System.out.printf("%s's turn: fired at %s : HIT : %s destroyed. Remaining A:%d B:%d%n",
                            playerATurn ? "PlayerA" : "PlayerB", target,
                            destroyedId, repo.getActiveShipsCount(playerAName), repo.getActiveShipsCount(playerBName));
                } else {
                    System.out.printf("%s's turn: fired at %s : MISS : Remaining A:%d B:%d%n",
                            playerATurn ? "PlayerA" : "PlayerB", target,
                            repo.getActiveShipsCount(playerAName), repo.getActiveShipsCount(playerBName));
                }
            }
            // swap
            playerATurn = !playerATurn;
            currentStrategy = playerATurn ? strategyA : strategyB;
        }

        int aRem = repo.getActiveShipsCount(playerAName);
        int bRem = repo.getActiveShipsCount(playerBName);
        if (aRem == 0 && bRem == 0) System.out.println("GameOver: Draw.");
        else if (bRem == 0) System.out.println("GameOver. PlayerA wins.");
        else if (aRem == 0) System.out.println("GameOver. PlayerB wins.");
        else System.out.println("Game ended without winner.");
    }

    // allow tests to override strategies
    public void setStrategies(FireStrategy a, FireStrategy b) {
        this.strategyA = a;
        this.strategyB = b;
    }
}
