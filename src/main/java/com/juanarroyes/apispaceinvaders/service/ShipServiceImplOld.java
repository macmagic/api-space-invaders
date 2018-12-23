package com.juanarroyes.apispaceinvaders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.dto.*;
import com.juanarroyes.apispaceinvaders.model.GameStatus;
import com.juanarroyes.apispaceinvaders.repository.GameStatusRepository;
import com.juanarroyes.apispaceinvaders.ship.Commander;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import com.juanarroyes.apispaceinvaders.utils.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ShipServiceImplOld {

    @Value("${app.logpath}")
    private String path;

    private String[][] maze;

    private Commander shipCommander;

    private Map<String, Storage> games = new HashMap<>();

    private List<Coordinates> pathUsed;

    private List<Coordinates> wallsFound;

    private ObjectMapper mapper;

    private Storage storage;

    private GameStatusRepository gameRepository;

    @Autowired
    public ShipServiceImplOld(GameStatusRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.storage = new Storage();
        this.mapper = new ObjectMapper();
    }

    public String moveShip(Stage stageData) {
        init(stageData);
        log.info("GameModel info: {player_id: " + stageData.getPlayerId() + ", height: " + stageData.getMazeSize().getHeight() + ", width: " + stageData.getMazeSize().getWidth() + "}");
        log.info("Discover maze area to get objects to calculate next action...");
        mazeDiscovery(stageData);
        log.info("The maze now is: \n" + MazeUtils.drawMazeToLog(maze));
        String move = shipCommander.getDecision();
        pathUsed = shipCommander.getPathUsed();
        log.info("Next action is: " + move);
        clearMaze();
        saveGame(stageData.getGameId(), stageData.getPlayerId(), maze.length, maze[0].length, new ArrayList<ObjectDetect>());
        shipCommander = null;
        return move;
    }


    /**
     *
     * @param stageData
     */
    private void init(Stage stageData) {
        String gameId = stageData.getPlayerId() + ":" + stageData.getGameId();

        log.info("Game ID is: " + gameId);

        GameStatus game = getGameById(gameId);
        if(game != null) {
            log.info("Game is not null, get data!");
            int height = game.getMazeHeight();
            int width = game.getMazeWidth();
            maze = new String[height][width];
           // wallsFound = mapper.readValues();
        } else {
            log.info("Game is null, new party");
            int height = stageData.getMazeSize().getHeight();
            int width = stageData.getMazeSize().getWidth();
            maze = new String[height][width];
            maze = MazeUtils.addLimitWalls(maze, CellType.WALL);
            pathUsed = new ArrayList<>();
        }
        shipCommander = new Commander(maze, stageData.getArea(), stageData.getActualPosition(), stageData.getPreviousPosition(), stageData.isFire(), pathUsed);
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

    public GameStatus getGameById(String id) {
        Optional<GameStatus> result = gameRepository.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    public void saveGame(String gameId, String playerId, int height, int width, List<ObjectDetect> objects) {
        try {
            GameStatus actualGame = new GameStatus();
            String id = playerId + ":" + gameId;
            actualGame.setId(id);
            actualGame.setMazeHeight(height);
            actualGame.setMazeWidth(width);
            String lastObjectsFound = mapper.writeValueAsString(objects);
            actualGame.setLastObjectsFound(lastObjectsFound);
            gameRepository.save(actualGame);
        } catch(JsonProcessingException e) {
            log.error("Error when try save data game to database: " + e.getMessage(), e);
        } catch(Exception e) {
            log.error("Unexpected exception in method saveGame", e);
        }
    }

    private void storageGame(String playerId, String[][] maze, List<Coordinates> pathUsed) {
        Storage storage = games.get(playerId);
        if(storage == null) {
            storage = new Storage();
        }

        /*storage.setHeight(maze.length);
        storage.setWidth(maze[0].length);
        storage.setMaze(maze);
        storage.setPathUsed(pathUsed);*/
        games.put(playerId, storage);
    }
}
