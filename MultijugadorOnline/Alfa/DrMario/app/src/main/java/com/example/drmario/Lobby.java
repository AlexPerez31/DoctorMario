package com.example.drmario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.drmario.logica.ConexionFireBase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Lobby extends AppCompatActivity {
    private Button player1, player2;
    private Timer timer;
    private boolean play1, play2;
    public static int mi;
    private ImageView waiting;
    private Juego2 juego2 = new Juego2();
    private long count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        this.waiting = findViewById(R.id.Waiting);
        this.waiting.setImageResource(R.drawable.waiting);
        this.waiting.setVisibility(View.GONE);

        this.player1 = findViewById(R.id.P1);
        this.player1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmarPlayer(true);
                mi = 1;
            }
        });

        this.player2 = findViewById(R.id.P2);
        this.player2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmarPlayer(false);
                mi = 2;
            }
        });

        if(this.timer != null){
            this.timer.cancel();
        }
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        verificarPlayer();
                        regresar();
                        System.out.println("play1: " + play1);
                        System.out.println("play2: " + play2);
                        System.out.println("mi: " + mi);
                        System.out.println("count: " + count);
                        pasarActivity();
                    }
                });
            }
        },0,10);


    }

    private void regresar() {
        if(count > 0) {
            if(mi == 1) {
                if(play1 && !play2) {
                    player1.setEnabled(false);
                    player1.setBackgroundColor(Color.GRAY);
                    player2.setVisibility(View.GONE);
                }
                if(play2 && !play1) {
                    player1.setEnabled(true);
                    player2.setEnabled(false);
                    player2.setBackgroundColor(Color.GRAY);
                }
            }
            if(mi == 2) {
                if(play1 && !play2) {
                    player2.setEnabled(true);
                    player1.setEnabled(false);
                    player1.setBackgroundColor(Color.GRAY);
                }
                if(play2 && !play1) {
                    player2.setEnabled(false);
                    player2.setBackgroundColor(Color.GRAY);
                    player1.setVisibility(View.GONE);
                }
            }
            if(mi == 0) {
                if(play1 && !play2) {
                    player2.setEnabled(true);
                    player1.setEnabled(false);
                    player1.setBackgroundColor(Color.GRAY);
                }
                if(play2 && !play1) {
                    player1.setEnabled(true);
                    player2.setEnabled(false);
                    player2.setBackgroundColor(Color.GRAY);
                }
            }
        }
        if(!play1 && !play2) {
            player1.setEnabled(true);
            player1.setVisibility(View.VISIBLE);
            player1.setBackgroundColor(Color.parseColor("#FF0000"));
            player2.setEnabled(true);
            player2.setVisibility(View.VISIBLE);
            player2.setBackgroundColor(Color.parseColor("#FFDD00"));
        }
    }


    private void pasarActivity() {
        if(play1 && play2){
            this.timer.cancel();
            juego2.detenertimertask();
            Intent intent = new Intent(this, Juego2.class);
            startActivity(intent);
        }
    }

    private void verificarPlayer() {
        ConexionFireBase.getmDatabase().child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    count = dataSnapshot.getChildrenCount();
                    if(count > 0) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String id = snapshot.getKey();
                            if(id.equals("1")) {
                                play1 = true;
                            }
                            if(id.equals("2")) {
                                play2 = true;
                            }
                        }
                    }
                    waiting.setVisibility(View.VISIBLE);
                }
                else{
                    play1 = false;
                    play2 = false;
                    waiting.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void confirmarPlayer(boolean b) {
        if(b){
            ConexionFireBase.getINSTANCE().guardarPlayer(1);
        }else{
            ConexionFireBase.getINSTANCE().guardarPlayer(2);
        }
    }
}