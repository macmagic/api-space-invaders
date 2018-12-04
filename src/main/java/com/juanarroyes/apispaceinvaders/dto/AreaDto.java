package com.juanarroyes.apispaceinvaders.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class AreaDto {

    @JsonAlias(value = "y1")
    private int cordY1;

    @JsonAlias(value = "x1")
    private int cordX1;

    @JsonAlias(value = "y2")
    private int cordY2;

    @JsonAlias(value = "x2")
    private int cordX2;

    public AreaDto() {

    }

    public AreaDto(int cordY1, int cordX1, int cordY2, int cordX2) {
        this.cordY1 = cordY1;
        this.cordX1 = cordX1;
        this.cordY2 = cordY2;
        this.cordX2 = cordX2;
    }

    public int getCordY1() {
        return cordY1;
    }

    public void setCordY1(int cordY1) {
        this.cordY1 = cordY1;
    }

    public int getCordX1() {
        return cordX1;
    }

    public void setCordX1(int cordX1) {
        this.cordX1 = cordX1;
    }

    public int getCordY2() {
        return cordY2;
    }

    public void setCordY2(int cordY2) {
        this.cordY2 = cordY2;
    }

    public int getCordX2() {
        return cordX2;
    }

    public void setCordX2(int cordX2) {
        this.cordX2 = cordX2;
    }
}
