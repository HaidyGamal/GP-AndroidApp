package com.example.publictransportationguidance.Tracking;

import static com.example.publictransportationguidance.HelperClasses.Constants.PATH;
import static com.example.publictransportationguidance.HelperClasses.Constants.SEARCH_BY_COST;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.HelperClasses.Functions;
import com.example.publictransportationguidance.POJO.Nearby.Nearby;
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
    ActivityPathResultsBinding binding;
    DAO dao;

    Intent txtIntent;
    String pickedPathNodes;
    String pickedPathDetails;

    String[] wheelTransportations ={};
    ArrayList<String> transportationsTemp = new ArrayList<>();

    String LOCATION = "";
    String DESTINATION = "";

    String locNear="30.02735999999999,31.2559448";
    String destNear="30.0154402,31.2118712";

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

        getNearby(locNear,destNear);

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
                        searchingForResulsToast();
                        initCostAndDistance(shortestPathsInCost);                   /* M Osama: put cost & distance of the best root */
                        HashMap pathMap = Shortest.pathMap(shortestPathsInCost);    /* M Osama: give each Path a number */
                        cachingToRoom(pathMap,shortestPathsInCost);                 /* M Osama: caching Paths to Room */
                        wheelTransportations = feedWheel();                         /* M Osama: Build String Array from cachedPath Info */
                        identifyingWheelSettings();                                 /* M Osama: Identifying wheel settings*/
                        wheelOnClickListener(shortestPathsInCost);                  /* M Osama: onClickListener for wheel representing choices */
                        costAndDistanceTracker();                                   /* M Osama: textViews tracking currentPath cost & distance */
                        sortingClickListener();                                     /* M Osama: onClickListener for two sorting Buttons */
                    }
                    else { noPathsFound();  finish();  noAvailiablePathsToast(); }
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
                        searchingForResulsToast();
                        initCostAndDistance(shortestPathsInCost);                   /* M Osama: put cost & distance of the best root */
                        HashMap pathMap = Shortest.pathMap(shortestPathsInCost);    /* M Osama: give each path a number*/
                        cachingToRoom(pathMap,shortestPathsInCost);                 /* M Osama: caching Paths to Room */
                        wheelTransportations = feedWheel();                         /* M Osama: Build String Array from cachedPath Info To be fed to Wheel */
                        identifyingWheelSettings();                                 /* M Osama: Identifying wheel settings*/
                        wheelOnClickListener(shortestPathsInCost);                  /* M Osama: onClickListener for wheel representing choices */
                        costAndDistanceTracker();                                   /* M Osama: textViews tracking currentPath cost & distance */
                        sortingClickListener();                                     /* M Osama: onClickListener for two sorting Buttons */
                    } else { noPathsFound();  finish();  noAvailiablePathsToast(); }
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

    public void getNearby(String location,String destination){
        RetrofitClient.getInstance().getApi().getNearby(location,destination).enqueue(new Callback<List<Nearby>>() {
            @Override
            public void onResponse(Call<List<Nearby>> call, Response<List<Nearby>> response) {
                List<Nearby> nearbies = response.body();
                if(response.body()!=null){
                    if(nearbies.size()>0){
                        List<Nearby> nearbyLocations=new ArrayList<>();
                        List<Nearby> nearbyDestinations=new ArrayList<>();

                        /* M Osama: splitting locations from destinations */
                        for(Nearby n : nearbies){
                            if(n.getInputField().equals("Location"))  nearbyLocations.add(n);
                            else nearbyDestinations.add(n);
                        }

                        /* M Osama: sorting nearbyLocations & nearbyDestinations */
                        Toast.makeText(getApplicationContext(), nearbyLocations.get(0).getName()+","+nearbyDestinations.get(0).getName(), Toast.LENGTH_SHORT).show();
                        nearbyLocations = Functions.sortByDistance(nearbyLocations);
                        nearbyDestinations = Functions.sortByDistance(nearbyDestinations);

                        /* M Osama: closestNearbyLocation , closestNearbyDestination */
                        Nearby closestLocation = nearbyLocations.get(0);
                        Nearby closestDestination = nearbyDestinations.get(0);
                        Toast.makeText(getApplicationContext(), closestLocation.getName()+","+closestDestination.getName(), Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Size=0", Toast.LENGTH_SHORT).show();       /* M Osama; for debugging to be deleted */
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();             /* M Osama; for debugging to be deleted */
                }
            }

            @Override
            public void onFailure(Call<List<Nearby>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
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
            txtIntent = new Intent(PathResults.this, SelectedPath.class); /* haidy: to link between the 2 pages*/
            pickedPathNodes = wheelTransportations[binding.wheel.getValue()];     /*haidy: getting the text of the selected index in the NumberPicker*/

            pickedPathDetails = Shortest.detailedPathToPrint(Shortest.stopsAndMeans(shortestPaths,binding.wheel.getValue()));
            txtIntent.putExtra(PATH,pickedPathDetails);

            Toast.makeText(PathResults.this, pickedPathNodes, Toast.LENGTH_SHORT).show();
            startActivity(txtIntent);
        });
    }

    public void noAvailiablePathsToast(){
        Toast.makeText(PathResults.this, "نأسف لعدم وجود طريق متوفرة", Toast.LENGTH_SHORT).show();
    }
    public void checkInternetConnectionToast(){
        Toast.makeText(PathResults.this, R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show();
    }
    public void searchingForResulsToast(){
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

}
