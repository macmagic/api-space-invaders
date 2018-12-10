package com.juanarroyes.apispaceinvaders.service;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.dto.*;
import com.juanarroyes.apispaceinvaders.exception.SaveGameNotFoundException;
import com.juanarroyes.apispaceinvaders.model.SaveGame;
import com.juanarroyes.apispaceinvaders.repository.SaveGameRepository;
import com.juanarroyes.apispaceinvaders.utils.DetectorUtils;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import com.juanarroyes.apispaceinvaders.utils.ShipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.util.*;

@Slf4j
@Service
public class ShipServiceImpl {

    private String[][] maze;

    private SaveGameRepository saveGameRepository;

    private boolean setup = false;

    @Autowired
    public ShipServiceImpl(SaveGameRepository saveGameRepository) {
        this.saveGameRepository = saveGameRepository;
    }

    public String moveShip(Stage stageData) {
        try {
            SaveGame saveGame = getSaveGameByGameIdAndPlayer(stageData.getGameId(), stageData.getPlayerId());
            byte[] serializedMaze = saveGame.getMaze().getBytes();
            maze = (String[][]) SerializationUtils.deserialize(serializedMaze);
        } catch(SaveGameNotFoundException e) {
            init(stageData);
        }

        mazeDiscovery(stageData);
        MazeUtils.drawMaze(maze);
        String move = getDecision(stageData.getArea(), stageData.getActualPosition(), stageData.getPreviousPosition(), stageData.isFire());
        System.out.println("Move is: " + move);
        clearMaze();
        saveSaveGame(stageData.getGameId(), stageData.getPlayerId(), maze);
        return move;
    }

    public SaveGame getSaveGameByPlayerIdAndGameId(String gameId, String playerId) throws SaveGameNotFoundException {
        Optional<SaveGame> result = saveGameRepository.findOneByGameIdAndPlayerId(gameId, playerId);
        if(!result.isPresent()) {
            throw new SaveGameNotFoundException("Cannot find this save-game by game-id: " + gameId + " and player-id: " + playerId);
        }
        return result.get();
    }

    private void init(Stage stageData) {
        int height = stageData.getMazeSize().getHeight();
        int width = stageData.getMazeSize().getWidth();
        maze = new String[height][width];
        maze = MazeUtils.addLimitWalls(maze, CellType.WALL);
        setup = true;
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
    }

    private String getDecision(Area area, Coordinates actualPosition, Coordinates lastPosition, boolean fire) {
        String move = null;
        Coordinates bestEnemyFire = ShipUtils.getTargetDirectShot(maze, area, actualPosition, CellType.ENEMY);
        Coordinates bestInvaderFire = ShipUtils.getTargetDirectShot(maze, area, actualPosition, CellType.INVADER);

        if(fire && bestEnemyFire != null) {
            String direction = DetectorUtils.directionOfTarget(actualPosition, bestEnemyFire);
            move = getMovement(direction, fire);
        } else if(fire && bestInvaderFire != null) {
            String direction = DetectorUtils.directionOfTarget(actualPosition, bestInvaderFire);
            move = getMovement(direction, fire);
        } else {
            move = ShipUtils.getShipTripDirection(maze, area, actualPosition, lastPosition);
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


    public void saveSaveGame(String gameId, String playerId, String[][] maze) {
        SaveGame saveGame = new SaveGame();
        saveGame.setGameId(gameId);
        saveGame.setPlayerId(playerId);
        byte[] mazeSerializable = SerializationUtils.serialize(maze);
        saveGame.setMaze(mazeSerializable.toString());
        saveGameRepository.save(saveGame);
    }

    public SaveGame getSaveGameByGameIdAndPlayer(String gameId, String playerId) throws SaveGameNotFoundException {
        Optional<SaveGame> result = saveGameRepository.findOneByGameIdAndPlayerId(gameId, playerId);
        if(!result.isPresent()) {
            throw new SaveGameNotFoundException("Not found");
        }
        return result.get();
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
}
