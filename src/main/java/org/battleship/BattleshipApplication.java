package org.battleship;

import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;
import org.battleship.service.BattleFieldGameService;

public class BattleshipApplication {
    private static final int N = 7;

    public static void main(String[] args) {
        BattleFieldGameService service = new BattleFieldGameService();
        service.initGame(N);

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
            service.addShip("SH2", 1, 0, 0, 5, 6);
        } catch (Exception e) {
            System.err.println("Failed to place ship: " + e);
        }

        try {
            service.viewBattleField();
            service.startGame();
        } catch (Exception e) {
            System.err.println("Game error: " + e.getMessage());
        }
    }
}
