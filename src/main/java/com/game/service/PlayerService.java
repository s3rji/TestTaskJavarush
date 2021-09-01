package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlayerService {
    Optional<Player> findById(Long id);

    void save(Player player);

    void delete(Long id);

    List<Player> findAll();
}
