package com.juanarroyes.apispaceinvaders.dto;

public class Player {

    private String id;

    private String name;

    private Coordinates position;

    private Coordinates previous;

    private Area area;

    private boolean fire;

    public Player() {

    }

    public Player(String id, String name, Coordinates position, Coordinates previous, Area area, boolean fire) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.previous = previous;
        this.area = area;
        this.fire = fire;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public Coordinates getPrevious() {
        return previous;
    }

    public void setPrevious(Coordinates previous) {
        this.previous = previous;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }
}
