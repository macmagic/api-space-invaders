package com.juanarroyes.apispaceinvaders.dto;

import java.util.List;

public class Stage {

    private String gameId;

    private String playerId;

    private MazeSize mazeSize;

    private Area area;

    private List<Coordinates> enemies;

    private List<Coordinates> walls;

    private List<Invader> invaders;

    private Coordinates actualPosition;

    private Coordinates previousPosition;

    public Stage() {

    }

    public Stage(String gameId,
                 String playerId,
                 MazeSize mazeSize,
                 Area area,
                 List<Coordinates> enemies,
                 List<Coordinates> walls,
                 List<Invader> invaders,
                 Coordinates actualPosition,
                 Coordinates previousPosition) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.area = area;
        this.enemies = enemies;
        this.walls = walls;
        this.invaders = invaders;
        this.actualPosition = actualPosition;
        this.previousPosition = previousPosition;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public MazeSize getMazeSize() {
        return mazeSize;
    }

    public void setMazeSize(MazeSize mazeSize) {
        this.mazeSize = mazeSize;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public List<Coordinates> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Coordinates> enemies) {
        this.enemies = enemies;
    }

    public List<Coordinates> getWalls() {
        return walls;
    }

    public void setWalls(List<Coordinates> walls) {
        this.walls = walls;
    }

    public List<Invader> getInvaders() {
        return invaders;
    }

    public void setInvaders(List<Invader> invaders) {
        this.invaders = invaders;
    }

    public Coordinates getActualPosition() {
        return actualPosition;
    }

    public void setActualPosition(Coordinates actualPosition) {
        this.actualPosition = actualPosition;
    }

    public Coordinates getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(Coordinates previousPosition) {
        this.previousPosition = previousPosition;
    }
}
