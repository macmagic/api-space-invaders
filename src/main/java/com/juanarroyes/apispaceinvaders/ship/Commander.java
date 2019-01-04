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
        log.info("Actual maze is:\n");
        log.info("\n" + drawMaze()+"\n");
        String move = null;
        Coordinates targetFire = null;

        List<Coordinates> potentialEnemies = Detector.getPotentialThreats(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);
        List<Coordinates> neutralInvaders = Detector.getPotentialThreats(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);
        String lastDirection = Detector.directionOfTarget(lastPosition, actualPosition);
        List<String> availableMoves = Detector.getAvailableMovesByObject(maze, actualPosition, MOVES);
        List<String> movesRecommended = Detector.getRecommendedMovesOrdered(maze, area, actualPosition, availableMoves);

        if(canFire) {
            targetFire = getTargetDirectShot(CellType.ENEMY);
            targetFire = (targetFire == null) ? getTargetDirectShot(CellType.INVADER) : targetFire;
        }

        if(targetFire != null) {
            String direction = Detector.directionOfTarget(actualPosition, targetFire);
            move = getMovement(direction, canFire);
        } else if(canFire && !potentialEnemies.isEmpty()) {
            Coordinates enemy = Detector.followBestEnemy(maze, actualPosition, potentialEnemies);
            move = Detector.directionOfTarget(actualPosition, enemy);
        } else if(!neutralInvaders.isEmpty()) {
            Coordinates bestNeutralInvader = Detector.followBestEnemy(maze, actualPosition, neutralInvaders);
            move = Detector.directionOfTarget(actualPosition, bestNeutralInvader);
        } else if(lastDirection != null && availableMoves.contains(lastDirection) && Detector.isNextMovementCorrect(maze, actualPosition, lastDirection)) {
            move = lastDirection;
        } else if (!movesRecommended.isEmpty()) {
            move = movesRecommended.get(0);
        } else if(!availableMoves.isEmpty()) {
            move = availableMoves.get(new Random().nextInt(availableMoves.size()));
        } else {
            move = randomMove();
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

    /*private String attackStrategy() {
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
    }*/

    /*private String defenseStrategy() {
        String move;

        List<Coordinates> neutralInvaders = Detector.getPotentialThreats(maze, actualPosition, DEFAULT_DISTANCE_OF_DETECTION);
        String lastDirection = Detector.directionOfTarget(lastPosition, actualPosition);
        List<String> availableMoves = Detector.getAvailableMovesByObject(maze, actualPosition, MOVES);
        List<String> movesRecommended = Detector.getRecommendedMovesOrdered(maze, area, actualPosition, availableMoves);

        if(!neutralInvaders.isEmpty()) {
            Coordinates bestNeutralInvader = Detector.followBestEnemy(maze, actualPosition, neutralInvaders);
            move = Detector.directionOfTarget(actualPosition, bestNeutralInvader);
        } else if(lastDirection != null && availableMoves.contains(lastDirection) && Detector.isNextMovementCorrect(maze, actualPosition, lastDirection)) {
            move = lastDirection;
        } else if (!movesRecommended.isEmpty()) {
            move = movesRecommended.get(0);
        } else if(!availableMoves.isEmpty()) {
            move = availableMoves.get(new Random().nextInt(availableMoves.size()));
        } else {
            move = randomMove();
        }
        return move;
    }*/

    /**
     *
     * @return
     */
    public static String randomMove() {
        return MOVES[new Random().nextInt(MOVES.length)];
    }

    /**
     * Set the objects detected to maze.
     */
    private void setObjectsToMaze() {
        log.info("Put objects detected to maze");

        log.info("Put player positions");
        maze[actualPosition.getCordY()][actualPosition.getCordX()] = CellType.POSITION;

        maze[lastPosition.getCordY()][lastPosition.getCordX()] = CellType.LAST_POSITION;

        log.info("Put invaders (neutral and enemy)");
        for(Invader invader :  invaders) {
            maze[invader.getCordY()][invader.getCordX()] = (invader.isNeutral() ? CellType.INVADER_NEUTRAL : CellType.INVADER);
        }

        log.info("Put enemies");
        for(Coordinates enemy : enemies) {
            maze[enemy.getCordY()][enemy.getCordX()] = CellType.ENEMY;
        }

        if(!lastWalls.isEmpty()) {
            addNewDiscoveredWalls();
        } else {
            log.info("Put maze limits");
            walls.addAll(getLimitWalls());
        }

        log.info("Put new walls");
        for(Coordinates wall :  walls) {
            maze[wall.getCordY()][wall.getCordX()] = CellType.WALL;
        }

        /*log.info("Put last walls");
        for(Coordinates wall :  lastWalls) {
            maze[wall.getCordY()][wall.getCordX()] = CellType.WALL;
        }*/

        log.info("Add visible area cells");
        for(int y = area.getCordY1(); y<=area.getCordY2(); y++) {
            for(int x = area.getCordX1(); x<=area.getCordX2(); x++) {
                maze[y][x] = (maze[y][x] !=null) ? maze[y][x] : CellType.VIEWED;
            }
        }

        addNewDiscoveredWalls();
    }

    private void addNewDiscoveredWalls() {
        for (Coordinates wall : lastWalls) {
            if(!walls.contains(wall)) {
                walls.add(wall);
            }
        }
    }

    private List<Coordinates> getLimitWalls() {
        List<Coordinates> limitWalls = new ArrayList<>();
        Coordinates position;
        int rowCount = maze.length;
        int colCount = maze[0].length;

        for(int y = 0; y < rowCount; y++) {
            if(y == 0 || y == rowCount-1) {
                for(int x = 0; x < colCount; x++) {
                    position = new Coordinates(y,x);
                    limitWalls.add(position);
                }
            } else {
                position = new Coordinates(y, 0);
                limitWalls.add(position);
                position = new Coordinates(y, colCount-1);
                limitWalls.add(position);
            }
        }
        return limitWalls;
    }

    private String drawMaze() {
        StringBuilder sb = new StringBuilder();
        int rowCount = maze.length;
        int colCount = maze[0].length;
        String cell;

        for(int y = 0; y < rowCount; y++) {
            sb.append("| ");
            for(int x = 0; x < colCount; x++) {
                cell = (maze[y][x] == null) ? "-" : maze[y][x];
                sb.append(cell).append(" | ");
            }
            sb.append("\n");
        }
        return sb.toString();
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
