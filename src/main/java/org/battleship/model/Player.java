package org.battleship.model;

/**
 * Simple player model.
 * Use Player.Builder to construct.
 */
public final class Player {
    private final String id;
    private final String name;

    private Player(Builder b) {
        this.id = b.id;
        this.name = b.name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private String id;
        private String name;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Player build() {
            if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
            if (name == null || name.isEmpty()) throw new IllegalArgumentException("name required");
            return new Player(this);
        }
    }

    @Override
    public String toString() {
        return name + "(" + id + ")";
    }
}
