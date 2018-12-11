package com.juanarroyes.apispaceinvaders.ship;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.constants.Moves;
import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;
import com.juanarroyes.apispaceinvaders.utils.DetectorUtils;
import javafx.scene.control.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Commander {

    private static final String[] MOVES = { Moves.DOWN, Moves.UP, Moves.LEFT, Moves.RIGHT };

    private String[][] maze;
    private Area area;
    private Coordinates actualPosition;
    private Coordinates lastPosition;
    private boolean fire;

    public Commander() {

    }

    public Commander(String[][] maze, Area area, Coordinates actualPosition, Coordinates lastPosition, boolean fire) {
        this.maze = maze;
        this.area = area;
        this.actualPosition = actualPosition;
        this.lastPosition = lastPosition;
        this.fire = fire;
    }

    public String getDecision() {
        String move = null;
        Coordinates bestEnemyFire = getTargetDirectShot(CellType.ENEMY);
        Coordinates bestInvaderFire = getTargetDirectShot(CellType.INVADER);
        List<Coordinates> potentialThreads = Detector.getPotentialThreats(maze, actualPosition, 3);

        if(fire && bestEnemyFire != null) {
            String direction = Detector.directionOfTarget(actualPosition, bestEnemyFire);
            move = getMovement(direction, fire);
        } else if(fire && bestInvaderFire != null) {
            String direction = Detector.directionOfTarget(actualPosition, bestInvaderFire);
            move = getMovement(direction, fire);
        } else if(fire && !potentialThreads.isEmpty()) {
            Coordinates enemy = Detector.followBestEnemy(maze, actualPosition, potentialThreads);
            move = Detector.directionOfTarget(actualPosition, enemy);
        } else {
            move = getShipTripDirection();
        }
        return move;

    }

    private String getShipTripDirection() {

        String lastDirection = Detector.directionOfTarget(lastPosition, actualPosition);
        String enemyDirection = runAwayFromEnemies(maze, actualPosition);
        String[] availableMoves = Detector.getAvailableMoves(maze, actualPosition, MOVES);
        //String moveFollow = Detector.directionOfTarget(actualPosition, Detector.getBestEnemyToFollow(maze, actualPosition, Detector.getPotentialThreats(maze, actualPosition, 2)));
        String moveRecommended = Detector.getRecommendedDirection(maze, area, actualPosition, availableMoves);

        if(moveRecommended != null) {
            return moveRecommended;
        } else if(lastDirection != null && Arrays.stream(availableMoves).anyMatch(lastDirection::equals)) {
            return lastDirection;
        } else if(availableMoves.length > 0){
            return availableMoves[new Random().nextInt(availableMoves.length)];
        } else {
            return MOVES[new Random().nextInt(MOVES.length)];
        }
    }

    public static String getMovePersecution(String[][] maze, Area area, Coordinates actualPosition) {
        List<Coordinates> potentialThreats = Detector.getPotentialThreats(maze, actualPosition, 4);
        return null;
    }

    private Coordinates getTargetDirectShot(String targetType) {
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
            int distanceOfEnemy = Detector.distanceOfTwoObjects(actualPosition, item);

            if(enemy == null || Detector.distanceOfTwoObjects(actualPosition, enemy) > distanceOfEnemy) {
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

        List<Coordinates> enemies = Detector.getPotentialThreats(maze, actualPosition, 2);
        for(Coordinates enemy : enemies) {
            int distance = Detector.distanceOfTwoObjects(actualPosition, enemy);
            String direction = Detector.directionOfTarget(actualPosition, enemy);

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

    private static String getMovement(String direction, boolean fire) {
        String movement = "";
        if(fire) {
            movement = "fire-";
        }
        return movement + direction;
    }
}