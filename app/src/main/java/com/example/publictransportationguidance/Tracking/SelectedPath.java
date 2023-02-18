package com.example.publictransportationguidance.Tracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.publictransportationguidance.UI.PathResults;
import com.example.publictransportationguidance.R;

public class SelectedPath extends AppCompatActivity {
    Intent incoming,intent;
    TextView path;
    Button startLiveLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_path);
        incoming=getIntent();
        path=findViewById(R.id.txtpath);
        path.setText(incoming.getStringExtra(PathResults.TAG));  /* haidy: to write the selected path that passed from PathResults page in the text view*/
        startLiveLoc=findViewById(R.id.start_btn);
        startLiveLoc.setOnClickListener((View v)-> {
                intent=new Intent(SelectedPath.this, LiveLocation.class);
                startActivity(intent);
        });
    }

}
