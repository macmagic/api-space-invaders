package com.juanarroyes.apispaceinvaders.utils;

public class Storage {

    private int width;

    private int height;

    private String[][] maze;

    public Storage() {

    }

    public Storage(int width, int height, String[][] maze) {
        this.width = width;
        this.height = height;
        this.maze = maze;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String[][] getMaze() {
        return maze;
    }

    public void setMaze(String[][] maze) {
        this.maze = maze;
    }
}
