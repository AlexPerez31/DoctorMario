package com.example.drmario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.drmario.logica.ConexionFireBase;
import com.example.drmario.logica.Dr_Mario2;
import com.example.drmario.logica.Players;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.drmario.Lobby.mi;


public class Juego2 extends AppCompatActivity {
    public int x1,x2;
    public int y1,y2;
    private int aux;
    private Dr_Mario2 dr_mario = new Dr_Mario2();
    private GridLayout tablero;
    private ImageView sprite;
    private ImageView [][] matriz = new ImageView[16][8];
    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    boolean movimiento = true;
    private TextView puntaje;
    private TextView virus;
    private TextView nivel;
    private MediaPlayer mediaPlayer;
    private Button stop;
    private Button reiniciar;
    Handler reinicio_automatico = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego2);

        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.feverextended);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int ancho = size.x;
        int alto = size.y;
        tablero = (GridLayout)findViewById(R.id.Tablero);
        tablero.setRowCount(16);
        tablero.setColumnCount(8);
        sprite = (ImageView)findViewById(R.id.sprite);
        puntaje = (TextView)findViewById(R.id.Puntaje);
        virus = (TextView)findViewById(R.id.Virus);
        nivel = (TextView)findViewById(R.id.EVirus);
        stop = (Button)findViewById(R.id.Stop);
        reiniciar = (Button)findViewById(R.id.Reiniciar);
        reiniciar.setVisibility(View.INVISIBLE);


        for(int fila=0;fila<16;fila++){
            for(int columna=0;columna<8;columna++){
                ImageView imagen = new ImageView(this);
                imagen.setLayoutParams(new LinearLayout.LayoutParams(ancho/17, alto/30));
                imagen.setBackgroundColor(Color.BLACK);
                imagen.setImageDrawable(getResources().getDrawable(R.drawable.nada));
                matriz[fila][columna] = imagen;
                tablero.addView(imagen);
            }
        }

        View pantalla = getWindow().getDecorView();
        pantalla.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    x1 = (int)motionEvent.getX();
                    y1 = (int)motionEvent.getY();
                } else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    x2 = (int)motionEvent.getX();
                    y2 = (int)motionEvent.getY();
                    movimiento(x1,x2,y1,y2);
                }
                return true;
            }
        });
        pantalla_completa(pantalla);
        reactivar_pantalla_completa(pantalla);
        pintar();
        pintarSiguiente();
        comenzarTimer();


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                stop.setVisibility(View.INVISIBLE);
            }
        });

        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                movimiento = true;
                reinicio_automatico.postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                },500);
            }
        });


    }

    private void movimiento(int x1, int x2, int y1, int y2){
        if(movimiento){
            int deltaX=x1-x2;
            int deltaY=y1-y2;
            if(Math.abs(deltaY)>=Math.abs(deltaX)){
                if(deltaY>0){
                    dr_mario.rotar();
                    pintar();
                }else if(deltaY<0){
                    while(dr_mario.validarAbajo()){
                        dr_mario.abajo();
                    }
                    pintar();
                }
            }else{
                if(deltaX>0){
                    dr_mario.izquierda();
                    pintar();
                }else if(deltaX<0){
                    dr_mario.derecha();
                    pintar();
                }
            }
        }
    }

    private void pantalla_completa(View vw){
        vw.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void reactivar_pantalla_completa(final View vw) {
        vw.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                pantalla_completa(vw);
            }
        });
    }

    private void pintar(){
        for(int fila=0;fila<16;fila++){
            for(int columna=0;columna<8;columna++){
                if(dr_mario.matriz[fila][columna] == 0){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 30){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.virus_azul_1));
                }else if(dr_mario.matriz[fila][columna] == 20){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.virus_amarillo_1));
                }else if(dr_mario.matriz[fila][columna] == 10){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.virus_rojo_1));
                }else if(dr_mario.matriz[fila][columna] == 11){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.rojizq));
                }else if(dr_mario.matriz[fila][columna] == 21){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.amaizq));
                }else if(dr_mario.matriz[fila][columna] == 31){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.azuizq));
                }else if(dr_mario.matriz[fila][columna] == 12){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.rojder));
                }else if(dr_mario.matriz[fila][columna] == 22){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.amader));
                }else if(dr_mario.matriz[fila][columna] == 32){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.azuder));
                }else if(dr_mario.matriz[fila][columna] == 13){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.rojaba));
                }else if(dr_mario.matriz[fila][columna] == 23){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.amaaba));
                }else if(dr_mario.matriz[fila][columna] == 33){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.azuaba));
                }else if(dr_mario.matriz[fila][columna] == 14){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.rojarr));
                }else if(dr_mario.matriz[fila][columna] == 24){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.amaarr));
                }else if(dr_mario.matriz[fila][columna] == 34){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.azuarr));
                }else if(dr_mario.matriz[fila][columna] == 15){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.past1));
                }else if(dr_mario.matriz[fila][columna] == 25){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.past2));
                }else if(dr_mario.matriz[fila][columna] == 35){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.past3));
                }
            }
        }

    }

    private void pintarSiguiente(){
        if(dr_mario.imagen == 1){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic1));
        }else if(dr_mario.imagen == 2){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic2));
        }else if(dr_mario.imagen == 3){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic3));
        }else if(dr_mario.imagen == 4){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic4));
        }else if(dr_mario.imagen == 5){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic5));
        }else if(dr_mario.imagen == 6){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic6));
        }else if(dr_mario.imagen == 7){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic7));
        }else if(dr_mario.imagen == 8){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic8));
        }else if(dr_mario.imagen == 9){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonic9));
        }
    }

    private void pintarperder(){
        this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.derrota));
    }

    private void pintarganar(){
        this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.victoria));
    }

    private void virusEnemigo(){
        ConexionFireBase.getmDatabase().child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int cantE = 0;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Players user = snapshot.getValue(Players.class);
                        String id = snapshot.getKey();
                        if(Integer.parseInt(id) != mi){
                            cantE = user.getCantVirus();
                        }
                    }
                    nivel.setText("" + cantE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("cantVirus", dr_mario.virus);
        ConexionFireBase.getmDatabase().child("players").child(String.valueOf(mi)).updateChildren(playerMap);
    }

    public void derrota(){
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("derrota", true);
        ConexionFireBase.getmDatabase().child("players").child(String.valueOf(mi)).updateChildren(playerMap);
    }

    public void victoria(){
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("victoria", true);
        ConexionFireBase.getmDatabase().child("players").child(String.valueOf(mi)).updateChildren(playerMap);
    }

    public void verificarDerrota(){
        ConexionFireBase.getmDatabase().child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Players user = snapshot.getValue(Players.class);
                        String id = snapshot.getKey();
                        boolean derrota = user.isDerrota();
                        if(Integer.parseInt(id) != mi){
                            if(derrota){
                                detenertimertask();
                                movimiento = false;
                                pintarganar();
                                mediaPlayer.stop();
                                reiniciar.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void verificarVictoria(){
        ConexionFireBase.getmDatabase().child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Players user = snapshot.getValue(Players.class);
                        String id = snapshot.getKey();
                        boolean victoria = user.isVictoria();
                        if(Integer.parseInt(id) != mi){
                            if(victoria){
                                detenertimertask();
                                movimiento = false;
                                pintarperder();
                                mediaPlayer.stop();
                                reiniciar.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void comenzarTimer(){
        if(this.timer != null) {
            this.timer.cancel();
        }
        timer = new Timer();
        inicializarTimerTask();
        timer.schedule(timerTask, 1500, 1000);
    }
    public void inicializarTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        if(dr_mario.perdio){
                            detenertimertask();
                            movimiento = false;
                            pintarperder();
                            mediaPlayer.stop();
                            derrota();
                            reiniciar.setVisibility(View.VISIBLE);
                        }else if(dr_mario.findeljuego){
                            detenertimertask();
                            movimiento = false;
                            pintarganar();
                            mediaPlayer.stop();
                            victoria();
                            reiniciar.setVisibility(View.VISIBLE);
                        }else {
                         /*   if (dr_mario.contBD == 1) {
                                while (dr_mario.validarAbajo()) {
                                    dr_mario.abajo();
                                    pintar();
                                }
                            } else {*/

                                dr_mario.abajo();
                                if (!dr_mario.movimiento) {
                                    movimiento = false;
                                } else {
                                    movimiento = true;
                                }
                                verificarDerrota();
                                verificarVictoria();
                                pintar();
                                virusEnemigo();

                                virus.setText("" + dr_mario.virus);

                                puntaje.setText("" + dr_mario.puntaje);
                                pintarSiguiente();

                           // }
                        }
                    }
                });
            }
        };
    }

    public void detenertimertask(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


}