package com.juanarroyes.apispaceinvaders.repository;

import com.juanarroyes.apispaceinvaders.model.SaveGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaveGameRepository extends JpaRepository<SaveGame, Long> {

    Optional<SaveGame> findOneByGameIdAndPlayerId(String gameId, String playerId);

}
