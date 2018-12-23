package com.juanarroyes.apispaceinvaders.utils;

import com.juanarroyes.apispaceinvaders.dto.Coordinates;

import java.util.List;

public class Utils {

    public static List<Coordinates> addItemsToList(List<Coordinates> source, List<Coordinates> newItems) {
        for(Coordinates item : newItems) {
            if(!source.contains(item)) {
                source.add(item);
            }
        }
        return source;
    }

    public static String getGameStatusId(String gameId, String playerId) {
        return playerId + ":" + gameId;
    }
}
