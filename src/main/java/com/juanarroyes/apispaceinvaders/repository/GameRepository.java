package com.juanarroyes.apispaceinvaders.repository;

import com.juanarroyes.apispaceinvaders.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, String> {
}
