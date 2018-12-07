package com.juanarroyes.apispaceinvaders.service;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.dto.*;
import com.juanarroyes.apispaceinvaders.exception.SavegameNotFoundException;
import com.juanarroyes.apispaceinvaders.model.Savegame;
import com.juanarroyes.apispaceinvaders.repository.SavegameRepository;
import com.juanarroyes.apispaceinvaders.request.MoveRequest;
import com.juanarroyes.apispaceinvaders.utils.CalculationsUtils;
import com.juanarroyes.apispaceinvaders.utils.DetectorUtils;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import com.juanarroyes.apispaceinvaders.utils.ShipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.*;

@Slf4j
@Service
public class ShipServiceImpl {

    /*private static final String CELL_WALL = "W";
    private static final String CELL_ENEMY = "E";
    private static final String CELL_INVADER = "I";
    private static final String CELL_INVADER_NEUTRAL = "N";
    private static final String CELL_POSITION = "P";
    private static final String CELL_VIEW = "V";*/

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
        String move = getDecision(stageData.getArea(), stageData.getActualPosition(), stageData.isFire());
        saveSaveGame(stageData.getGameId(), stageData.getPlayerId(), maze);
        return move;
    }

    private void init(Stage stageData) {
        int height = stageData.getMazeSize().getHeight();
        int width = stageData.getMazeSize().getWidth();
        maze = new String[width][height];
        setup = true;
    }

    private void mazeDiscovery(Stage stageData) {
        Area area = stageData.getArea();
        maze[stageData.getActualPosition().getCordX()][stageData.getActualPosition().getCordY()] = CellType.POSITION;

        // Add walls to area
        List<Coordinates> walls = stageData.getWalls();
        for(Coordinates item : walls) {
            maze[item.getCordY()][item.getCordX()] = CellType.WALL;
        }

        List<Invader> invaders = stageData.getInvaders();
        for(Invader invader: invaders) {
            maze[invader.getCordY()][invader.getCordX()] = (invader.isNeutral()) ? CellType.INVADER_NEUTRAL : CellType.INVADER;
        }

        List<Coordinates> enemies = stageData.getEnemies();
        for(Coordinates enemy : enemies) {
            maze[enemy.getCordY()][enemy.getCordX()] = CellType.ENEMY;
        }

        for(int y = area.getCordY1(); y <= area.getCordY2(); y++) {
            for(int x = area.getCordX1(); x <= area.getCordX2(); x++) {
                maze[y][x] = (maze[y][x] == null) ? CellType.VIEWED : maze[y][x];
            }
        }
        maze = mazeUpdate(area);
    }

    private String getDecision(Area area, Coordinates actualPosition, boolean fire) {
        String move = null;
        Coordinates bestEnemyFire = ShipUtils.getTargetDirectShot(maze, area, actualPosition, CellType.ENEMY);
        Coordinates bestInvaderFire = ShipUtils.getTargetDirectShot(maze, area, actualPosition, CellType.INVADER);

        if(bestEnemyFire != null && bestInvaderFire != null && !fire) {
            // evitar morir aqui!
        } else if(bestEnemyFire != null){
            String direction = DetectorUtils.directionOfTarget(actualPosition, bestEnemyFire);
            move = getMovement(direction, fire);
        } else if(bestInvaderFire != null) {
            String direction = DetectorUtils.directionOfTarget(actualPosition, bestInvaderFire);
            move = getMovement(direction, fire);
        }
        return move;
    }

    private String getMovement(String direction, boolean fire) {
        String movement = "";
        if(fire) {
            movement = "fire-";
        }
        return movement + direction;
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

        savegame = (!result.isPresent()) ? new Savegame() : result.get();

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

        for(int y = 0; y < rowCount; y++) {
            for(int x = 0; x < colCount; x++) {
                if((y < area.getCordX1() || y > area.getCordX2()) || (x < area.getCordY1() || x > area.getCordY2())) {
                    maze[y][x] = (maze[y][x] != null && maze[y][x].equals(CellType.WALL)) ? CellType.WALL : null;
                }
            }
        }
        return maze;
    }
}
