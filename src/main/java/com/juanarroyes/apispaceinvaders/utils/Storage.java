package com.juanarroyes.apispaceinvaders.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanarroyes.apispaceinvaders.dto.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
@Slf4j
public class Storage {

    private static final String DEFAULT_FILENAME = "savegame.json";

    private static final String DEFAULT_PATH = "/tmp/spaceinvaders/";

    @Value("${app.savegamepath}")
    private String saveGamePath;

    private ObjectMapper mapper;

    public Storage() {
        this.mapper = new ObjectMapper();
    }

    public void saveGame(String playerId, Stage data) {
        try {
            String json = mapper.writeValueAsString(data);
            write(data.getPlayerId(), json);
        } catch (JsonProcessingException e) {
            log.error("Error when convert object to json string", e);
        }

    }

    private void write(String playerId, String json) {
        FileWriter fw = null;

        try {
            File directory = new File(DEFAULT_PATH);
            if(!directory.exists()) {
                directory.mkdir();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(json);

            String filename = DEFAULT_PATH + DEFAULT_FILENAME.replace("savegame", "savegame_" + playerId);
            log.info(filename);
            fw = new FileWriter(filename, false);
            fw.write(sb.toString());
            fw.close();
        } catch(IOException e) {
            log.error("Error when write file: " + e.getMessage(), e);
        } finally {
            try {
                if(fw != null) {
                    fw.close();
                }
            } catch(IOException e) {
                log.error("Error when try to close file: " + e.getMessage(), e);
            }
        }

    }
}
