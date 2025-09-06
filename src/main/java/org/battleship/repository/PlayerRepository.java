package org.battleship.repository;


import org.battleship.model.Player;

import java.util.Optional;

public interface PlayerRepository {
    void addPlayer(Player p);

    Optional<Player> getById(String id);
}

