package com.juanarroyes.apispaceinvaders.utils;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.constants.Moves;
import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class DetectorUtils {

    /**
     *
     * @param source
     * @param target
     * @return
     */
    public static int distanceOfTwoObjects(Coordinates source, Coordinates target) {
        int distance = 0;
        if(source.getCordX() == target.getCordX()) {
            distance = target.getCordY() - source.getCordY();
        } else if(source.getCordY() == target.getCordY()) {
            distance = target.getCordX() - source.getCordX();
        } else {
            double calcX = (double) (target.getCordX() - source.getCordX());
            calcX = (calcX < 0) ? calcX * -1 : calcX;

            double calcY = (double) (target.getCordY() - source.getCordY());
            calcY = (calcY < 0) ? calcY * -1 : calcY;

            double result = Math.sqrt(Math.pow(calcX, 2) + Math.pow(calcY, 2));
            distance = (int) Math.round(result);
        }
        return (distance < 0) ? distance * -1 : distance;
    }

    /**
     *
     * @param ship
     * @param target
     * @return
     */
    public static String directionOfTarget(Coordinates ship, Coordinates target) {
        if (ship.getCordY() == target.getCordY() && (ship.getCordX() - target.getCordX()) < 0) {
            return Moves.RIGHT;
        } else if (ship.getCordY() == target.getCordY() && (ship.getCordX() - target.getCordX()) > 0) {
            return Moves.LEFT;
        } else if (ship.getCordX() == target.getCordX() && (ship.getCordY() - target.getCordY()) > 0) {
            return Moves.UP;
        } else if (ship.getCordX() == target.getCordX() && (ship.getCordY() - target.getCordY()) < 0) {
            return Moves.DOWN;
        }
        return null;
    }

    public static List<Coordinates> getPotentialThreats(String[][] maze, Coordinates position, int distance) {
        int cordY1 = position.getCordY() - distance;
        int cordX1 = position.getCordX() - distance;
        int cordY2 = position.getCordY() + distance;
        int cordX2 = position.getCordX() + distance;
        List<Coordinates> threats = new ArrayList<>();

        for(int y = cordY1; y <= cordY2; y++) {
            for(int x = cordX1; x <= cordX2; x++) {
                String cellValue = maze[y][x];
                if(cellValue.equals(CellType.ENEMY) || cellValue.equals(CellType.INVADER)) {
                    threats.add(new Coordinates(y, x));
                }
            }
        }
        return threats;
    }

    public static String[] getAvailableMoves(String[][] maze, Coordinates actualPosition, String[] moves) {
        List<String> availiableMovesList = new ArrayList<>();

        for(String move :  moves) {
            if(checkMoveIsAvailable(maze, actualPosition, move)) {
                availiableMovesList.add(move);
            }
        }

        String[] availiableMovesArr = new String[availiableMovesList.size()];
        availiableMovesList.toArray(availiableMovesArr);
        return availiableMovesArr;
    }

    public static boolean checkMoveIsAvailable(String[][] maze, Coordinates actualPosition, String move) {
        switch(move) {
            case Moves.DOWN:
                return (!maze[actualPosition.getCordY()+1][actualPosition.getCordX()].equals(CellType.WALL));
            case Moves.UP:
                return (!maze[actualPosition.getCordY()-1][actualPosition.getCordX()].equals(CellType.WALL));
            case Moves.LEFT:
                return (!maze[actualPosition.getCordY()][actualPosition.getCordX()-1].equals(CellType.WALL));
            case Moves.RIGHT:
                return (!maze[actualPosition.getCordY()][actualPosition.getCordX()+1].equals(CellType.WALL));
            default:
                return false;
        }
    }

    public static String recommendedDirection(String[][] maze, Area area, Coordinates actualPosition, String[] moves) {
        int points = 0;
        int lastPoints = 0;
        String moveRecommended = null;

        int z1 = 0;
        int z2 = 0;
        int x = 0;
        int y = 0;

        for(String move :  moves) {
            switch(move) {
                case Moves.DOWN:
                    z1 = actualPosition.getCordY();
                    z2 = area.getCordY2();
                    break;
                case Moves.UP:
                    z1 = area.getCordY1();
                    z2 = actualPosition.getCordY();
                    break;
                case Moves.LEFT:
                    z1 = area.getCordX1();
                    z2 = actualPosition.getCordX();
                    break;
                case Moves.RIGHT:
                    z1 = actualPosition.getCordX();
                    z2 = area.getCordX2();
                    break;
            }

            if(move.equals(Moves.DOWN) || move.equals(Moves.UP)) {
                x = actualPosition.getCordX();
                for(y = z1; y <= z2; y++) {
                    if(maze[y][x].equals("V")) {
                        points++;
                    }
                }
                if(lastPoints < points) {
                    moveRecommended = move;
                }
            } else if(move.equals(Moves.LEFT) || move.equals(Moves.RIGHT)) {
                y = actualPosition.getCordY();
                for(x = z1; x <= z2; x++) {
                    if(maze[y][x].equals("V")) {
                        points++;
                    }
                }
                if(lastPoints < points) {
                    moveRecommended = move;
                }
            }
        }

        return moveRecommended;
    }
}
