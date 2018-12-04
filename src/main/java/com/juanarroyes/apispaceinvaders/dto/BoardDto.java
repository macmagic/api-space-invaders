package com.juanarroyes.apispaceinvaders.dto;

import java.util.List;

public class BoardDto {

    private MazeSizeDto size;

    private List<WallDto> walls;

    public BoardDto() {

    }

    public BoardDto(MazeSizeDto size, List<WallDto> walls) {
        this.size = size;
        this.walls = walls;
    }

    public MazeSizeDto getSize() {
        return size;
    }

    public void setSize(MazeSizeDto size) {
        this.size = size;
    }

    public List<WallDto> getWalls() {
        return walls;
    }

    public void setWalls(List<WallDto> walls) {
        this.walls = walls;
    }
}
