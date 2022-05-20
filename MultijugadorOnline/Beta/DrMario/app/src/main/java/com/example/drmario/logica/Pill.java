package com.example.drmario.logica;

public class Pill {
    private int valDer;
    private int valIzq;

    public Pill() {
        valIzq = ((1 + (int) (Math.random() * 3)) *10) + 1;
        valDer = ((1 + (int) (Math.random() * 3)) *10) + 2;
    }

    public int getValDer() {
        return valDer;
    }

    public int getValIzq() {
        return valIzq;
    }
}
