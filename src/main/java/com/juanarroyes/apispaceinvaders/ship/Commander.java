package com.juanarroyes.apispaceinvaders.ship;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Commander {

    private String[][] maze;
    private Area area;
    private Coordinates actualPosition;
    private Coordinates lastPosition;
    private List<Coordinates> enemies;
    private List<Coordinates> walls;
    private List<Invader> invaders;
    private List<ObjectDetect> lastObjectsFound;
    private List<Coordinates> lastWalls;


    public Commander() { }

    public Commander(int height, int width, List<ObjectDetect> lastObjectsFound, List<Coordinates> lastWalls, Area area, Coordinates actualPosition, Coordinates lastPosition) {
        this.maze = new String[height][width];
        this.area = area;
        this.actualPosition = actualPosition;
        this.lastPosition = lastPosition;
        this.lastObjectsFound = lastObjectsFound;
        this.lastWalls = lastWalls;
    }

    public String getDecision() {
        setObjectsToMaze();


        return "left";
    }

    public String[][] getMaze() {
        return maze;
    }

    public void setMaze(String[][] maze) {
        this.maze = maze;
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

    private void setObjectsToMaze() {

        log.info("Put objects detected to maze");
        log.info("Put invaders (neutral and enemy)");
        for(Invader invader :  invaders) {
            maze[invader.getCordY()][invader.getCordX()] = (invader.isNeutral() ? CellType.INVADER_NEUTRAL : CellType.INVADER);
        }

        log.info("Put enemies");
        for(Coordinates enemy : enemies) {
            maze[enemy.getCordY()][enemy.getCordX()] = CellType.ENEMY;
        }

        log.info("Put walls");
        for(Coordinates wall :  walls) {
            maze[wall.getCordY()][wall.getCordX()] = CellType.WALL;
        }
    }
}
