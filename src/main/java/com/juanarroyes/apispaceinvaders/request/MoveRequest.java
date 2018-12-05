package com.juanarroyes.apispaceinvaders.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.juanarroyes.apispaceinvaders.dto.*;

import java.util.List;

public class MoveRequest  {

    private Game game;

    private Player player;

    private Board board;

    @JsonAlias(value = "players")
    private List<Coordinates> enemies;

    private List<Invader> invaders;

    public MoveRequest() {

    }

    public MoveRequest(Game game, Player player, Board board, List<Coordinates> enemies, List<Invader> invaders) {
        this.game = game;
        this.player = player;
        this.board = board;
        this.enemies = enemies;
        this.invaders = invaders;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Coordinates> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Coordinates> enemies) {
        this.enemies = enemies;
    }

    public List<Invader> getInvaders() {
        return invaders;
    }

    public void setInvaders(List<Invader> invaders) {
        this.invaders = invaders;
    }
}
