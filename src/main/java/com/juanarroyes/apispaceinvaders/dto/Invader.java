package com.juanarroyes.apispaceinvaders.dto;

public class Invader extends Coordinates {

    private boolean neutral;

    public Invader() {
    }

    public Invader(int cordY, int cordX, boolean neutral) {
        super(cordY, cordX);
        this.neutral = neutral;
    }

    public boolean isNeutral() {
        return neutral;
    }

    public void setNeutral(boolean neutral) {
        this.neutral = neutral;
    }
}

