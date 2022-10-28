package com.example.publictransportationguidance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

public class PathResults extends AppCompatActivity {

    /* M Osama: Wheel datasource representing transportations using array of String to be replaced with database content */
    String[] transportations={"Microbus from Ramses to Helwna","Bus from Faisal ro Zamalek","Metro from Zamalek to Sheraton","temp1","temp2","temp3","temp4"};

    NumberPicker resultsWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_results);

        resultsWheel=findViewById(R.id.wheel);
        resultsWheel.setMinValue(0);                            /* M Osama: wheel populated starting from index0 from source*/
        resultsWheel.setMaxValue(transportations.length-1);     /* M Osama: wheel populated till index(len-1)*/
        resultsWheel.setValue(2);                               /* M Osama: index2 content represent best result; to be edited after building database & mapping */
        resultsWheel.setDisplayedValues(transportations);       /* M Osama: populating wheel with data */

        Toast.makeText(this, "For blind their will be a sound", Toast.LENGTH_SHORT).show();
    }

}