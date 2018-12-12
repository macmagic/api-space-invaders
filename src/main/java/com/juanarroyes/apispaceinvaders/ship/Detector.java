package com.juanarroyes.apispaceinvaders.ship;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.constants.Moves;
import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;
import com.juanarroyes.apispaceinvaders.utils.MazeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Detector {

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

    public static boolean isObstacleBetweenTwoObjects(String[][] maze, Coordinates source, Coordinates target) {
        int y1;
        int y2;
        int x1;
        int x2;

        y1 = ((target.getCordY() - source.getCordY()) < 0) ? target.getCordY() : source.getCordY();
        y2 = ((target.getCordY() - source.getCordY()) < 0) ? source.getCordY() : target.getCordY();
        x1 = ((target.getCordX() - source.getCordX()) < 0) ? target.getCordX() : source.getCordX();
        x2 = ((target.getCordX() - source.getCordX()) < 0) ? source.getCordX() : target.getCordX();

        if(target.getCordX() == source.getCordX()) {
            if((target.getCordY() - source.getCordY()) < 0) {
                for(int y = y2; y>=y1; y--) {
                    if(maze[y][target.getCordX()] != null && maze[y][target.getCordX()].equals(CellType.WALL)) {
                        return true;
                    }
                }
            } else if((target.getCordY() - source.getCordY()) > 0) {
                for(int y = y1; y<=y2; y++) {
                    if(maze[y][target.getCordX()] != null && maze[y][target.getCordX()].equals(CellType.WALL)) {
                        return true;
                    }
                }
            }
        } else if(target.getCordY() == source.getCordY()) {
            if((target.getCordX() - source.getCordX()) < 0) {
                for(int x = x2; x>=x1; x--) {
                    if(maze[target.getCordY()][x] != null && maze[target.getCordY()][x].equals(CellType.WALL)) {
                        return true;
                    }
                }
            } else if((target.getCordY() - source.getCordY()) > 0) {
                for(int x = x1; x<=x2; x++) {
                    if(maze[target.getCordY()][x] != null && maze[target.getCordY()][x].equals(CellType.WALL)) {
                        return true;
                    }
                }
            }
        } else {
            if(((target.getCordX() - source.getCordX()) < 0) && (target.getCordY() - source.getCordY()) < 0) {
                System.out.println("Revert for in axis Y and X");
                for(int y = y2; y >= y1; y--) {
                    for(int x = x2; x >= x1; x--) {
                        if(maze[y][x] != null && maze[y][x].equals(CellType.WALL)) {
                            return true;
                        }
                    }
                }
                System.out.println("Finish X and Y....");
            } else if((target.getCordY() - source.getCordY()) < 0) {
                System.out.println("Revert for in axis Y");
                for(int y = y2; y>=y1; y--) {
                    for(int x = x1; x<= x2; x++) {
                        if(maze[y][x] != null && maze[y][x].equals(CellType.WALL)) {
                            return true;
                        }
                    }
                }
                System.out.println("Finish Y....");
            } else if((target.getCordX() - source.getCordX()) < 0) {
                System.out.println("Revert for in axis X");
                for(int y = y1; y<=y2; y++) {
                    for(int x = x2; x >= x1; x--) {
                        if(maze[y][x] != null && maze[y][x].equals(CellType.WALL)) {
                            return true;
                        }
                    }
                }
                System.out.println("Finish X....");
            } else {
                for(int y = y1; y<=y2; y++) {
                    for(int x = x1; x < x2; x++) {
                        if(maze[y][x] != null && maze[y][x].equals(CellType.WALL)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param ship
     * @param target
     * @return
     */
    public static String directionOfTarget(Coordinates ship, Coordinates target) {
        if ((ship.getCordX() - target.getCordX()) < 0) {
            return Moves.RIGHT;
        } else if ((ship.getCordX() - target.getCordX()) > 0) {
            return Moves.LEFT;
        } else if ((ship.getCordY() - target.getCordY()) > 0) {
            return Moves.UP;
        } else if ((ship.getCordY() - target.getCordY()) < 0) {
            return Moves.DOWN;
        }
        return null;
    }

    public static List<Coordinates> getPotentialThreats(String[][] maze, Coordinates position, int distance) {
        int cordY1 = ((position.getCordY() - distance) < 0) ? 0 : position.getCordY() - distance;
        int cordX1 = ((position.getCordX() - distance) < 0) ? 0 : position.getCordX() - distance;
        int cordY2 = ((position.getCordY() + distance) >= maze.length) ? maze.length -1 : position.getCordY() + distance;
        int cordX2 = ((position.getCordX() + distance) >= maze[0].length) ? maze.length - 1 : position.getCordX() + distance;
        List<Coordinates> threats = new ArrayList<>();

        for(int y = cordY1; y <= cordY2; y++) {
            for(int x = cordX1; x <= cordX2; x++) {
                String cellValue = maze[y][x];
                if(cellValue == null) {
                    continue;
                } else if((cellValue.equals(CellType.ENEMY) || cellValue.equals(CellType.INVADER)) && !isObstacleBetweenTwoObjects(maze, position, new Coordinates(y, x))) {
                    threats.add(new Coordinates(y, x));
                }
            }
        }
        return threats;
    }

    public static Coordinates followBestEnemy(String[][] maze, Coordinates actualPosition, List<Coordinates> threats) {

        int lastDistance = 0;
        Coordinates bestEnemyFollow = null;

        for(Coordinates enemy :  threats) {
            int distance = distanceOfTwoObjects(actualPosition, enemy);
            if((bestEnemyFollow == null || distance <= lastDistance)) {
                lastDistance = distance;
                bestEnemyFollow = enemy;
            }
        }
        return bestEnemyFollow;
    }

    public static String[] getAvailableMoves(String[][] maze, Coordinates actualPosition, String[] moves, String enemyDirection) {
        List<String> availiableMovesList = new ArrayList<>();

        for(String move :  moves) {
            if(!move.equals(enemyDirection) && checkMoveIsAvailable(maze, actualPosition, move)) {
                availiableMovesList.add(move);
            }
        }

        String[] availableMovesArr = new String[availiableMovesList.size()];
        availiableMovesList.toArray(availableMovesArr);
        return availableMovesArr;
    }

    private static boolean checkMoveIsAvailable(String[][] maze, Coordinates actualPosition, String move) {
        switch(move) {
            case Moves.DOWN:
                return (maze[actualPosition.getCordY()+1][actualPosition.getCordX()] !=null && !maze[actualPosition.getCordY()+1][actualPosition.getCordX()].equals(CellType.WALL));
            case Moves.UP:
                return (maze[actualPosition.getCordY()-1][actualPosition.getCordX()] != null && !maze[actualPosition.getCordY()-1][actualPosition.getCordX()].equals(CellType.WALL));
            case Moves.LEFT:
                return (maze[actualPosition.getCordY()][actualPosition.getCordX()-1] != null && !maze[actualPosition.getCordY()][actualPosition.getCordX()-1].equals(CellType.WALL));
            case Moves.RIGHT:
                return (maze[actualPosition.getCordY()][actualPosition.getCordX()+1] != null && !maze[actualPosition.getCordY()][actualPosition.getCordX()+1].equals(CellType.WALL));
            default:
                return false;
        }
    }

    public static String getRecommendedDirection(String[][] maze, Area area, Coordinates actualPosition, String[] availableMoves) {
        int lastPoints = 0;
        String moveSelected = null;
        int idx1;
        int idx2;
        int idx;
        int points;

        for(String move : availableMoves) {
            points = 0;
            switch(move) {
                case Moves.DOWN:
                    idx1 = actualPosition.getCordY()+1;
                    idx2 = area.getCordY2();
                    for(idx = idx1; idx <= idx2; idx++) {
                        if(maze[idx][actualPosition.getCordX()] != null && maze[idx][actualPosition.getCordX()].equals(CellType.VIEWED)) {
                            points++;
                        } else if(maze[idx][actualPosition.getCordX()] == null || !maze[idx][actualPosition.getCordX()].equals(CellType.VIEWED)) {
                            break;
                        }
                    }
                    break;
                case Moves.UP:
                    idx1 = area.getCordY1();
                    idx2 = actualPosition.getCordY()-1;
                    for(idx = idx2; idx >= idx1; idx--) {
                        if(maze[idx][actualPosition.getCordX()] != null && maze[idx][actualPosition.getCordX()].equals(CellType.VIEWED)) {
                            points++;
                        } else if(maze[idx][actualPosition.getCordX()] == null || !maze[idx][actualPosition.getCordX()].equals(CellType.VIEWED)) {
                            break;
                        }
                    }
                    break;
                case Moves.LEFT:
                    idx1 = area.getCordX1();
                    idx2 = actualPosition.getCordX() -1;
                    for(idx = idx2; idx >= idx1; idx--) {
                        if(maze[actualPosition.getCordY()][idx] != null && maze[actualPosition.getCordY()][idx].equals(CellType.VIEWED)) {
                            points++;
                        } else if(maze[actualPosition.getCordY()][idx] == null || !maze[actualPosition.getCordY()][idx].equals(CellType.VIEWED)) {
                            break;
                        }
                    }
                    break;
                case Moves.RIGHT:
                    idx1 = actualPosition.getCordX() + 1;
                    idx2 = area.getCordX2();
                    for(idx = idx1; idx <= idx2; idx++) {
                        if(maze[actualPosition.getCordY()][idx] != null && maze[actualPosition.getCordY()][idx].equals(CellType.VIEWED)) {
                            points++;
                        } else if(maze[actualPosition.getCordY()][idx] == null || !maze[actualPosition.getCordY()][idx].equals(CellType.VIEWED)) {
                            break;
                        }
                    }
                    break;
                default:
                    continue;
            }

            if(lastPoints < points) {
                moveSelected = move;
                lastPoints = points;
            }
        }
        return moveSelected;
    }

}
