package com.juanarroyes.apispaceinvaders.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;
import com.juanarroyes.apispaceinvaders.dto.ObjectDetect;
import com.juanarroyes.apispaceinvaders.dto.Stage;
import com.juanarroyes.apispaceinvaders.model.GameStatus;
import com.juanarroyes.apispaceinvaders.repository.GameStatusRepository;
import com.juanarroyes.apispaceinvaders.ship.Commander;
import com.juanarroyes.apispaceinvaders.ship.CommanderOld;
import com.juanarroyes.apispaceinvaders.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ShipServiceImpl implements ShipService {

    private GameStatusRepository gameStatusRepository;

    private ObjectMapper mapper;

    public ShipServiceImpl(GameStatusRepository gameStatusRepository) {
        this.gameStatusRepository = gameStatusRepository;
        this.mapper = new ObjectMapper();
    }

    @Override
    public String moveShip(Stage stageData) {
        String move = null;
        try {
            String gameStatusId = Utils.getGameStatusId(stageData.getGameId(), stageData.getPlayerId());
            GameStatus gameStatus = getGameStatusById(gameStatusId);
            int height;
            int width;
            List<ObjectDetect> lastObjectsFound = new ArrayList<>();
            List<Coordinates> lastWalls = new ArrayList<>();

            if (gameStatus != null) {
                log.info("Retry game from database");
                height = gameStatus.getMazeHeight();
                width = gameStatus.getMazeWidth();
                lastObjectsFound = mapper.readValue(gameStatus.getLastObjectsFound(), mapper.getTypeFactory().constructCollectionType(List.class, ObjectDetect.class));
                lastWalls = mapper.readValue(gameStatus.getWallsFound(), mapper.getTypeFactory().constructCollectionType(List.class, Coordinates.class));
            } else {
                height = stageData.getMazeSize().getHeight();
                width = stageData.getMazeSize().getWidth();
            }

            Commander shipCommander = new Commander(height, width, lastObjectsFound, lastWalls, stageData.getArea(), stageData.getActualPosition(), stageData.getPreviousPosition(), stageData.isFire());
            shipCommander.setEnemies(stageData.getEnemies());
            shipCommander.setInvaders(stageData.getInvaders());
            shipCommander.setWalls(stageData.getWalls());
            move = shipCommander.getDecision();
            saveGameStatus(gameStatusId, height, width, lastObjectsFound, shipCommander.getWalls());
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Error when read values from json string", e);
        } catch (IOException e) {
            log.error("Error when read values from IOException", e);
        } catch (Exception e) {
            log.error("Unexpected error in method moveShip", e);
        }

        if(move == null) {
            move = Commander.randomMove();
        }
        return move;
    }

    /**
     *
     * @param id
     * @return
     */
    public GameStatus getGameStatusById(String id) {
        Optional<GameStatus> result = gameStatusRepository.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    /**
     *
     * @param id
     * @param height
     * @param width
     * @param objectsDetect
     * @param walls
     */
    public void saveGameStatus(String id, int height, int width, List<ObjectDetect> objectsDetect, List<Coordinates> walls) {
        try {
            GameStatus gameStatus = new GameStatus();
            gameStatus.setId(id);
            gameStatus.setMazeHeight(height);
            gameStatus.setMazeWidth(width);
            gameStatus.setLastObjectsFound(mapper.writeValueAsString(objectsDetect));
            gameStatus.setWallsFound(mapper.writeValueAsString(walls));
            gameStatusRepository.save(gameStatus);
            log.info("Save game with id [" + id + "] ok");
        } catch(JsonProcessingException e) {
            log.error("Error when process object to json", e);
        } catch(Exception e) {
            log.error("Unexpected error in method saveGameStatus", e);
        }
    }
}
