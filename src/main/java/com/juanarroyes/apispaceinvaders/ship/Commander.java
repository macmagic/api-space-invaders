package com.juanarroyes.apispaceinvaders.ship;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.constants.Moves;
import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
    private int counter;

    private List<Coordinates> potentialEnemies;

    public Commander(String[][] maze, Area area, Coordinates actualPosition, Coordinates lastPosition, boolean fire, List<Coordinates> pathUsed) {
        this.maze = maze;
        this.area = area;
        this.actualPosition = actualPosition;
        this.lastPosition = lastPosition;
        this.fire = fire;
        this.potentialEnemies = new ArrayList<>();
        this.pathUsed = pathUsed;
        this.counter = new Random().nextInt(8888888);
    }

    public String getDecision() {
        String move;
        Coordinates bestEnemyFire = getTargetDirectShot(CellType.ENEMY);
        Coordinates bestInvaderFire = getTargetDirectShot(CellType.INVADER);
        potentialEnemies = Detector.getPotentialThreats(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);
        List<Coordinates> neutralInvaders = Detector.getNeutralInvaders(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);

        if(fire && bestEnemyFire != null) {
            log.info("Fire enemy!");
            String direction = Detector.directionOfTarget(actualPosition, bestEnemyFire);
            move = getMovement(direction, fire);
        } else if(fire && bestInvaderFire != null) {
            log.info("Fire invader!");
            String direction = Detector.directionOfTarget(actualPosition, bestInvaderFire);
            move = getMovement(direction, fire);
        } else if(fire && !potentialEnemies.isEmpty()) {
            log.info("Persecution enemies!!!!");
            Coordinates enemy = Detector.followBestEnemy(maze, actualPosition, potentialEnemies);
            move = Detector.directionOfTarget(actualPosition, enemy);
        } else if(!neutralInvaders.isEmpty()) {
            Coordinates bestNeutralInvader = Detector.followBestEnemy(maze, actualPosition, neutralInvaders);
            move = Detector.directionOfTarget(actualPosition, bestNeutralInvader);
        } else {
            log.info("Traveler...");
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
        List<String> availableMoves = Detector.getAvailableMovesByObject(maze, actualPosition, MOVES);
        List<String> moves = Detector.getRecommendedDirection(maze, area, actualPosition, availableMoves);
        int pointsLastDirection = Detector.checkDirectionRank(maze, area, actualPosition, lastDirection);
        String moveRecommended = getMoveRecommendedWithoutPath(moves, actualPosition);
        int pointsMoveRecommended = Detector.checkDirectionRank(maze, area, actualPosition, moveRecommended);
        boolean isNextCoordsInPath = (lastDirection != null) ? isNextCoordsInPathUsed(getNextCoordsByMove(lastDirection,actualPosition)) : false;

        if(lastDirection != null && availableMoves.contains(lastDirection) &&
                (!isNextCoordsInPath || (isNextCoordsInPath && pointsLastDirection >= pointsMoveRecommended))) {
            log.info("I have use the LAST DIRECTION");
            return lastDirection;
        } else if (moveRecommended != null && pointsMoveRecommended > pointsLastDirection) {
            log.info("I have use the RECOMMENDED DIRECTION");
            return moveRecommended;
        } else if(!availableMoves.isEmpty()){
            log.info("I have use the random available moves");
            return availableMoves.get(new Random().nextInt(availableMoves.size()));
        } else {
            log.info("I have use the random moves");
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

    private String runAwayFromEnemies() {
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

    private boolean isNextCoordsInPathUsed(Coordinates nextCoords) {
        for (Coordinates coords : pathUsed) {
            if (coords.getCordX() == nextCoords.getCordX() && coords.getCordY() == nextCoords.getCordY()) {
                return true;
            }
        }
        return false;
    }

    private String getMoveRecommendedWithoutPath(List<String> moves, Coordinates actualPosition) {
        if(moves.isEmpty()) {
            return null;
        } else if(moves.size() == 1) {
            return moves.get(0);
        } else if(!isNextCoordsInPathUsed(getNextCoordsByMove(moves.get(0), actualPosition))) {
            return moves.get(0);
        } else {
            moves.remove(0);
            return getMoveRecommendedWithoutPath(moves, actualPosition);
        }
    }
}
