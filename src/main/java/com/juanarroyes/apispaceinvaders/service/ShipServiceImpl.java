package com.juanarroyes.apispaceinvaders.service;

import com.juanarroyes.apispaceinvaders.dto.*;
import com.juanarroyes.apispaceinvaders.model.Savegame;
import com.juanarroyes.apispaceinvaders.request.MoveRequest;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class ShipServiceImpl {

    private static final String CELL_WALL = "W";
    private static final String CELL_ENEMY = "E";
    private static final String CELL_INVADER = "I";
    private static final String CELL_INVADER_NEUTRAL = "N";
    private static final String CELL_POSITION = "P";

    private String[][] maze;
    private String[][] viewArea;

    private boolean setup = false;

    public String moveShip(Stage stageData) {
        if(!setup) {
            init(stageData);
        }
        areaDiscovery(stageData);
        MazeUtils.drawMaze(maze);
        List<String> moves = new ArrayList<>(Arrays.asList("up", "right", "left", "down"));
        return  moves.get(new Random().nextInt(4));
    }

    private void init(Stage stageData) {
        int height = stageData.getMazeSize().getHeight();
        int width = stageData.getMazeSize().getWidth();
        maze = new String[width][height];
        setup = true;
    }

    private void areaDiscovery(Stage stageData) {
        Area area = stageData.getArea();
        int lengthX = area.getCordX2() - area.getCordX1();
        int lengthY = area.getCordY2() - area.getCordY1();

        lengthX = (lengthX < 0) ? (lengthX * -1) : lengthX;
        lengthY = (lengthY < 0) ? (lengthY * -1) : lengthY;

        viewArea = new String[lengthX][lengthY];

        viewArea[stageData.getActualPosition().getCordX()][stageData.getActualPosition().getCordY()] = CELL_POSITION;

        // Add walls to area
        List<Coordinates> walls = stageData.getWalls();
        for(Coordinates item : walls) {
            viewArea[item.getCordX()][item.getCordY()] = CELL_WALL;
        }

        List<Invader> invaders = stageData.getInvaders();
        for(Invader invader: invaders) {
            viewArea[invader.getCordX()][invader.getCordY()] = (invader.isNeutral()) ? CELL_INVADER_NEUTRAL : CELL_INVADER;
        }

        List<Coordinates> enemies = stageData.getEnemies();
        for(Coordinates enemy : enemies) {
            viewArea[enemy.getCordX()][enemy.getCordY()] = CELL_ENEMY;
        }
        maze = MazeUtils.mazeUpdate(maze, viewArea, area);
    }

    public void savegame(String gameId, String playerId, String[][] maze) {
        Savegame savegame = new Savegame();
        savegame.setGameId(gameId);
        savegame.setPlayerId(playerId);

//        savegame.setMaze();
    }
}
