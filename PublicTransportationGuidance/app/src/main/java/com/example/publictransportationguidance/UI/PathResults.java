package com.example.publictransportationguidance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.Shortest;
import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.ShortestPath;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Tracking.SelectedPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathResults extends AppCompatActivity {

    NumberPicker resultsWheel;
    Intent txtIntent;
    String  pickedValue ;
    TextView tvCost;
    TextView tvDistance;
    public static final String TAG="route";

    String[] transportations={"Bus from Faisal ro Zamalek","Metro from Zamalek to Sheraton","temp1","temp2","temp3","temp4"};
    ArrayList<String> transportationsTemp;

    String SOURCE ="الحي الثامن";
    String DESTINATION ="العباسية";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_results);
        resultsWheel=findViewById(R.id.wheel);
        tvCost=findViewById(R.id.cost);
        tvDistance=findViewById(R.id.distance);

        transportationsTemp =new ArrayList<>();

        RetrofitClient.getInstance().getApi().getShortestByCost(SOURCE, DESTINATION).enqueue(new Callback<List<List<ShortestPath>>>() {
            @Override
            public void onResponse(Call<List<List<ShortestPath>>> call, Response<List<List<ShortestPath>>> response) {
                List<List<ShortestPath>> shortestPathsInCost=response.body();
                tvCost.setText(""+Shortest.getPathCost(shortestPathsInCost,0));
                tvDistance.setText(""+Shortest.getPathDistance(shortestPathsInCost,0));

                    HashMap pathMap=Shortest.pathMap(shortestPathsInCost);

                    int hashMapSize=pathMap.size();
                    for(int pathNum=0;pathNum<hashMapSize;pathNum++){
                        List<String> pathStops = (List<String>) pathMap.get(pathNum);
                        String path=Shortest.pathToPrint(pathStops);
                        transportationsTemp.add(path);
                    }
                    transportations= transportationsTemp.toArray(new String[0]);


                resultsWheel.setMinValue(0);                            /* M Osama: wheel populated starting from index0 from source*/
                resultsWheel.setMaxValue(transportations.length-1);     /* M Osama: wheel populated till index(len-1)*/
                resultsWheel.setValue(0);                               /* M Osama: index0 content represent best result; to be edited after building database & mapping */
                resultsWheel.setDisplayedValues(transportations);       /* M Osama: populating wheel with data */

                resultsWheel.setOnClickListener((View v) -> {
                    txtIntent =new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
                    pickedValue=transportations[resultsWheel.getValue()];     /*haidy: getting the text of the selected index in the NumberPicker*/
                    txtIntent.putExtra(TAG,pickedValue);
                    startActivity(txtIntent);
                });

                resultsWheel.setOnScrollListener((NumberPicker numberPicker, int i) ->{
                    String cost = ""+Shortest.getPathCost(shortestPathsInCost,numberPicker.getValue());
                    String distance = ""+Shortest.getPathDistance(shortestPathsInCost,numberPicker.getValue());
                    tvCost.setText(cost);
                    tvDistance.setText(distance);
                });

            }

            @Override
            public void onFailure(Call<List<List<ShortestPath>>> call, Throwable t) {
            }
        });

        Toast.makeText(this, "For blind their will be a sound", Toast.LENGTH_SHORT).show();
    }

}




