package com.example.publictransportationguidance.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.publictransportationguidance.API.POJO.PathInfo;
import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.Shortest;
import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.ShortestPath;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Room.DAO;
import com.example.publictransportationguidance.Room.RoomDB;
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
    RadioButton costRadioBtn;
    RadioButton distanceRadioBtn;

    public static final String TAG="route";

    String[] transportations={"Bus from Faisal ro Zamalek","Metro from Zamalek to Sheraton","temp1","temp2","temp3","temp4"};
    ArrayList<String> transportationsTemp=new ArrayList<>();

    String LOCATION ="";
    String DESTINATION ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_results);
        resultsWheel=findViewById(R.id.wheel);
        tvCost=findViewById(R.id.cost);
        tvDistance=findViewById(R.id.distance);
        costRadioBtn=findViewById(R.id.costRB_pathResults);
        distanceRadioBtn=findViewById(R.id.distanceRB_pathResults);

        /* M Osama: reading values from homeFragment */
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            LOCATION=extras.getString("LOCATION");
            DESTINATION=extras.getString("DESTINATION");
        }

        DAO dao = RoomDB.getInstance(getApplication()).Dao();

        RetrofitClient.getInstance().getApi().getShortestByCost(LOCATION, DESTINATION).enqueue(new Callback<List<List<ShortestPath>>>() {
            @Override
            public void onResponse(Call<List<List<ShortestPath>>> call, Response<List<List<ShortestPath>>> response) {
                List<List<ShortestPath>> shortestPathsInCost=response.body();

                if(shortestPathsInCost.size()>0) {

                    tvCost.setText("" + Shortest.getPathCost(shortestPathsInCost, 0));
                    tvDistance.setText("" + Shortest.getPathDistance(shortestPathsInCost, 0));

                    HashMap pathMap = Shortest.pathMap(shortestPathsInCost);

                    /*ToBe Deleted*/
                    dao.deleteAllPaths();

                    /* M Osama: caching Paths in Room (only if PathsTable is empty) */
                    if (dao.getNumberOfRowsOfPathsTable() == 0) {
                        for (int pathNum = 0; pathNum < pathMap.size(); pathNum++) {
                            double tempDistance = Shortest.getPathDistance(shortestPathsInCost, pathNum);
                            int tempCost = Shortest.getPathCost(shortestPathsInCost, pathNum);
                            String tempPath = Shortest.getStringPathToPopulateRoom(pathMap).get(pathNum);
                            PathInfo pathInfo = new PathInfo(pathNum, tempDistance, tempCost, tempPath);
                            dao.insertPath(pathInfo);
                        }
                    }

                    /* M Osama: Build String Array from cachedPath Info */
                    for (int pathNum = 0; pathNum < dao.getNumberOfRowsOfPathsTable(); pathNum++) {
                        PathInfo info = dao.getPathToPopulateWheel(pathNum);
                        transportationsTemp.add(info.getPath());
                    }
                    transportations = transportationsTemp.toArray(new String[0]);

                    /* M Osama: Identifying wheel settings*/
                    resultsWheel.setMinValue(0);                            /* M Osama: wheel populated starting from index0 from source*/
                    resultsWheel.setMaxValue(transportations.length - 1);     /* M Osama: wheel populated till index(len-1)*/
                    resultsWheel.setValue(0);                               /* M Osama: index0 content represent best result; to be edited after building database & mapping */
                    resultsWheel.setDisplayedValues(transportations);       /* M Osama: populating wheel with data */

                    resultsWheel.setOnClickListener((View v) -> {
                        txtIntent = new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
                        pickedValue = transportations[resultsWheel.getValue()];     /*haidy: getting the text of the selected index in the NumberPicker*/
                        txtIntent.putExtra(TAG, pickedValue);
                        startActivity(txtIntent);
                    });

                    /* M Osama: track costs & distances of selected Path */
                    resultsWheel.setOnScrollListener((NumberPicker numberPicker, int i) -> {
                        String cost = "" + dao.getPathToPopulateWheel(numberPicker.getValue()).getCost();
                        String distance = "" + dao.getPathToPopulateWheel(numberPicker.getValue()).getDistance();
                        tvCost.setText(cost);
                        tvDistance.setText(distance);
                    });

                    /* M Osama: Sort by cost */
                    costRadioBtn.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, "يتم الترتيب طبقا للتكلفة", Toast.LENGTH_SHORT).show();
                    });

                    /* M Osama: Sort by distance */
                    distanceRadioBtn.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, "يتم الترتيب طبقا للمسافة", Toast.LENGTH_SHORT).show();
                    });

                }
                else{
                    tvCost.setText("غير متوفر");
                    tvDistance.setText("غير متوفر");
                    String[] temp={"عذرا، لا يوجد طريق متوفر"};
                    resultsWheel.setDisplayedValues(temp);

                    /* M Osama: Sort by cost */
                    costRadioBtn.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, "لا يوجد طرق لترتيبهم", Toast.LENGTH_SHORT).show();
                    });

                    /* M Osama: Sort by distance */
                    distanceRadioBtn.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, "لا يوجد طرق لترتيبهم", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<List<ShortestPath>>> call, Throwable t) {
                tvCost.setText("غير متوفر");
                tvDistance.setText("غير متوفر");
                String[] temp={"عذرا، لا يوجد طريق متوفر"};
                resultsWheel.setDisplayedValues(temp);
            }
        });

    }

}




