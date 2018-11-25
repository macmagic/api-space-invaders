package com.juanarroyes.apispaceinvaders.controller;

import com.juanarroyes.apispaceinvaders.response.MoveResponse;
import com.juanarroyes.apispaceinvaders.response.NameResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/")
public class ShipController {

    @Value("${app.name}")
    private String name;

    @Value("${app.email}")
    private String email;

    @PostMapping("/name")
    public ResponseEntity<NameResponse> getWhoAmI() {
        NameResponse response = new NameResponse(name, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/move")
    public ResponseEntity<MoveResponse> moveShip() {
        List<String> moves = new ArrayList<>(Arrays.asList("up", "right", "left", "down"));
        String move = moves.get(new Random().nextInt(4));
        MoveResponse response = new MoveResponse(move, new Random().nextBoolean());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
