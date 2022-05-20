package com.example.drmario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Level extends AppCompatActivity {

    Spinner comboLevels;
    Button start;
    public static int virusLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        comboLevels = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.levels, android.R.layout.simple_spinner_item);

        comboLevels.setAdapter(adapter);

        comboLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                virusLevel = Integer.parseInt(parent.getItemAtPosition(position).toString()) + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        start = findViewById(R.id.StartS);

        this.start.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                ingresarJuego();
            }
        });

    }


    private void ingresarJuego() {
        System.out.println("ssssssssssssssssssssss " + virusLevel);
        Intent intent = new Intent(this, Juego.class);
        startActivity(intent);
    }
}