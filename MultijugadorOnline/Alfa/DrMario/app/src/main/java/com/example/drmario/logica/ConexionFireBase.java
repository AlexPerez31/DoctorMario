package com.example.drmario.logica;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ConexionFireBase {
    private static final ConexionFireBase INSTANCE = new ConexionFireBase();
    private static DatabaseReference mDatabase;

    public ConexionFireBase() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static ConexionFireBase getINSTANCE() {
        return INSTANCE;
    }

    public static DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public void guardarPlayer(int p) {
        Players player = new Players(p);
        this.mDatabase.child("players").child(String.valueOf(player.getId())).setValue(player);
    }
}