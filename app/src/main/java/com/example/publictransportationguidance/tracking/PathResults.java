package com.example.publictransportationguidance.tracking;

import static com.example.publictransportationguidance.helpers.GlobalVariables.BUNDLE_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.INTENT_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SELECTED_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SEARCH_BY_COST;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.helpers.GlobalVariables;
import com.example.publictransportationguidance.pojo.PathInfo;
import com.example.publictransportationguidance.api.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.room.DAO;
import com.example.publictransportationguidance.room.RoomDB;
import com.example.publictransportationguidance.pojo.shortestPathResponse.Shortest;
import com.example.publictransportationguidance.pojo.shortestPathResponse.ShortestPath;
import com.example.publictransportationguidance.databinding.ActivityPathResultsBinding;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathResults extends AppCompatActivity {
    ActivityPathResultsBinding binding;
    DAO dao;

    Intent intentToSelectedPath;
    String pickedPathNodes;
    String pickedPathDetails;

    String[] wheelTransportations ={};
    ArrayList<String> transportationsTemp = new ArrayList<>();

    String LOCATION = "";
    String DESTINATION = "";


    /* M Osama: contains lats & longs for every location in the returned paths from our API */
    public static ArrayList<ArrayList<LatLng>> pathsNodesLatLng = new ArrayList<>();
    ArrayList<LatLng> tempPathNodesLatLng=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_path_results);

        /* M Osama: reading values from homeFragment */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            LOCATION = extras.getString("LOCATION");
            DESTINATION = extras.getString("DESTINATION");
        }

        dao = RoomDB.getInstance(getApplication()).Dao();

        if (SEARCH_BY_COST)  getShortestPathsSortedByCost(LOCATION, DESTINATION);
        else                 getShortestPathsSortedByDistance(LOCATION, DESTINATION);

    }


    public void getShortestPathsSortedByCost(String location, String destination) {
        RetrofitClient.getInstance().getApi().getShortestByCost(location, destination).enqueue(new Callback<List<List<ShortestPath>>>() {
            @Override
            public void onResponse(@NonNull Call<List<List<ShortestPath>>> call, @NonNull Response<List<List<ShortestPath>>> response) {
                List<List<ShortestPath>> shortestPathsInCost = response.body();

                if (shortestPathsInCost != null) {
                    if (shortestPathsInCost.size() > 0) {
                        storeLatLngOfEveryNodeInReturnedPaths(shortestPathsInCost,getApplicationContext());   /* M Osama: store latLng of every node in each path to be able to use them to draw default path on map*/
                        searchingForResultsToast();
                        initCostAndDistance(shortestPathsInCost);                   /* M Osama: put cost & distance of the best root */
                        HashMap pathMap = Shortest.pathMap(shortestPathsInCost);    /* M Osama: give each Path a number */
                        cachingToRoom(pathMap,shortestPathsInCost);                 /* M Osama: caching Paths to Room */
                        wheelTransportations = feedWheel();                         /* M Osama: Build String Array from cachedPath Info */
                        identifyingWheelSettings();                                 /* M Osama: Identifying wheel settings*/
                        wheelOnClickListener(shortestPathsInCost);                  /* M Osama: onClickListener for wheel representing choices */
                        costAndDistanceTracker();                                   /* M Osama: textViews tracking currentPath cost & distance */
                        sortingClickListener();                                     /* M Osama: onClickListener for two sorting Buttons */
                    }
                    else { noPathsFound();  finish();  noAvailablePathsToast(); }
                }
                else { checkInternetConnectionToast(); }

            }

            @Override
            public void onFailure(@NonNull Call<List<List<ShortestPath>>> call, @NonNull Throwable t) { noPathsFound(); }
        });
    }
    public void getShortestPathsSortedByDistance(String location, String destination) {
        RetrofitClient.getInstance().getApi().getShortestByDistance(location, destination).enqueue(new Callback<List<List<ShortestPath>>>() {
            @Override
            public void onResponse(@NonNull Call<List<List<ShortestPath>>> call, @NonNull Response<List<List<ShortestPath>>> response) {
                List<List<ShortestPath>> shortestPathsInCost = response.body();

                if (shortestPathsInCost != null) {
                    if (shortestPathsInCost.size() > 0) {
                        storeLatLngOfEveryNodeInReturnedPaths(shortestPathsInCost,getApplicationContext());   /* M Osama: store latLng of every node in each path to be able to use them to draw default path on map*/
                        searchingForResultsToast();
                        initCostAndDistance(shortestPathsInCost);                   /* M Osama: put cost & distance of the best root */
                        HashMap pathMap = Shortest.pathMap(shortestPathsInCost);    /* M Osama: give each path a number*/
                        cachingToRoom(pathMap,shortestPathsInCost);                 /* M Osama: caching Paths to Room */
                        wheelTransportations = feedWheel();                         /* M Osama: Build String Array from cachedPath Info To be fed to Wheel */
                        identifyingWheelSettings();                                 /* M Osama: Identifying wheel settings*/
                        wheelOnClickListener(shortestPathsInCost);                  /* M Osama: onClickListener for wheel representing choices */
                        costAndDistanceTracker();                                   /* M Osama: textViews tracking currentPath cost & distance */
                        sortingClickListener();                                     /* M Osama: onClickListener for two sorting Buttons */
                    } else { noPathsFound();  finish();  noAvailablePathsToast(); }
                } else { checkInternetConnectionToast(); }

            }

            @Override
            public void onFailure(@NonNull Call<List<List<ShortestPath>>> call, @NonNull Throwable t) { noPathsFound(); }
        });
    }


    public void identifyingWheelSettings() {
        binding.wheel.setMinValue(0);                            /* M Osama: wheel populated starting from index0 from source*/
        binding.wheel.setMaxValue(wheelTransportations.length - 1);   /* M Osama: wheel populated till index(len-1)*/
        binding.wheel.setValue(0);                               /* M Osama: index0 content represent best result; to be edited after building database & mapping */
        binding.wheel.setDisplayedValues(wheelTransportations);       /* M Osama: populating wheel with data */
    }



    public void noPathsFound(){
        binding.cost.setText(R.string.NotAvailable);
        binding.distance.setText(R.string.NotAvailable);
        String[] temp = {"لا يوجد طريق متوفر","لا يوجد طريق متوفر","لا يوجد طريق متوفر"};
        binding.wheel.setDisplayedValues(temp);
    }

    public void sortingClickListener(){
        /* M Osama: Sort by cost */
        binding.costRBPathResults.setOnClickListener((View view) -> Toast.makeText(PathResults.this, R.string.PathsSortedAccordingToCost, Toast.LENGTH_SHORT).show());
        /* M Osama: Sort by distance */
        binding.distanceRBPathResults.setOnClickListener((View view) -> Toast.makeText(PathResults.this, R.string.PathsSortedAccordingToDistance, Toast.LENGTH_SHORT).show());

    }
    public void wheelOnClickListener(List<List<ShortestPath>> shortestPaths){
        binding.wheel.setOnClickListener((View v) -> {
            intentToSelectedPath = new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
            Bundle bundle = new Bundle();

            int numOfSelectedPath = binding.wheel.getValue();

            pickedPathNodes = wheelTransportations[numOfSelectedPath];     /*haidy: getting the text of the selected index in the NumberPicker*/

            pickedPathDetails = Shortest.detailedPathToPrint(Shortest.stopsAndMeans(shortestPaths,numOfSelectedPath));

            /* M Osama: to track which path the user chosen */
            GlobalVariables.selectedPathNumberInWheel=numOfSelectedPath;

            /* M Osama: pass the nodes of selectedPath to be able to draw them on Map */
            bundle.putSerializable(BUNDLE_PATH,pathsNodesLatLng.get(numOfSelectedPath));
            intentToSelectedPath.putExtra(INTENT_PATH,bundle);

            /* M Osama: pass Path details to print them on screen */
            intentToSelectedPath.putExtra(SELECTED_PATH,pickedPathDetails);

            Toast.makeText(PathResults.this, pickedPathNodes, Toast.LENGTH_SHORT).show();

            startActivity(intentToSelectedPath);
        });
    }

    public void noAvailablePathsToast(){
        Toast.makeText(PathResults.this, "نأسف لعدم وجود طريق متوفرة", Toast.LENGTH_SHORT).show();
    }
    public void checkInternetConnectionToast(){
        Toast.makeText(PathResults.this, R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show();
    }
    public void searchingForResultsToast(){
        Toast.makeText(PathResults.this, R.string.SearchingForPaths, Toast.LENGTH_SHORT).show();
    }

    public void initCostAndDistance(List<List<ShortestPath>> shortestPaths){
        binding.cost.setText("" + Shortest.getPathCost(shortestPaths, 0));
        binding.distance.setText("" + Shortest.getPathDistance(shortestPaths, 0));
    }
    public void costAndDistanceTracker(){
        /* M Osama: track costs & distances of selected Path */
        binding.wheel.setOnScrollListener((com.shawnlin.numberpicker.NumberPicker numberPicker, int i) -> {
            String cost = "" + dao.getPathToPopulateWheel(numberPicker.getValue()).getCost();
            String distance = "" + dao.getPathToPopulateWheel(numberPicker.getValue()).getDistance();
            binding.cost.setText(cost);
            binding.distance.setText(distance);
        });
    }

    public void cachingToRoom(HashMap pathMap,List<List<ShortestPath>> shortestPaths){
        /*ToBe Deleted*/
        dao.deleteAllPaths();

        /* M Osama: caching Paths in Room (only if PathsTable is empty) */
        if (dao.getNumberOfRowsOfPathsTable() == 0) {
            for (int pathNum = 0; pathNum < pathMap.size(); pathNum++) {
                double tempDistance = Shortest.getPathDistance(shortestPaths, pathNum);
                int tempCost = Shortest.getPathCost(shortestPaths, pathNum);
                String tempPath = Shortest.getStringPathToPopulateRoom(pathMap).get(pathNum);
                PathInfo pathInfo = new PathInfo(pathNum, tempDistance, tempCost, tempPath);
                dao.insertPath(pathInfo);
            }
        }
    }
    public String[] feedWheel(){
        /* M Osama: Build String Array from cachedPath Info */
        for (int pathNum = 0; pathNum < dao.getNumberOfRowsOfPathsTable(); pathNum++) {
            PathInfo info = dao.getPathToPopulateWheel(pathNum);
            transportationsTemp.add(info.getPath());
        }
        return Shortest.listToArray(transportationsTemp);
    }

    public void storeLatLngOfEveryNodeInReturnedPaths(List<List<ShortestPath>> shortestPaths, Context context){
        int numberOfReturnedPaths = shortestPaths.size();
        for(int pathNumber=0;pathNumber<numberOfReturnedPaths;pathNumber++){

            int numberOfNodesInPath = shortestPaths.get(pathNumber).size();
            for(int nodeNumber=0;nodeNumber<numberOfNodesInPath;nodeNumber++) {

                double latitude = shortestPaths.get(pathNumber).get(nodeNumber).getLatitude();
                double longitude = shortestPaths.get(pathNumber).get(nodeNumber).getLongitude();

                tempPathNodesLatLng.add(nodeNumber,new LatLng(latitude,longitude));

            }
            pathsNodesLatLng.add(pathNumber, tempPathNodesLatLng);
        }
        Toast.makeText(context,"Size: "+pathsNodesLatLng.size(),Toast.LENGTH_LONG);
    }

}
