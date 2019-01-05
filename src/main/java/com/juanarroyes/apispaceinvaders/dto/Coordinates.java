package com.juanarroyes.apispaceinvaders.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Objects;

public class Coordinates {

    @JsonAlias(value = "x")
    private int cordX;

    @JsonAlias(value = "y")
    private int cordY;

    public Coordinates() {

    }

    public Coordinates(int cordY, int cordX) {
        this.cordX = cordX;
        this.cordY = cordY;
    }

    public int getCordX() {
        return cordX;
    }

    public void setCordX(int cordX) {
        this.cordX = cordX;
    }

    public int getCordY() {
        return cordY;
    }

    public void setCordY(int cordY) {
        this.cordY = cordY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return cordX == that.cordX &&
                cordY == that.cordY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cordX, cordY);
    }

    @Override
    public String toString() {
        return "Coordinates {" +
                "cordX=" + cordX +
                ", cordY=" + cordY +
                " }";
    }
}
