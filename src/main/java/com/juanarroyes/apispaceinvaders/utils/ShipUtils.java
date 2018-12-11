package com.juanarroyes.apispaceinvaders.utils;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.constants.Moves;
import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ShipUtils {

    public static String getShipTripDirection(String[][] maze, Area area, Coordinates actualPosition, Coordinates lastPosition) {

        String lastDirection = DetectorUtils.directionOfTarget(lastPosition, actualPosition);
        String enemyDirection = runAwayFromEnemies(maze, actualPosition);
        String[] moves = {Moves.DOWN, Moves.UP, Moves.LEFT, Moves.RIGHT};
        String[] availableMoves = DetectorUtils.getAvailableMoves(maze, actualPosition, moves);

        String moveRecommended = DetectorUtils.getRecommendedDirection(maze, area, actualPosition, availableMoves);

        if(moveRecommended != null) {
            return moveRecommended;
        } else if(lastDirection != null && Arrays.stream(availableMoves).anyMatch(lastDirection::equals)) {
            return lastDirection;
        } else if(availableMoves.length > 0){
            return availableMoves[new Random().nextInt(availableMoves.length)];
        } else {
            return moves[new Random().nextInt(moves.length)];
        }
    }

    public static Coordinates getTargetDirectShot(String[][] maze, Area area, Coordinates actualPosition, String targetType) {
        Coordinates enemy = null;
        List<Coordinates> coordinatesList = new ArrayList<>();

        for(int x = area.getCordX1(); x <= area.getCordX2(); x++) {
            if(maze[actualPosition.getCordY()][x] != null && maze[actualPosition.getCordY()][x].equals(targetType)) {
                coordinatesList.add(new Coordinates(actualPosition.getCordY(), x));
            } else if(maze[actualPosition.getCordY()][x] != null && maze[actualPosition.getCordY()][x].equals(CellType.WALL)) {
                if(actualPosition.getCordX() < x) {
                    break;
                } else if(!coordinatesList.isEmpty()) {
                    coordinatesList.remove(coordinatesList.size()-1);
                }
            }
        }

        for(int y = area.getCordY1(); y <= area.getCordY2(); y++) {
            if(maze[y][actualPosition.getCordX()] != null && maze[y][actualPosition.getCordX()].equals(targetType)) {
                coordinatesList.add(new Coordinates(y, actualPosition.getCordX()));
            } else if(maze[y][actualPosition.getCordX()] != null && maze[y][actualPosition.getCordX()].equals(CellType.WALL)) {
                if(actualPosition.getCordY() < y) {
                    break;
                } else if(!coordinatesList.isEmpty()) {
                    coordinatesList.remove(coordinatesList.size()-1);
                }
            }
        }

        for(Coordinates item : coordinatesList) {
            int distanceOfEnemy = DetectorUtils.distanceOfTwoObjects(actualPosition, item);

            if(enemy == null || DetectorUtils.distanceOfTwoObjects(actualPosition, enemy) > distanceOfEnemy) {
                enemy = item;
            }
            System.out.println("Distance for " + item.toString() + " of position " + actualPosition.toString() + " is " + String.valueOf(distanceOfEnemy));
        }

        System.out.println("The enemy is: " + ((enemy != null) ? enemy.toString() : "null"));
        return enemy;
    }

    public static String runAwayFromEnemies(String[][] maze, Coordinates actualPosition) {
        String enemyDirection = null;
        int lastDistance = 0;

        List<Coordinates> enemies = DetectorUtils.getPotentialThreats(maze, actualPosition, 2);
        for(Coordinates enemy : enemies) {
            int distance = DetectorUtils.distanceOfTwoObjects(actualPosition, enemy);
            String direction = DetectorUtils.directionOfTarget(actualPosition, enemy);

            if(lastDistance == 0) {
                lastDistance = distance;
                enemyDirection = direction;
            } else if(lastDistance > distance) {
                lastDistance = distance;
                enemyDirection = direction;
            }
        }
        return enemyDirection;
    }
}
