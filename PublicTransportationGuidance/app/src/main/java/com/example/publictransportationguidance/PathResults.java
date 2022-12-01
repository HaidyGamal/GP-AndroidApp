package com.example.publictransportationguidance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.Route;
import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.ShortestPathInfo;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.Tracking.SelectedPath;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathResults extends AppCompatActivity {

    NumberPicker resultsWheel;
    Intent txtIntent;
    String  pickedValue ;
    public static final String TAG="route";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_results);
        resultsWheel=findViewById(R.id.wheel);


        RetrofitClient.getInstance().getApi().getShortestPath("صلاح سالم","شيراتون").enqueue(new Callback<ShortestPathInfo>() {
            @Override
            public void onResponse(Call<ShortestPathInfo> call, Response<ShortestPathInfo> response) {
                ShortestPathInfo shortestPathInfo = response.body();

                List<List<Route>> routeList=shortestPathInfo.getRoutes();
                List<Route> route = routeList.get(0);

                String path="";
                for (Route station : route) { path+=(station.getName()+" -> "); }
                path+=shortestPathInfo.getEndNode();

                /* M Osama: Wheel datasource representing transportations using array of String to be replaced with database content */
                String[] transportations={path,"Bus from Faisal ro Zamalek","Metro from Zamalek to Sheraton","temp1","temp2","temp3","temp4"};

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
            }

            @Override
            public void onFailure(Call<ShortestPathInfo> call, Throwable t) { Log.i("5555555555555555555555555555555555555555555","Hi"); }
        });

        Toast.makeText(this, "For blind their will be a sound", Toast.LENGTH_SHORT).show();
    }



}