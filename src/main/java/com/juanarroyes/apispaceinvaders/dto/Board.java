package com.juanarroyes.apispaceinvaders.dto;

import java.util.List;

public class Board {

    private MazeSize size;

    private List<Coordinates> walls;

    public Board() {

    }

    public Board(MazeSize size, List<Coordinates> walls) {
        this.size = size;
        this.walls = walls;
    }

    public MazeSize getSize() {
        return size;
    }

    public void setSize(MazeSize size) {
        this.size = size;
    }

    public List<Coordinates> getWalls() {
        return walls;
    }

    public void setWalls(List<Coordinates> walls) {
        this.walls = walls;
    }
}
