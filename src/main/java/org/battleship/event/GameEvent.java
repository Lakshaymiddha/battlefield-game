package org.battleship.event;

import org.battleship.model.Position;

/**
 * Simple game event model.
 */
public final class GameEvent {
    public enum Type {HIT, MISS}

    private final Type type;
    private final String byPlayer;
    private final Position position;
    private final String destroyedShipId; // nullable

    public GameEvent(Type type, String byPlayer, Position position, String destroyedShipId) {
        this.type = type;
        this.byPlayer = byPlayer;
        this.position = position;
        this.destroyedShipId = destroyedShipId;
    }

    public Type getType() {
        return type;
    }

    public String getByPlayer() {
        return byPlayer;
    }

    public Position getPosition() {
        return position;
    }

    public String getDestroyedShipId() {
        return destroyedShipId;
    }
}
