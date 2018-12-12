package com.juanarroyes.apispaceinvaders.service;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.dto.*;
import com.juanarroyes.apispaceinvaders.ship.Commander;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import com.juanarroyes.apispaceinvaders.utils.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ShipServiceImpl {

    @Value("${app.logpath}")
    private String path;

    private String[][] maze;

    private Commander shipCommander;

    private Map<String, Storage> games = new HashMap<>();

    public ShipServiceImpl() {
    }

    public String moveShip(Stage stageData) {
        init(stageData);
        mazeDiscovery(stageData);
        MazeUtils.drawMaze(maze);
        String move = shipCommander.getDecision();
        System.out.println("Move is: " + move);
        clearMaze();
        storageGame(stageData.getPlayerId(), maze);
        shipCommander = null;
        return move;
    }

    /**
     *
     * @param stageData
     */
    private void init(Stage stageData) {
        Storage storage = games.get(stageData.getGameId());
        if(storage != null) {
            maze = storage.getMaze();
        } else {
            int height = stageData.getMazeSize().getHeight();
            int width = stageData.getMazeSize().getWidth();
            maze = new String[height][width];
            maze = MazeUtils.addLimitWalls(maze, CellType.WALL);
        }

        shipCommander = new Commander(maze, stageData.getArea(), stageData.getActualPosition(), stageData.getPreviousPosition(), stageData.isFire());
    }

    private void mazeDiscovery(Stage stageData) {
        Area area = stageData.getArea();
        maze[stageData.getActualPosition().getCordY()][stageData.getActualPosition().getCordX()] = CellType.POSITION;

        maze[stageData.getPreviousPosition().getCordY()][stageData.getPreviousPosition().getCordX()] = CellType.LAST_POSITION;

        // Add walls to area
        List<Coordinates> walls = stageData.getWalls();
        for(Coordinates item : walls) {
            maze[item.getCordY()][item.getCordX()] = CellType.WALL;
        }

        // Add invaders on maze
        List<Invader> invaders = stageData.getInvaders();
        for(Invader invader: invaders) {
            maze[invader.getCordY()][invader.getCordX()] = (invader.isNeutral()) ? CellType.INVADER_NEUTRAL : CellType.INVADER;
        }

        // Add players on maze
        List<Coordinates> enemies = stageData.getEnemies();
        for(Coordinates enemy : enemies) {
            maze[enemy.getCordY()][enemy.getCordX()] = CellType.ENEMY;
        }

        // Add area viewed cells to calculation propusal
        for(int y = area.getCordY1(); y <= area.getCordY2(); y++) {
            for(int x = area.getCordX1(); x <= area.getCordX2(); x++) {
                maze[y][x] = (maze[y][x] == null) ? CellType.VIEWED : maze[y][x];
            }
        }
    }

    private void clearMaze() {
        int rowCount = maze.length;
        int colCount = maze[0].length;

        for(int y = 0; y < rowCount; y++) {
            for(int x = 0; x < colCount; x++) {
                maze[y][x] = (maze[y][x] != null && maze[y][x].equals(CellType.WALL)) ? CellType.WALL : null;
            }
        }
    }

    private List<Map<String, Coordinates>> storeObjectsFromMaze(String[][] maze) {
        int rowCount = maze.length;
        int colCount = maze[0].length;
        List<Map<String, Coordinates>> objects = new ArrayList<>();

        for(int y = 0; y < rowCount; y++) {
            for(int x = 0; x < colCount; x++) {
                if(maze[y][x] != null && maze[y][x].equals(CellType.WALL)) {
                    Map<String, Coordinates> item = new HashMap<>();
                    item.put(CellType.WALL, new Coordinates(y, x));
                    objects.add(item);
                }
            }
        }

        return objects;
    }

    private void storageGame(String playerId, String[][] maze) {
        Storage storage = games.get(playerId);
        if(storage == null) {
            storage = new Storage();
        }

        storage.setHeight(maze.length);
        storage.setWidth(maze[0].length);
        storage.setMaze(maze);
        games.put(playerId, storage);
    }
}
