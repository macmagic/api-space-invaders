package com.juanarroyes.apispaceinvaders.utils;

import com.juanarroyes.apispaceinvaders.dto.Area;

public class MazeUtils {

    public static final String CELL_WALL = "W";
    public static final String CELL_ENEMY = "E";
    public static final String CELL_INVADER = "I";
    public static final String CELL_INVADER_NEUTRAL = "N";
    public static final String CELL_POSITION = "P";
    public static final String CELL_VIEW = "V";

    public static void drawMaze(String[][] maze) {
        StringBuilder sb = new StringBuilder();
        int rowCount = maze.length;
        int colCount = maze[0].length;
        String cell = null;

        for(int y = 0; y < rowCount; y++) {
            sb.setLength(0);
            sb.append("| ");
            for(int x = 0; x < colCount; x++) {
                cell = (maze[y][x] == null) ? "-" : maze[y][x];
                sb.append(cell).append(" | ");
            }
            System.out.println(sb.toString());
        }

        System.out.println("-----------------------------------------------------");
    }
}
