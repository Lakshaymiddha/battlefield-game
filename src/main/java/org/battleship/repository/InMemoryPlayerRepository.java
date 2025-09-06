package org.battleship.repository;

import org.battleship.model.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

public class InMemoryPlayerRepository implements PlayerRepository {
    private final ConcurrentHashMap<String, Player> byId = new ConcurrentHashMap<>();

    @Override
    public void addPlayer(Player p) {
        byId.put(p.getId(), p);
    }

    @Override
    public Optional<Player> getById(String id) {
        return Optional.ofNullable(byId.get(id));
    }
}
