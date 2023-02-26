package com.example.publictransportationguidance.Tracking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.LiveLocationBinding;

public class LiveLocation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiveLocationBinding binding = DataBindingUtil.setContentView(this,R.layout.live_location);

        binding.stopBtn.setText("Stop");
        binding.stopBtn.setOnClickListener((View v)-> {
            if(binding.stopBtn.getText()=="Stop"){ binding.stopBtn.setText("Continue"); }
            else binding.stopBtn.setText("Stop");
        });
}}
