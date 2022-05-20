package com.example.drmario.logica;

public class Players {
    private int id;
    private int cantVirus;
    private boolean virus;
    private boolean victoria;
    private boolean derrota;

    public Players() {
    }

    public Players(int id) {
        this.id = id;
        this.cantVirus = 4;

    }

    public int getId() {
        return id;
    }

    public int getCantVirus() {
        return cantVirus;
    }

    public boolean getVirus() {
        return virus;
    }

    public boolean isDerrota() {
        return derrota;
    }

    public boolean isVictoria() {
        return victoria;
    }
}


