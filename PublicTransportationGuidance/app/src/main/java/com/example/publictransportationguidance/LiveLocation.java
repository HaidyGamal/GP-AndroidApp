package com.example.publictransportationguidance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LiveLocation extends AppCompatActivity {
    Button stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_location);
        stop=findViewById(R.id.stop_btn);
        stop.setText("Stop");
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stop.getText()=="Stop"){
                stop.setText("Continue");}
                else stop.setText("Stop");
            }
        });
}}
