package com.example.publictransportationguidance.tracking;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static com.example.publictransportationguidance.api.Api.NEO4J_BASE_URL;
import static com.example.publictransportationguidance.helpers.Functions.calcEstimatedTripsTime;
import static com.example.publictransportationguidance.helpers.Functions.checkInternetConnectionToast;
import static com.example.publictransportationguidance.helpers.Functions.noAvailablePathsToast;
import static com.example.publictransportationguidance.helpers.Functions.searchingForResultsToast;
import static com.example.publictransportationguidance.helpers.Functions.sortingByCostToast;
import static com.example.publictransportationguidance.helpers.Functions.sortingByDistanceToast;
import static com.example.publictransportationguidance.helpers.Functions.sortingByTimeToast;
import static com.example.publictransportationguidance.helpers.Functions.tryAgainToast;
import static com.example.publictransportationguidance.helpers.GlobalVariables.BUNDLE_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.INTENT_PATH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SELECTED_PATH;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.TripsTimeCallback;
import com.example.publictransportationguidance.helpers.GlobalVariables;
import com.example.publictransportationguidance.pojo.pathsResponse.PathInfo;
import com.example.publictransportationguidance.api.RetrofitClient;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.room.DAO;
import com.example.publictransportationguidance.room.RoomDB;
import com.example.publictransportationguidance.helpers.PathsTokenizer;
import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;
import com.example.publictransportationguidance.databinding.ActivityPathResultsBinding;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathResults extends AppCompatActivity implements TripsTimeCallback {
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

        getNearestPaths(LOCATION,DESTINATION,this,this);

    }


    public void getNearestPaths(String location, String destination, Activity activity,TripsTimeCallback callback) {

        RetrofitClient.getInstance(NEO4J_BASE_URL).getApi().getNearestPaths(location,destination).enqueue(new Callback<List<List<NearestPaths>>>() {
            @Override
            public void onResponse(@NonNull Call<List<List<NearestPaths>>> call, @NonNull Response<List<List<NearestPaths>>> response) {
                List<List<NearestPaths>> paths = response.body();

                if (paths != null) {
                    if (paths.size() > 0) {
                        storeLatLngOfEveryNodeInReturnedPaths(paths,getApplicationContext());   /* M Osama: store latLng of every node in each path to be able to use them to draw default path on map*/
                        searchingForResultsToast(getApplicationContext());
                        initCostAndDistance(paths);                                             /* M Osama: put cost,distance,time of the best root */
                        HashMap pathMap = PathsTokenizer.pathMap(paths);                         /* M Osama: give each Path a number */
                        cachingToRoom(pathMap,paths);                                           /* M Osama: caching Paths to Room */
                        wheelTransportations = feedWheel();                                     /* M Osama: Build String Array from cachedPath Info */
                        identifyingWheelSettings();                                         /* M Osama: Identifying wheel settings*/
                        wheelOnClickListener(paths);                                     /* M Osama: onClickListener for wheel representing choices */
                        costDistanceTimeTracker();                                           /* M Osama: textViews tracking currentPath cost & distance */
                        sortingClickListener();                                             /* M Osama: onClickListener for two sorting Buttons */
                        calcEstimatedTripsTime(LOCATION,DESTINATION,paths.size(),paths,activity,callback);
                    }
                    else { tryAgainToast(getApplicationContext());  noPathsFound();  noAvailablePathsToast(getApplicationContext()); }
                }
                else { checkInternetConnectionToast(getApplicationContext()); noPathsFound(); }

            }

            @Override
            public void onFailure(@NonNull Call<List<List<NearestPaths>>> call, @NonNull Throwable t) { noPathsFound(); }
        });

    }

    public void identifyingWheelSettings() {
        binding.wheel.setMinValue(0);                                   /* M Osama: wheel populated starting from index0 from source*/
        binding.wheel.setMaxValue(wheelTransportations.length - 1);     /* M Osama: wheel populated till index(len-1)*/
        binding.wheel.setValue(0);                                      /* M Osama: index0 content represent best result; to be edited after building database & mapping */
        binding.wheel.setDisplayedValues(wheelTransportations);         /* M Osama: populating wheel with data */
    }

    public void noPathsFound(){
        binding.cost.setText(R.string.NotAvailable);
        binding.distance.setText(R.string.NotAvailable);
        binding.time.setText(R.string.NotAvailable);

        String[] temp = new String[100];
        Arrays.fill(temp, "لا يوجد طريق متوفر");

        binding.wheel.setDisplayedValues(temp);
    }

    public void sortingClickListener(){
        binding.costRBPathResults.setOnClickListener((View view) -> sortingByCostToast(this));          /* M Osama: Sort by cost */
        binding.distanceRBPathResults.setOnClickListener((View view) -> sortingByDistanceToast(this));  /* M Osama: Sort by distance */
        binding.timeRBPathResults.setOnClickListener((View view) -> sortingByTimeToast(this));          /* M Osama: Sort by time */
    }

    public void wheelOnClickListener(List<List<NearestPaths>> shortestPaths){
        binding.wheel.setOnClickListener((View v) -> {
            intentToSelectedPath = new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
            Bundle bundle = new Bundle();

            int numOfSelectedPath = binding.wheel.getValue();

            pickedPathNodes = wheelTransportations[numOfSelectedPath];     /*haidy: getting the text of the selected index in the NumberPicker*/

            pickedPathDetails = PathsTokenizer.detailedPathToPrint(PathsTokenizer.stopsAndMeans(shortestPaths,numOfSelectedPath));

            /* M Osama: to track which path the user chosen */
            GlobalVariables.selectedPathNumberInWheel=numOfSelectedPath;

            /* M Osama: pass the nodes of selectedPath to be able to draw them on Map */
            bundle.putSerializable(BUNDLE_PATH,pathsNodesLatLng.get(numOfSelectedPath));
            intentToSelectedPath.putExtra(INTENT_PATH,bundle);

            /* M Osama: pass Path details to print them on screen */
            intentToSelectedPath.putExtra(SELECTED_PATH,pickedPathDetails);

            Toast.makeText(PathResults.this, pickedPathNodes, Toast.LENGTH_SHORT).show();
//            Toast.makeText(PathResults.this,pickedPathDetails, Toast.LENGTH_SHORT).show();
            Log.i("Hona",pickedPathDetails);
            Log.i("Hona",pickedPathNodes);
            Log.i("Hona",numOfSelectedPath+"");
            Log.i("Hona",pathsNodesLatLng.get(numOfSelectedPath)+"");

            startActivity(intentToSelectedPath);
        });
    }

    public void initCostAndDistance(List<List<NearestPaths>> shortestPaths){
        binding.cost.setText("" + PathsTokenizer.getPathCost(shortestPaths, 0));
        binding.distance.setText("" + PathsTokenizer.getPathDistance(shortestPaths, 0));
        /* binding.time is written down in onTripsTimeCalculated */
    }

    public void costDistanceTimeTracker() {
        binding.wheel.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            updateCostDistanceTime(newValue);
        });

        binding.wheel.setOnScrollListener((numberPicker, scrollState) -> {
            if (scrollState == SCROLL_STATE_IDLE) {
                int currentValue = binding.wheel.getValue();
                updateCostDistanceTime(currentValue);
            }
        });
    }

    private void updateCostDistanceTime(int value) {
        String cost = "" + dao.getPathToPopulateWheel(value).getCost();
        String distance = "" + dao.getPathToPopulateWheel(value).getDistance();
        String time = "" + dao.getPathToPopulateWheel(value).getTime();
        binding.cost.setText(cost);
        binding.distance.setText(distance);
        binding.time.setText(time);
    }

    public void cachingToRoom(HashMap pathMap,List<List<NearestPaths>> shortestPaths){
        /*ToBe Deleted*/
        dao.deleteAllPaths();

        /* M Osama: caching Paths in Room (only if PathsTable is empty) */
        if (dao.getNumberOfRowsOfPathsTable() == 0) {
            for (int pathNum = 0; pathNum < pathMap.size(); pathNum++) {
                double tempDistance = PathsTokenizer.getPathDistance(shortestPaths, pathNum);
                int tempCost = PathsTokenizer.getPathCost(shortestPaths, pathNum);
                /* int tempTime is written down at onTripsTimeCalculated */
                String tempPath = PathsTokenizer.getStringPathToPopulateRoom(pathMap).get(pathNum);
                PathInfo pathInfo = new PathInfo(pathNum, tempDistance,tempCost, 0, tempPath);
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
        return PathsTokenizer.listToArray(transportationsTemp);
    }

    public void storeLatLngOfEveryNodeInReturnedPaths(List<List<NearestPaths>> shortestPaths, Context context){
        int numberOfReturnedPaths = shortestPaths.size();
        for(int pathNumber=0;pathNumber<numberOfReturnedPaths;pathNumber++){
            int numberOfNodesInPath = shortestPaths.get(pathNumber).size();
//            tempPathNodesLatLng.clear();
            for(int nodeNumber=0;nodeNumber<numberOfNodesInPath;nodeNumber++) {
                double latitude = shortestPaths.get(pathNumber).get(nodeNumber).getLatitude();
                double longitude = shortestPaths.get(pathNumber).get(nodeNumber).getLongitude();
                tempPathNodesLatLng.add(nodeNumber,new LatLng(latitude,longitude));
            }
            pathsNodesLatLng.add(pathNumber, tempPathNodesLatLng);
        }
        Toast.makeText(context,"Size: "+pathsNodesLatLng.size(),Toast.LENGTH_LONG);
    }

    @Override
    public void onTripsTimeCalculated(int[] globalDurations) {
        for (int pathNumber = 0; pathNumber < globalDurations.length; pathNumber++) {
            Log.i("Messi","Path " + pathNumber + " duration: " + globalDurations[pathNumber]);
            dao.updatePathTime(pathNumber,globalDurations[pathNumber]);
        }
        binding.time.setText(globalDurations[0]+"");
    }

}
