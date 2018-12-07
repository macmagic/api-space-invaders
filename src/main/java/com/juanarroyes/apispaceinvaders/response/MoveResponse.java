package com.juanarroyes.apispaceinvaders.response;

public class MoveResponse {

    private String move;
    private Boolean fire;

    public MoveResponse() {

    }

    public MoveResponse(String move) {
        this.move = move;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
