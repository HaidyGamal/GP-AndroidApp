package com.example.publictransportationguidance.Tracking;

import static com.example.publictransportationguidance.HelperClasses.Constants.NAVIGATING_TO_LIVE_LOCATION_REQUEST_CODE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

        binding.startLiveLocationBtn.setOnClickListener(view -> startActivity(new Intent(SelectedPath.this, LiveLocation.class)) );
    }

//    private void trackLiveLocation(){
//        askPermission();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { startActivity(new Intent(SelectedPath.this, LiveLocation.class)); }
//        else  askPermission();
//    }
//
//    private void askPermission() {
//        ActivityCompat.requestPermissions(SelectedPath.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == NAVIGATING_TO_LIVE_LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ binding.startLiveLocationBtn.setOnClickListener((View v)-> { startActivity(new Intent(SelectedPath.this, LiveLocation.class)); }); }
            else { Toast.makeText(SelectedPath.this,"من فضلك أضغط على زر سماح للموقع",Toast.LENGTH_SHORT).show(); }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
