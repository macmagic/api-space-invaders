package com.juanarroyes.apispaceinvaders.ship;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.constants.Moves;
import com.juanarroyes.apispaceinvaders.dto.*;
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
    private List<Coordinates> enemies;
    private List<Coordinates> walls;
    private List<Invader> invaders;
    private List<ObjectDetect> lastObjectsFound;
    private List<Coordinates> lastWalls;
    private boolean canFire;

    public Commander() { }

    public Commander(int height,
                     int width,
                     List<ObjectDetect> lastObjectsFound,
                     List<Coordinates> lastWalls, Area area,
                     Coordinates actualPosition,
                     Coordinates lastPosition,
                     boolean canFire) {
        this.maze = new String[height][width];
        this.area = area;
        this.actualPosition = actualPosition;
        this.lastPosition = lastPosition;
        this.lastObjectsFound = lastObjectsFound;
        this.lastWalls = lastWalls;
        this.canFire = canFire;
    }

    public String getDecision() {
        setObjectsToMaze();
        String move = null;

        if(canFire) {
            move = attackStrategy();
        } else {
            List<Coordinates> neutralInvaders = Detector.getNeutralInvaders(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);

        }


        return move;
    }

    public String[][] getMaze() {
        return maze;
    }

    public void setMaze(String[][] maze) {
        this.maze = maze;
    }

    public List<Coordinates> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Coordinates> enemies) {
        this.enemies = enemies;
    }

    public List<Coordinates> getWalls() {
        return walls;
    }

    public void setWalls(List<Coordinates> walls) {
        this.walls = walls;
    }

    public List<Invader> getInvaders() {
        return invaders;
    }

    public void setInvaders(List<Invader> invaders) {
        this.invaders = invaders;
    }

    private String attackStrategy() {
        String move = null;
        List<Coordinates> potentialEnemies = Detector.getPotentialThreats(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);

        Coordinates targetFire = getTargetDirectShot(CellType.ENEMY);
        targetFire = (targetFire == null) ? getTargetDirectShot(CellType.INVADER) : targetFire;

        if(targetFire != null) {
            String direction = Detector.directionOfTarget(actualPosition, targetFire);
            move = getMovement(direction, canFire);
        } else if(!potentialEnemies.isEmpty()) {
            Coordinates enemy = Detector.followBestEnemy(maze, actualPosition, potentialEnemies);
            move = Detector.directionOfTarget(actualPosition, enemy);
        }
        return move;
    }

    private String defenseStrategy() {
        String move = null;

        List<Coordinates> neutralInvaders = Detector.getPotentialThreats(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);
        String lastDirection = Detector.directionOfTarget(lastPosition, actualPosition);
        List<String> availableMoves = Detector.getAvailableMovesByObject(maze, actualPosition, MOVES);
        List<String> moves = Detector.getRecommendedDirection(maze, area, actualPosition, availableMoves);
        int pointsLastDirection = Detector.checkDirectionRank(maze, area, actualPosition, lastDirection);

        if(!neutralInvaders.isEmpty()) {
            Coordinates bestNeutralInvader = Detector.followBestEnemy(maze, actualPosition, neutralInvaders);
            move = Detector.directionOfTarget(actualPosition, bestNeutralInvader);
        } else if(lastDirection != null && availableMoves.contains(lastDirection)) {

        }

        /*int pointsMoveRecommended = Detector.checkDirectionRank(maze, area, actualPosition, moveRecommended);
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
        }*/


        return move;
    }

    /**
     *
     * @return
     */
    public static String randomMove() {
        return MOVES[new Random().nextInt(MOVES.length)];
    }

    private void setObjectsToMaze() {

        log.info("Put objects detected to maze");
        log.info("Put invaders (neutral and enemy)");
        for(Invader invader :  invaders) {
            maze[invader.getCordY()][invader.getCordX()] = (invader.isNeutral() ? CellType.INVADER_NEUTRAL : CellType.INVADER);
        }

        log.info("Put enemies");
        for(Coordinates enemy : enemies) {
            maze[enemy.getCordY()][enemy.getCordX()] = CellType.ENEMY;
        }

        log.info("Put walls");
        for(Coordinates wall :  walls) {
            maze[wall.getCordY()][wall.getCordX()] = CellType.WALL;
        }
    }

    private String getMovement(String direction, boolean fire) {
        String movement = "";
        if(fire) {
            movement = "fire-";
        }
        return movement + direction;
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
}
