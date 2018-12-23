package com.juanarroyes.apispaceinvaders.repository;

import com.juanarroyes.apispaceinvaders.model.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStatusRepository extends JpaRepository<GameStatus, String> {
}
