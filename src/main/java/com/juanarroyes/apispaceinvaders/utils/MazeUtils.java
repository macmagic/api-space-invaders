package com.juanarroyes.apispaceinvaders.utils;

import com.juanarroyes.apispaceinvaders.dto.Area;

public class MazeUtils {

    public static void drawMaze(String[][] maze) {
        StringBuilder sb = new StringBuilder();
        int rowCount = maze.length;
        int colCount = maze[0].length;
        String cell = null;

        for(int i = 0; i<rowCount; i++) {
            sb.setLength(0);
            sb.append("| ");
            for(int j = 0; j<colCount; j++) {
                cell = (maze[i][j] == null) ? "-" : maze[i][j];
                sb.append(cell).append(" | ");
            }
            System.out.println(sb.toString());
        }

        System.out.println("-----------------------------------------------------");
    }
}
