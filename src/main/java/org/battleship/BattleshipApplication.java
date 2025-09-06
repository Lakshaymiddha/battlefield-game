package org.battleship;

import org.battleship.exceptions.GameException;
import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;
import org.battleship.service.GameEngine;

public class BattleshipApplication {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();

        // Add a simple event listener that prints hits/misses
        engine.addListener(new GameEventListener() {
            @Override
            public void onEvent(GameEvent event) {
                System.out.println("[EVENT] " + event.getByPlayer() + " -> " + event.getType() + " at " + event.getPosition() +
                        (event.getDestroyedShipId() != null ? " destroyed:" + event.getDestroyedShipId() : ""));
            }
        });

        try {
            engine.initGame(6);
            // add ships - caller can catch exceptions individually and continue
            try {
                engine.addShip("SH1", 2, 1, 4, 4, 4);
                engine.addShip("SH2", 1, 0, 0, 5, 0);
            } catch (GameException ge) {
                System.out.println("Ship placement failed: " + ge.getMessage());
            }

            engine.viewBattleField();
            engine.startGame();

        } catch (GameException | IllegalArgumentException e) {
            System.err.println("Game error: " + e.getMessage());
        }
    }
}


