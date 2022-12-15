package com.example.publictransportationguidance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.publictransportationguidance.API.POJO.StopsResponse.StopModel;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Room.RoomDB;
import com.example.publictransportationguidance.Tracking.SelectedPath;

import java.util.ArrayList;
import java.util.List;

public class PathResults extends AppCompatActivity {

    NumberPicker resultsWheel;
    Intent txtIntent;
    String  pickedValue ;
    public static final String TAG="route";

    String[] transportations={"Bus from Faisal ro Zamalek","Metro from Zamalek to Sheraton","temp1","temp2","temp3","temp4"};

    ArrayList<String> transportationsTwoTemp =new ArrayList<>();
    public static String[] transportationsTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_results);
        resultsWheel=findViewById(R.id.wheel);

        //for(StopModel tempStop : stops){ transportationsTwoTemp.add(tempStop.getName()); }
        //transportationsTwo=transportationsTwoTemp.toArray(new String[0]);

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

//        RetrofitClient.getInstance().getApi().getShortestPath("صلاح سالم","شيراتون").enqueue(new Callback<ShortestPath>() {
//            @Override
//            public void onResponse(Call<ShortestPath> call, Response<ShortestPath> response) {
//                ShortestPath shortestPathInfo = response.body();
//
//                List<List<Shortest>> routeList=shortestPathInfo.getRoutes();
//                List<Shortest> route = routeList.get(0);
//
//                String path="";
//                for (Shortest station : route) { path+=(station.getName()+" -> "); }
//                path+=shortestPathInfo.getEndNode();
//
//                /* M Osama: Wheel datasource representing transportations using array of String to be replaced with database content */
//                String[] transportations={path,"Bus from Faisal ro Zamalek","Metro from Zamalek to Sheraton","temp1","temp2","temp3","temp4"};
//
//                resultsWheel.setMinValue(0);                            /* M Osama: wheel populated starting from index0 from source*/
//                resultsWheel.setMaxValue(transportations.length-1);     /* M Osama: wheel populated till index(len-1)*/
//                resultsWheel.setValue(0);                               /* M Osama: index0 content represent best result; to be edited after building database & mapping */
//                resultsWheel.setDisplayedValues(transportations);       /* M Osama: populating wheel with data */
//
//                resultsWheel.setOnClickListener((View v) -> {
//                    txtIntent =new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
//                    pickedValue=transportations[resultsWheel.getValue()];     /*haidy: getting the text of the selected index in the NumberPicker*/
//                    txtIntent.putExtra(TAG,pickedValue);
//                    startActivity(txtIntent);
//                });
//            }
//
//            @Override
//            public void onFailure(Call<ShortestPath> call, Throwable t) { Log.i("5555555555555555555555555555555555555555555","Hi"); }
//        });

        Toast.makeText(this, "For blind their will be a sound", Toast.LENGTH_SHORT).show();
    }



}