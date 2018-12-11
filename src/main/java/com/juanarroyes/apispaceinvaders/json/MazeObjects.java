package com.juanarroyes.apispaceinvaders.json;

import com.juanarroyes.apispaceinvaders.dto.Coordinates;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MazeObjects implements Serializable {

    List<Map<String, Coordinates>> objects;

    public MazeObjects() {

    }

    public MazeObjects(List<Map<String, Coordinates>> objects) {
        this.objects = objects;
    }

    public void setObjects(List<Map<String, Coordinates>> objects) {
        this.objects = objects;
    }

    public List<Map<String, Coordinates>> getObjects() {
        return objects;
    }
}
