package com.example.publictransportationguidance.Tracking;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.POJO.PathInfo;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Room.DAO;
import com.example.publictransportationguidance.Room.RoomDB;
import com.example.publictransportationguidance.POJO.ShortestPathResponse.Shortest;
import com.example.publictransportationguidance.POJO.ShortestPathResponse.ShortestPath;
import com.example.publictransportationguidance.databinding.ActivityPathResultsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathResults extends AppCompatActivity {

    Intent txtIntent;
    String pickedValue;

    public static final String TAG="route";

    String[] transportations={"Bus from Faisal to Zamalek","Metro from Zamalek to Sheraton","temp1","temp2","temp3","temp4"};
    ArrayList<String> transportationsTemp=new ArrayList<>();

    String LOCATION ="";
    String DESTINATION ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPathResultsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_path_results);

        /* Could be deleted */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { binding.wheel.setTextSize(60F); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { binding.wheel.setSelectionDividerHeight(0x1c); }
        binding.wheel.setWrapSelectorWheel(true);
        binding.wheel.setMinimumHeight(0xff);
        binding.wheel.setDividerPadding(10000);
        binding.wheel.setElevation(100F);
        /* Could be deleted */

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

                    binding.cost.setText("" + Shortest.getPathCost(shortestPathsInCost, 0));
                    binding.distance.setText("" + Shortest.getPathDistance(shortestPathsInCost, 0));

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
                    transportations=Shortest.listToArray(transportationsTemp);

                    /* M Osama: Identifying wheel settings*/
                    binding.wheel.setMinValue(0);                            /* M Osama: wheel populated starting from index0 from source*/
                    binding.wheel.setMaxValue(transportations.length - 1);   /* M Osama: wheel populated till index(len-1)*/
                    binding.wheel.setValue(0);                               /* M Osama: index0 content represent best result; to be edited after building database & mapping */
                    binding.wheel.setDisplayedValues(transportations);       /* M Osama: populating wheel with data */

                    binding.wheel.setOnClickListener((View v) -> {
                        txtIntent = new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
                        pickedValue = transportations[binding.wheel.getValue()];     /*haidy: getting the text of the selected index in the NumberPicker*/
                        txtIntent.putExtra(TAG, pickedValue);
                        startActivity(txtIntent);
                    });

                    /* M Osama: track costs & distances of selected Path */
                    binding.wheel.setOnScrollListener((NumberPicker numberPicker, int i) -> {
                        String cost = "" + dao.getPathToPopulateWheel(numberPicker.getValue()).getCost();
                        String distance = "" + dao.getPathToPopulateWheel(numberPicker.getValue()).getDistance();
                        binding.cost.setText(cost);
                        binding.distance.setText(distance);
                    });

                    /* M Osama: Sort by cost */
                    binding.costRBPathResults.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, R.string.PathsSortedAccordingToCost, Toast.LENGTH_SHORT).show();
                    });

                    /* M Osama: Sort by distance */
                    binding.distanceRBPathResults.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, R.string.PathsSortedAccordingToDistance, Toast.LENGTH_SHORT).show();
                    });

                }
                else{
                    binding.cost.setText(R.string.NotAvailable);
                    binding.distance.setText(R.string.NotAvailable);
                    String[] temp={getString(R.string.NoAvailablePathsToBePresented)};
                    binding.wheel.setDisplayedValues(temp);

                    /* M Osama: Sort by cost */
                    binding.costRBPathResults.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, "لا يوجد طرق لترتيبهم", Toast.LENGTH_SHORT).show();
                    });

                    /* M Osama: Sort by distance */
                    binding.distanceRBPathResults.setOnClickListener((View view) -> {
                        Toast.makeText(PathResults.this, "لا يوجد طرق لترتيبهم", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<List<ShortestPath>>> call, Throwable t) {
                binding.cost.setText(R.string.NotAvailable);
                binding.distance.setText(R.string.NotAvailable);
                String[] temp={getString(R.string.NoAvailablePathsToBePresented)};
                binding.wheel.setDisplayedValues(temp);
            }
        });

    }

}




