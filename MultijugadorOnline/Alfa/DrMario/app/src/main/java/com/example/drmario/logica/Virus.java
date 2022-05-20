package com.example.drmario.logica;

public class Virus {
    private int color;
    private int posX;
    private int posY;

    public Virus() {
    }

    public Virus(int color, int posX, int posY) {
        this.color = color;
        this.posX = posX;
        this.posY = posY;
    }

    public int getColor() {
        return color;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}
