package org.battleship;

import org.battleship.event.GameEvent;
import org.battleship.event.GameEventListener;
import org.battleship.service.BattleFieldGameService;

public class BattleshipApplication {
    private static final int N = 6;

    public static void main(String[] args) {
        BattleFieldGameService service = new BattleFieldGameService();
        service.initGame(N, "PlayerA", "PlayerB");

        // attach a simple event listener
        service.addListener(new GameEventListener() {
            @Override
            public void onEvent(GameEvent event) {
                String message;
                if (event.getType() == GameEvent.Type.HIT) {
                    message = event.getByPlayerId() + "’s turn: Missile fired at ("
                            + event.getPosition().getX() + ", " + event.getPosition().getY()
                            + "). “Hit”. "
                            + "Opponent’s ship with id \"" + event.getDestroyedShipId() + "\" destroyed.";
                } else {
                    message = event.getByPlayerId() + "’s turn: Missile fired at ("
                            + event.getPosition().getX() + ", " + event.getPosition().getY()
                            + "). “Miss”.";
                }
                System.out.println(message);
            }
        });

        try {
            service.addShip("SH1", 2, 1, 4, 4, 4);
            service.addShip("SH2", 1, 0, 0, 5, 0);
        } catch (Exception e) {
            System.err.println("Failed to place ship: " + e);
        }

        try {
            service.viewBattleField();
            service.startGame();
            service.viewBattleField();
        } catch (Exception e) {
            System.err.println("Game error: " + e.getMessage());
        }
    }
}
