package com.juanarroyes.apispaceinvaders.service;

import com.juanarroyes.apispaceinvaders.dto.AreaDto;
import com.juanarroyes.apispaceinvaders.request.MoveRequest;
import com.juanarroyes.apispaceinvaders.response.MoveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class ShipServiceImpl {

    private int[][] maze;

    private boolean setup = false;

    public String moveShip(MoveRequest requestData) {
        if(!setup) {
            init(requestData);
        }
        List<String> moves = new ArrayList<>(Arrays.asList("up", "right", "left", "down"));
        return  moves.get(new Random().nextInt(4));
    }

    private void init(MoveRequest requestData) {
        int height = requestData.getBoard().getSize().getHeight();
        int width = requestData.getBoard().getSize().getWidth();
        maze = new int[width][height];
        setup = true;
    }

    private void mazeDiscovery(AreaDto area) {

    }
}
