package com.juanarroyes.apispaceinvaders.dto;

public class PlayerDto {

    private String id;

    private String name;

    private PositionDto position;

    private PreviousDto previous;

    private AreaDto area;

    private boolean fire;

    public PlayerDto() {

    }

    public PlayerDto(String id, String name, PositionDto position, PreviousDto previous, AreaDto area, boolean fire) {
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

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public PreviousDto getPrevious() {
        return previous;
    }

    public void setPrevious(PreviousDto previous) {
        this.previous = previous;
    }

    public AreaDto getArea() {
        return area;
    }

    public void setArea(AreaDto area) {
        this.area = area;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }
}
