package org.battleship.event;

import org.battleship.model.Position;

/**
 * Simple game event model.
 */
public final class GameEvent {
    public enum Type {HIT, MISS}

    private final Type type;
    private final String byPlayerId;
    private final Position position;
    private final String destroyedShipId; // nullable

    public GameEvent(Type type, String byPlayerId, Position position, String destroyedShipId) {
        this.type = type;
        this.byPlayerId = byPlayerId;
        this.position = position;
        this.destroyedShipId = destroyedShipId;
    }

    public Type getType() {
        return type;
    }

    public String getByPlayerId() {
        return byPlayerId;
    }

    public Position getPosition() {
        return position;
    }

    public String getDestroyedShipId() {
        return destroyedShipId;
    }
}

