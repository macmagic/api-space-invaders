package com.juanarroyes.apispaceinvaders.dto;

public class ObjectDetect {

    private Coordinates position;

    private String lastMove;

    private int distance;

    public ObjectDetect() {

    }

    public ObjectDetect(Coordinates position, String lastMove, int distance) {
        this.position = position;
        this.lastMove = lastMove;
        this.distance = distance;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public String getLastMove() {
        return lastMove;
    }

    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
