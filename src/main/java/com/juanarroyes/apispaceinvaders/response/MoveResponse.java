package com.juanarroyes.apispaceinvaders.response;

public class MoveResponse {

    private String move;
    private Boolean fire;

    public MoveResponse() {

    }

    public MoveResponse(String move, Boolean fire) {
        this.move = move;
        this.fire = fire;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public Boolean getFire() {
        return fire;
    }

    public void setFire(Boolean fire) {
        this.fire = fire;
    }
}
