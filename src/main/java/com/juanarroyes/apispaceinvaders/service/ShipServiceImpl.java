package com.juanarroyes.apispaceinvaders.service;

import com.juanarroyes.apispaceinvaders.dto.*;
import com.juanarroyes.apispaceinvaders.exception.SavegameNotFoundException;
import com.juanarroyes.apispaceinvaders.model.Savegame;
import com.juanarroyes.apispaceinvaders.repository.SavegameRepository;
import com.juanarroyes.apispaceinvaders.request.MoveRequest;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.*;

@Slf4j
@Service
public class ShipServiceImpl {

    private static final String CELL_WALL = "W";
    private static final String CELL_ENEMY = "E";
    private static final String CELL_INVADER = "I";
    private static final String CELL_INVADER_NEUTRAL = "N";
    private static final String CELL_POSITION = "P";
    private static final String CELL_VIEW = "V";

    private String[][] maze;
    private String[][] viewArea;

    private SavegameRepository savegameRepository;

    private boolean setup = false;

    @Autowired
    public ShipServiceImpl(SavegameRepository savegameRepository) {
        this.savegameRepository = savegameRepository;
    }

    public String moveShip(Stage stageData) {
        if(!setup) {
            init(stageData);
        }
        mazeDiscovery(stageData);
        MazeUtils.drawMaze(maze);
        getDecision();
        saveSaveGame(stageData.getGameId(), stageData.getPlayerId(), maze);
        List<String> moves = new ArrayList<>(Arrays.asList("up", "right", "left", "down"));
        return  moves.get(new Random().nextInt(4));
    }

    private void init(Stage stageData) {
        int height = stageData.getMazeSize().getHeight();
        int width = stageData.getMazeSize().getWidth();
        maze = new String[width][height];
        setup = true;
    }

    private void mazeDiscovery(Stage stageData) {
        Area area = stageData.getArea();
        maze[stageData.getActualPosition().getCordX()][stageData.getActualPosition().getCordY()] = CELL_POSITION;

        // Add walls to area
        List<Coordinates> walls = stageData.getWalls();
        for(Coordinates item : walls) {
            maze[item.getCordX()][item.getCordY()] = CELL_WALL;
        }

        List<Invader> invaders = stageData.getInvaders();
        for(Invader invader: invaders) {
            maze[invader.getCordX()][invader.getCordY()] = (invader.isNeutral()) ? CELL_INVADER_NEUTRAL : CELL_INVADER;
        }

        List<Coordinates> enemies = stageData.getEnemies();
        for(Coordinates enemy : enemies) {
            maze[enemy.getCordX()][enemy.getCordY()] = CELL_ENEMY;
        }

        for(int i = area.getCordX1(); i <= area.getCordX2(); i++) {
            for(int j = area.getCordY1(); j <= area.getCordY2(); j++) {
                maze[i][j] = (maze[i][j] == null) ? CELL_VIEW : maze[i][j];
            }
        }

        maze = mazeUpdate(area);
    }

    private void areaDiscovery(Stage stageData) {
        Area area = stageData.getArea();
        int lengthX = area.getCordX2() - area.getCordX1();
        int lengthY = area.getCordY2() - area.getCordY1();

        lengthX = (lengthX < 0) ? (lengthX * -1) : lengthX;
        lengthY = (lengthY < 0) ? (lengthY * -1) : lengthY;

        viewArea = new String[lengthX+1][lengthY+1];

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
    }

    private String getDecision() {
        //getEnemyAvailable();
        return null;
    }

    private Coordinates getEnemyDirectShot(Area area, Coordinates actualPosition) {
        for(int x = area.getCordX1(); x<=area.getCordX2(); x++) {
            //if(maze[x][actualPosition.getCordY()]
        }
        return null;

    }

    public void saveSaveGame(String gameId, String playerId, String[][] maze) {
        Savegame savegame;
        Optional<Savegame> result = savegameRepository.findOneByGameIdAndPlayerId(gameId, playerId);
        if(result.isPresent()) {
            savegame = result.get();
        } else {
            savegame = new Savegame();
        }
        savegame.setGameId(gameId);
        savegame.setPlayerId(playerId);
        byte[] mazeSerializable = SerializationUtils.serialize(maze);
        savegame.setMaze(mazeSerializable.toString());
        savegameRepository.save(savegame);
    }

    public Savegame getSaveGameByGameIdAndPlayer(String gameId, String playerId) throws SavegameNotFoundException {
        Optional<Savegame> result = savegameRepository.findOneByGameIdAndPlayerId(gameId, playerId);
        if(!result.isPresent()) {
            throw new SavegameNotFoundException("Not found");
        }
        return result.get();
    }

    private String[][] mazeUpdate(Area area) {
        int rowCount = maze.length;
        int colCount = maze[0].length;

        for(int i = 0; i < rowCount; i++) {
            for(int j = 0; j < colCount; j++) {
                if((i < area.getCordX1() || i > area.getCordX2()) || (j < area.getCordY1() || j > area.getCordY2())) {
                    maze[i][j] = (maze[i][j] != null && maze[i][j].equals(CELL_WALL)) ? CELL_WALL : null;
                }
            }
        }
        return maze;
    }
}
