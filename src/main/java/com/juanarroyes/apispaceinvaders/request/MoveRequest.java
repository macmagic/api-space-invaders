package com.juanarroyes.apispaceinvaders.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.juanarroyes.apispaceinvaders.dto.*;

import java.util.List;

public class MoveRequest  {

    private GameDto game;

    private PlayerDto player;

    private BoardDto board;

    @JsonAlias(value = "players")
    private List<EnemyDto> enemies;

    private List<InvaderDto> invaders;

    public MoveRequest() {

    }

    public MoveRequest(GameDto game, PlayerDto player, BoardDto board, List<EnemyDto> enemies, List<InvaderDto> invaders) {
        this.game = game;
        this.player = player;
        this.board = board;
        this.enemies = enemies;
        this.invaders = invaders;
    }

    public GameDto getGame() {
        return game;
    }

    public void setGame(GameDto game) {
        this.game = game;
    }

    public PlayerDto getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDto player) {
        this.player = player;
    }

    public BoardDto getBoard() {
        return board;
    }

    public void setBoard(BoardDto board) {
        this.board = board;
    }

    public List<EnemyDto> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<EnemyDto> enemies) {
        this.enemies = enemies;
    }

    public List<InvaderDto> getInvaders() {
        return invaders;
    }

    public void setInvaders(List<InvaderDto> invaders) {
        this.invaders = invaders;
    }
}
