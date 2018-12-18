package com.juanarroyes.apispaceinvaders.controller;

import com.juanarroyes.apispaceinvaders.dto.Stage;
import com.juanarroyes.apispaceinvaders.request.MoveRequest;
import com.juanarroyes.apispaceinvaders.response.MoveResponse;
import com.juanarroyes.apispaceinvaders.response.NameResponse;
import com.juanarroyes.apispaceinvaders.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ShipController {

    @Value("${app.name}")
    private String name;

    @Value("${app.email}")
    private String email;

    private ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @PostMapping("/name")
    public ResponseEntity<NameResponse> getWhoAmI() {
        NameResponse response = new NameResponse(name, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/move")
    public ResponseEntity<MoveResponse> moveShip(@RequestBody MoveRequest request) {
        Stage stage =  new Stage();
        stage.setGameId(request.getGame().getId());
        stage.setPlayerId(request.getPlayer().getId());
        stage.setMazeSize(request.getBoard().getSize());
        stage.setActualPosition(request.getPlayer().getPosition());
        stage.setPreviousPosition(request.getPlayer().getPrevious());
        stage.setArea(request.getPlayer().getArea());
        stage.setFire(request.getPlayer().isFire());
        stage.setEnemies(request.getEnemies());
        stage.setInvaders(request.getInvaders());
        stage.setWalls(request.getBoard().getWalls());
        String move = shipService.moveShip(stage);
        MoveResponse response = new MoveResponse(move);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
