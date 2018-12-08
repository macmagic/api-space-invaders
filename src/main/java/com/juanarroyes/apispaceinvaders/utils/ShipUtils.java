package com.juanarroyes.apispaceinvaders.utils;

import com.juanarroyes.apispaceinvaders.constants.CellType;
import com.juanarroyes.apispaceinvaders.dto.Area;
import com.juanarroyes.apispaceinvaders.dto.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class ShipUtils {

    /*public static String getShipDirection() {

    }*/


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
            int distanceOfEnemy = DetectorUtils.distanceOfTwoObjects(actualPosition, item);

            if(enemy == null || DetectorUtils.distanceOfTwoObjects(actualPosition, enemy) > distanceOfEnemy) {
                enemy = item;
            }
            System.out.println("Distance for " + item.toString() + " of position " + actualPosition.toString() + " is " + String.valueOf(distanceOfEnemy));
        }

        System.out.println("The enemy is: " + ((enemy != null) ? enemy.toString() : "null"));
        return enemy;
    }
}
