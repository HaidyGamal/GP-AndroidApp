package com.example.publictransportationguidance.tracking;

import static com.example.publictransportationguidance.helpers.GlobalVariables.BUNDLE_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.INTENT_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.NAVIGATING_TO_LIVE_LOCATION_REQUEST_CODE;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SELECTED_PATH;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.SelectedPathBinding;

import java.io.Serializable;

public class SelectedPath extends AppCompatActivity {
    SelectedPathBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.selected_path);

        binding.selectedPath.setText(getIntent().getStringExtra(SELECTED_PATH));

        /* M Osama: read the path nodes from PathResults */
        Bundle bundle = getIntent().getBundleExtra(INTENT_PATH);

        Serializable data = bundle.getSerializable(BUNDLE_PATH);

        bundle.putSerializable(BUNDLE_PATH,data);

        /* M Osama: pass path nodes to TrackLiveLocation to be viewed on Map */
        Intent intent = new Intent(SelectedPath.this,TrackLiveLocation.class);
        intent.putExtra(INTENT_PATH,bundle);

        binding.startLiveLocationBtn.setOnClickListener( v -> startActivity(intent) );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {

        if (requestCode == NAVIGATING_TO_LIVE_LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ binding.startLiveLocationBtn.setOnClickListener((View v)-> startActivity(new Intent(SelectedPath.this, TrackLiveLocation.class))); }
            else { Toast.makeText(SelectedPath.this,"من فضلك أضغط على زر سماح للموقع",Toast.LENGTH_SHORT).show(); }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
