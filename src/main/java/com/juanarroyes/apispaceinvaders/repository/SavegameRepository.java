package com.juanarroyes.apispaceinvaders.repository;

import com.juanarroyes.apispaceinvaders.model.Savegame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavegameRepository extends JpaRepository<Savegame, Long> {

    Optional<Savegame> findOneByGameIdAndPlayerId(String gameId, String playerId);

}
