package com.juanarroyes.apispaceinvaders.utils;

import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class ShipUtils {

    public static final String DIRECTION_LEFT = "left";
    public static final String DIRECTION_RIGHT = "right";
    public static final String DIRECTION_UP = "up";
    public static final String DIRECTION_DOWN = "down";

    public static Coordinates getTargetDirectShot(String[][] maze, Area area, Coordinates actualPosition, String targetType) {
        Coordinates enemy = null;
        List<Coordinates> coordinatesList = new ArrayList<>();

        for(int x = area.getCordX1(); x <= area.getCordX2(); x++) {
            if(maze[actualPosition.getCordY()][x].equals(targetType)) {
                coordinatesList.add(new Coordinates(actualPosition.getCordY(), x));
            } else if(maze[actualPosition.getCordY()][x].equals(CellType.WALL)) {
                if(actualPosition.getCordX() < x) {
                    break;
                } else if(!coordinatesList.isEmpty()) {
                    coordinatesList.remove(coordinatesList.size()-1);
                }
            }
        }

        for(int y = area.getCordY1(); y <= area.getCordY2(); y++) {
            if(maze[y][actualPosition.getCordX()].equals(targetType)) {
                coordinatesList.add(new Coordinates(y, actualPosition.getCordX()));
            } else if(maze[y][actualPosition.getCordX()].equals(CellType.WALL)) {
                if(actualPosition.getCordY() < y) {
                    break;
                } else if(!coordinatesList.isEmpty()) {
                    coordinatesList.remove(coordinatesList.size()-1);
                }
            }
        }

        for(Coordinates item : coordinatesList) {
            int distanceOfEnemy = distanceOf(actualPosition, item);

            if(enemy == null || distanceOf(actualPosition, enemy) > distanceOfEnemy) {
                enemy = item;
            }

            System.out.println("Distance for " + item.toString() + " of position " + actualPosition.toString() + " is " + String.valueOf(distanceOfEnemy));
        }

        System.out.println("The enemy is: " + ((enemy != null) ? enemy.toString() : "null"));
        return enemy;
    }


    public static int distanceOf(Coordinates source, Coordinates target) {
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

    public static String directionFire(Coordinates ship, Coordinates target) {
        if (ship.getCordY() == target.getCordY() && (ship.getCordX() - target.getCordX()) > 0) {
            return DIRECTION_RIGHT;
        } else if (ship.getCordY() == target.getCordY() && (ship.getCordX() - target.getCordX()) < 0) {
            return DIRECTION_LEFT;
        } else if (ship.getCordX() == target.getCordX() && (ship.getCordY() - target.getCordY()) > 0) {
            return DIRECTION_UP;
        } else if (ship.getCordX() == target.getCordX() && (ship.getCordY() - target.getCordY()) < 0) {
            return DIRECTION_DOWN;
        }

        return null;
    }

}
