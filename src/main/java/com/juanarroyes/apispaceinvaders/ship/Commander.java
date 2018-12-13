package com.juanarroyes.apispaceinvaders.ship;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.constants.Moves;
import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
public class Commander {

    private static final String[] MOVES = { Moves.DOWN, Moves.UP, Moves.LEFT, Moves.RIGHT };

    private static final int DEFAULT_DISTANCE_OF_DETECTION = 3;

    private String[][] maze;
    private Area area;
    private Coordinates actualPosition;
    private Coordinates lastPosition;
    private boolean fire;
    private List<Coordinates> pathUsed;

    private List<Coordinates> potentialEnemies;

    public Commander() {

    }

    public Commander(String[][] maze, Area area, Coordinates actualPosition, Coordinates lastPosition, boolean fire, List<Coordinates> pathUsed) {
        this.maze = maze;
        this.area = area;
        this.actualPosition = actualPosition;
        this.lastPosition = lastPosition;
        this.fire = fire;
        this.potentialEnemies = new ArrayList<>();
        this.pathUsed = pathUsed;
    }

    public String getDecision() {
        String move;
        Coordinates bestEnemyFire = getTargetDirectShot(CellType.ENEMY);
        Coordinates bestInvaderFire = getTargetDirectShot(CellType.INVADER);
        potentialEnemies = Detector.getPotentialThreats(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);

        if(fire && bestEnemyFire != null) {
            String direction = Detector.directionOfTarget(actualPosition, bestEnemyFire);
            move = getMovement(direction, fire);
        } else if(fire && bestInvaderFire != null) {
            String direction = Detector.directionOfTarget(actualPosition, bestInvaderFire);
            move = getMovement(direction, fire);
        } else if(fire && !potentialEnemies.isEmpty()) {
            Coordinates enemy = Detector.followBestEnemy(maze, actualPosition, potentialEnemies);
            move = Detector.directionOfTarget(actualPosition, enemy);
        } else {
            move = getShipTripDirection();
        }

        if(move != null && getNextCoordsByMove(move, actualPosition) != null) {
            pathUsed.add(getNextCoordsByMove(move, actualPosition));
        }
        return move;
    }

    private Coordinates getNextCoordsByMove(String move, Coordinates actualPosition) {
        switch(move) {
            case Moves.DOWN:
                return new Coordinates(actualPosition.getCordY()+1, actualPosition.getCordX());
            case Moves.UP:
                return new Coordinates(actualPosition.getCordY()-1, actualPosition.getCordX());
            case Moves.LEFT:
                return new Coordinates(actualPosition.getCordY(), actualPosition.getCordX()-1);
            case Moves.RIGHT:
                return new Coordinates(actualPosition.getCordY(), actualPosition.getCordX()+1);
        }
        return null;
    }

    private String getShipTripDirection() {

        String lastDirection = Detector.directionOfTarget(lastPosition, actualPosition);
        String enemyDirection = runAwayFromEnemies();
        String[] availableMoves = Detector.getAvailableMoves(maze, actualPosition, MOVES, enemyDirection);
        String moveRecommended = Detector.getRecommendedDirection(maze, area, actualPosition, availableMoves);
        int pointsLastDirection = Detector.checkDirectionRank(maze, area, actualPosition, lastDirection);
        int pointsMoveRecommended = Detector.checkDirectionRank(maze, area, actualPosition, moveRecommended);

        if(lastDirection != null && Arrays.stream(availableMoves).anyMatch(lastDirection::equals) &&
            !isNextCoordsInPathUsed(getNextCoordsByMove(lastDirection,actualPosition))) {
            return lastDirection;
        } else if (moveRecommended != null && !isNextCoordsInPathUsed(getNextCoordsByMove(moveRecommended, actualPosition))) {
            return moveRecommended;
        } else if(availableMoves.length > 0){
            return availableMoves[new Random().nextInt(availableMoves.length)];
        } else {
            return MOVES[new Random().nextInt(MOVES.length)];
        }
    }

    private Coordinates getTargetDirectShot(String targetType) {
        Coordinates enemy = null;
        List<Coordinates> coordinatesList = new ArrayList<>();

        List<Coordinates> targetsOnAxisX = new ArrayList<>();
        for(int x = area.getCordX1(); x <= area.getCordX2(); x++) {
            if(maze[actualPosition.getCordY()][x] != null && maze[actualPosition.getCordY()][x].equals(targetType)) {
                targetsOnAxisX.add(new Coordinates(actualPosition.getCordY(), x));
            } else if(maze[actualPosition.getCordY()][x] != null && maze[actualPosition.getCordY()][x].equals(CellType.WALL)) {
                if(actualPosition.getCordX() < x) {
                    break;
                } else if(!targetsOnAxisX.isEmpty()) {
                    targetsOnAxisX.remove(targetsOnAxisX.size()-1);
                }
            }
        }

        List<Coordinates> targetsOnAxisY = new ArrayList<>();
        for(int y = area.getCordY1(); y <= area.getCordY2(); y++) {
            if(maze[y][actualPosition.getCordX()] != null && maze[y][actualPosition.getCordX()].equals(targetType)) {
                targetsOnAxisY.add(new Coordinates(y, actualPosition.getCordX()));
            } else if(maze[y][actualPosition.getCordX()] != null && maze[y][actualPosition.getCordX()].equals(CellType.WALL)) {
                if(actualPosition.getCordY() < y) {
                    break;
                } else if(!targetsOnAxisY.isEmpty()) {
                    targetsOnAxisY.remove(targetsOnAxisY.size()-1);
                }
            }
        }

        coordinatesList.addAll(targetsOnAxisX);
        coordinatesList.addAll(targetsOnAxisY);

        for(Coordinates item : coordinatesList) {
            int distanceOfEnemy = Detector.distanceOfTwoObjects(actualPosition, item);

            if(enemy == null || Detector.distanceOfTwoObjects(actualPosition, enemy) > distanceOfEnemy) {
                enemy = item;
            }
        }
        return enemy;
    }

    public String runAwayFromEnemies() {
        String enemyDirection = null;
        int lastDistance = 0;

        for(Coordinates enemy : potentialEnemies) {
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

    public List<Coordinates> getPathUsed() {
        return pathUsed;
    }

    public boolean isNextCoordsInPathUsed(Coordinates nextCoords) {
        for(Coordinates coords : pathUsed) {
            if(coords.getCordX() == nextCoords.getCordX() && coords.getCordY() == nextCoords.getCordY()) {
                return true;
            }
        }
        return false;
    }
}
