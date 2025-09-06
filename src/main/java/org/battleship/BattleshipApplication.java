package org.battleship;

import org.battleship.exceptions.GameException;
import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;
import org.battleship.service.GameService;

public class BattleshipApplication {
    public static void main(String[] args) {
        GameService service = new GameService();
        service.initGame(6);

        // attach a simple event listener
        service.addListener(new GameEventListener() {
            @Override
            public void onEvent(GameEvent event) {
                System.out.println("[EVENT] " + event.getByPlayerId() + " -> " + event.getType() + " at " + event.getPosition()
                        + (event.getDestroyedShipId() != null ? " destroyed:" + event.getDestroyedShipId() : ""));
            }
        });

        try {
            service.addShip("SH1", 2, 1, 4, 4, 4);
            service.addShip("SH2", 1, 0, 0, 5, 0);
        } catch (GameException ge) {
            System.out.println("Failed to place some ship: " + ge.getMessage());
        }

        try {
            service.viewBattleField();
            service.startGame();
        } catch (Exception e) {
            System.err.println("Game error: " + e.getMessage());
        }
    }
}
