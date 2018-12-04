package com.juanarroyes.apispaceinvaders.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PreviousDto {

    @JsonAlias(value = "y")
    private int cordY;

    @JsonAlias(value = "x")
    private int cordX;

    public PreviousDto() {

    }

    public PreviousDto(int cordY, int cordX) {
        this.cordY = cordY;
        this.cordX = cordX;
    }

    public int getCordY() {
        return cordY;
    }

    public void setCordY(int cordY) {
        this.cordY = cordY;
    }

    public int getCordX() {
        return cordX;
    }

    public void setCordX(int cordX) {
        this.cordX = cordX;
    }
}
