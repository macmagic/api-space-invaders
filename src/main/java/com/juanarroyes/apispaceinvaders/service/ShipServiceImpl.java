package com.juanarroyes.apispaceinvaders.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.dto.*;
import com.juanarroyes.apispaceinvaders.exception.SaveGameNotFoundException;
import com.juanarroyes.apispaceinvaders.json.MazeObjects;
import com.juanarroyes.apispaceinvaders.model.SaveGame;
import com.juanarroyes.apispaceinvaders.repository.SaveGameRepository;
import com.juanarroyes.apispaceinvaders.utils.DetectorUtils;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import com.juanarroyes.apispaceinvaders.utils.ShipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class ShipServiceImpl {

    @Value("${app.logpath}")
    private String path;

    private String[][] maze;

    private SaveGameRepository saveGameRepository;

    private ObjectMapper mapper;

    private Long saveGameId;

    private Environment env;

    @Autowired
    public ShipServiceImpl(SaveGameRepository saveGameRepository, Environment env) {
        this.saveGameRepository = saveGameRepository;
        this.mapper = new ObjectMapper();
        this.env = env;
    }

    public String moveShip(Stage stageData) {
        init(stageData);
        mazeDiscovery(stageData);
        MazeUtils.drawMaze(maze);
        String move = getDecision(stageData.getArea(), stageData.getActualPosition(), stageData.getPreviousPosition(), stageData.isFire());
        System.out.println("Move is: " + move);
        clearMaze();
        saveSaveGame(stageData.getGameId(), stageData.getPlayerId(), maze,saveGameId);
        return move;
    }


    /**
     *
     * @param stageData
     */
    private void init(Stage stageData) {

        SaveGame saveGame = null;

        try {
            saveGame = getSaveGameByGameIdAndPlayer(stageData.getGameId(), stageData.getPlayerId());
            saveGameId = saveGame.getId();
            MazeObjects objects = null;
            try {
                objects = mapper.readValue(saveGame.getMazeObjectsDiscovered(), MazeObjects.class);
            } catch(JsonParseException | JsonMappingException e) {
                log.error("Error when try read values from JSON", e);
                throw new SaveGameNotFoundException("Error when recovery the savegame info");
            }
            maze = restoreMazeFromSaveGame(saveGame.getMazeWidth(), saveGame.getMazeHeight(), objects.getObjects());
        } catch(IOException | SaveGameNotFoundException e) {
            log.error("Error when recovery the savegame data", e);
        }

        if(saveGame == null) {
            int height = stageData.getMazeSize().getHeight();
            int width = stageData.getMazeSize().getWidth();
            maze = new String[height][width];
            maze = MazeUtils.addLimitWalls(maze, CellType.WALL);
        }
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

        // Add area viewed for log proposal

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
        List<Coordinates> potentialThreads = DetectorUtils.getPotentialThreats(maze, actualPosition, 3);

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
        saveSaveGame(gameId, playerId, maze, null);
    }

    public void saveSaveGame(String gameId, String playerId, String[][] maze, Long saveGameId) {
        SaveGame saveGame = new SaveGame();
        if(saveGameId != null) {
            saveGame.setId(saveGameId);
        }
        saveGame.setGameId(gameId);
        saveGame.setPlayerId(playerId);
        saveGame.setMazeHeight(maze.length);
        saveGame.setMazeWidth(maze[0].length);

        ObjectMapper objectMapper = new ObjectMapper();
        MazeObjects mazeObjects = new MazeObjects(storeObjectsFromMaze(maze));
        String objectsSerialized = null;
        try {
            objectsSerialized = objectMapper.writeValueAsString(mazeObjects);
        } catch(JsonProcessingException e) {
            log.error("Error when convert object to json: " + e.getMessage(), e);
        }

        saveGame.setMazeObjectsDiscovered(objectsSerialized);
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

    private String[][] restoreMazeFromSaveGame(Integer width, Integer height, List<Map<String, Coordinates>> objectsDiscovered) {
        String[][] mazeRecovery = new String[height][width];

        for(Map<String, Coordinates> item : objectsDiscovered) {
            String cellType = (String) item.keySet().toArray()[0];
            Coordinates coordinates = item.get(cellType);
            mazeRecovery[coordinates.getCordY()][coordinates.getCordX()] = cellType;
        }
        return mazeRecovery;
    }
}
