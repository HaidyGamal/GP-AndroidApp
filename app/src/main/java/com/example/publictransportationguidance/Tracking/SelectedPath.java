package com.example.publictransportationguidance.Tracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.POJO.ShortestPathResponse.Shortest;
import com.example.publictransportationguidance.POJO.ShortestPathResponse.ShortestPath;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.SelectedPathBinding;

import java.util.List;

public class SelectedPath extends AppCompatActivity {

    SelectedPathBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.selected_path);

        Intent incoming=getIntent();
        int pathNum = incoming.getIntExtra("pathNum",0);
        List<List<ShortestPath>> shortestPath = (List<List<ShortestPath>>) incoming.getSerializableExtra("shortestPathInCost");
        int size = shortestPath.size();

        String detailedPath = Shortest.getStringDetailedPathToPopulateRoom(shortestPath,size).get(pathNum);
        Toast.makeText(this, detailedPath, Toast.LENGTH_SHORT).show();

        binding.selectedPath.setText(detailedPath);

        binding.startLiveLocationBtn.setOnClickListener((View v)-> { startActivity(new Intent(SelectedPath.this, LiveLocation.class)); });


    }

}
