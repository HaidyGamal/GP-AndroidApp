package com.example.publictransportationguidance.Tracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.SelectedPathBinding;

public class SelectedPath extends AppCompatActivity {
    Intent incoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SelectedPathBinding binding = DataBindingUtil.setContentView(this,R.id.selectedPath);

        incoming=getIntent();
        binding.selectedPath.setText(incoming.getStringExtra(PathResults.TAG));  /* haidy: to write the selected path that passed from PathResults page in the text view*/
        binding.startLiveLocationBtn.setOnClickListener((View v)-> { startActivity(new Intent(SelectedPath.this, LiveLocation.class)); });

    }

}
