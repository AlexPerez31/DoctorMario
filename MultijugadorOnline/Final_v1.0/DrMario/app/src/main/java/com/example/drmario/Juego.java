package com.example.drmario;

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

import com.example.drmario.logica.Dr_Mario;

import java.util.Timer;
import java.util.TimerTask;

public class Juego extends AppCompatActivity {
    public int x1,x2;
    public int y1,y2;
    private int aux;
    private Dr_Mario dr_mario = new Dr_Mario();
    private GridLayout tablero;
    private ImageView sprite;
    private ImageView [][] matriz = new ImageView[16][8];
    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    Handler reinicio_automatico = new Handler();
    Handler pausa = new Handler();
    private Button reiniciar;
    private Button pausar;
    private Button stop;
    boolean movimiento = true;
    private TextView puntaje;
    private TextView virus;
    private TextView nivel;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

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
        reiniciar = (Button)findViewById(R.id.Reiniciar);
        reiniciar.setVisibility(View.INVISIBLE);
        pausar = (Button)findViewById(R.id.Pausar);
        puntaje = (TextView)findViewById(R.id.Puntaje);
        virus = (TextView)findViewById(R.id.Virus);
        nivel = (TextView)findViewById(R.id.Nivel);
        stop = (Button)findViewById(R.id.Stop);

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

        pausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aux == 0){
                    detenertimertask();
                    pausar.setText("RESUME");
                    pausa();
                    aux = 1;
                    movimiento = false;
                    reiniciar.setVisibility(View.VISIBLE);
                }else if(aux == 1){
                    comenzarTimer();
                    pausar.setText("PAUSE");
                    pintar();
                    pintarSiguiente();
                    aux = 0;
                    movimiento = true;
                    reiniciar.setVisibility(View.INVISIBLE);
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                stop.setVisibility(View.INVISIBLE);
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

    private void pausa(){
        for(int fila=0;fila<16;fila++){
            for(int columna=0;columna<8;columna++){
                if(dr_mario.matriz[fila][columna] == 0){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 10){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 20){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 30){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 11){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 21){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 31){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 12){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 22){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 32){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 13){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 23){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 33){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 14){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 24){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 34){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 15){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 25){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }else if(dr_mario.matriz[fila][columna] == 35){
                    this.matriz[fila][columna].setImageDrawable(getResources().getDrawable(R.drawable.nada));
                }
            }
        }
        if(dr_mario.imagen == 1){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 2){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 3){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 4){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 5){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 6){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 7){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 8){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }else if(dr_mario.imagen == 9){
            this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.sonicnada));
        }
    }

    private void pintarperder(){
        this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.gameover));
    }

    private void pintarganar(){
        this.sprite.setImageDrawable(getResources().getDrawable(R.drawable.felicidades));
    }

    public void comenzarTimer(){
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
                            reiniciar.setVisibility(View.VISIBLE);
                            movimiento = false;
                            pintarperder();
                            mediaPlayer.stop();
                        }else if(dr_mario.findeljuego){
                            detenertimertask();
                            reiniciar.setVisibility(View.VISIBLE);
                            movimiento = false;
                            pausa();
                            pintarganar();
                            mediaPlayer.stop();
                        }else{
                            dr_mario.abajo();
                            if(!dr_mario.movimiento){
                                movimiento = false;
                            }else{
                                movimiento = true;
                            }
                            pintar();
                            pintarSiguiente();
                            virus.setText("" + dr_mario.virus);
                            puntaje.setText("" + dr_mario.puntaje);
                            nivel.setText("" + dr_mario.numnivel);
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