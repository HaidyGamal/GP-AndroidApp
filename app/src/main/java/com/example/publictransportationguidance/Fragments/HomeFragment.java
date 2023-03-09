package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.BuildConfig.MAPS_API_KEY;
import static com.example.publictransportationguidance.HelperClasses.Constants.FOOTER;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.HelperClasses.Constants;
import com.example.publictransportationguidance.POJO.StopsResponse.AllStops;
import com.example.publictransportationguidance.POJO.StopsResponse.StopModel;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.Adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.Room.DAO;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Tracking.PathResults;
import com.example.publictransportationguidance.UI.MainActivity;
import com.example.publictransportationguidance.databinding.FragmentHomeBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    public HomeFragment() {}
    FragmentHomeBinding binding;
    DAO dao;

    public static int LOCATION=0;
    public static int DESTINATION=1;

    double [] lats=new double[2];

    String locationLats;
    String destinationLats;

    /* M Osama: Google Maps API -> stops -> AutoCompleteTextView */
    String[] stopsArray ={};
    String[] stopsIDsArray={};
    public static List<String> stopsList = new ArrayList<>();
    public static List<String> stopsIDsList = new ArrayList<>();

    /* M Osama: instance of CustomAutoComplete Adapter */
    CustomAutoCompleteAdapter list;

    /* M Osama: Place AutoComplete instance */
    PlacesClient placesClient;

    /* M Osama: OnCreateView used to connect the fragment java class with it's xml layout */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        View rootView = binding.getRoot();
        return rootView;
    }

    /* M Osama: OnViewCreated used to write the code we want to execute in a fragment */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        /* M Osama: Instance to deal with Google MAPS Api */
        if (!Places.isInitialized())  Places.initialize(getContext(), MAPS_API_KEY);
        placesClient = Places.createClient(getContext());

        /* M Osama: update both autoComplete using Google Maps Places API */
        binding.tvLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) { updateDropDownListUsingGoogleMapsAPI(binding.tvLocation); }
        });
        binding.tvDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) { updateDropDownListUsingGoogleMapsAPI(binding.tvDestination); }
        });

        /* M Osama: autoComplete onClickListeners */
        autoCompleteOnItemClick(binding.tvLocation,0);
        autoCompleteOnItemClick(binding.tvDestination,1);

        /* M Osama: /* Will be edited based on which method we will search with PlaceName or Lat & Long*/
//        dao = RoomDB.getInstance(getContext()).Dao();
//
//        getStops();
//
//        stopsList =dao.getAllStops();
//        this.stopsArray = Shortest.listToArray(stopsList);                                 // M Osama: ArrayList to Array
//
//        /* M Osama: Give inital values to the text views */
//        list = new CustomAutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, this.stopsArray, FOOTER);
//
//        binding.tvLocation.setAdapter(list);
//        binding.tvLocation.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
//            String itemSelected = binding.tvLocation.getText() + "";
//            if (itemSelected.equals(FOOTER)) ;
//            else ;
//
//        });
//
//        binding.tvDestination.setAdapter(list);
//        binding.tvDestination.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
//            String itemSelected = binding.tvDestination.getText() + "";
//            if (itemSelected.equals(FOOTER)) ;
//            else ;
//
//        });

//        binding.btnOk.setOnClickListener((View v) -> {
//            String location = binding.tvLocation.getText()+"";
//            String destination = binding.tvDestination.getText()+"";
//
//            if(location!="" && destination!=""){
//                if(stopsList.contains(location) && stopsList.contains(destination)) {   /* Location & Destination are stored in our DB */
//                    Intent intent = new Intent(getActivity(), PathResults.class);
//                    intent.putExtra("LOCATION", location);
//                    intent.putExtra("DESTINATION", destination);
//                    startActivity(intent);
//                }
//                else{ Toast.makeText(getActivity(), R.string.PleaseChooseTwoAvailableStops, Toast.LENGTH_SHORT).show(); }
//            }
//            else { Toast.makeText(getActivity(), R.string.LocationAndDestinationMustBeSpecified, Toast.LENGTH_SHORT).show(); }
//        });

        binding.searchBtn.setOnClickListener(v -> {
            if(binding.tvLocation.getText()+""!="" && binding.tvDestination.getText()+""!=""){
//                Toast.makeText(getContext(), "We will Search", Toast.LENGTH_SHORT).show();
                searchForPaths(locationLats,destinationLats);
            }
            else Toast.makeText(getContext(), "لا يمكن ترك أحد نقطتي الانطلاق أو الانتهاء فارغة", Toast.LENGTH_SHORT).show();

            Toast.makeText(getContext(), lats[0]+","+lats[1], Toast.LENGTH_SHORT).show();
        });

        binding.distanceRBHomeFragment.setOnClickListener((View v)-> { Toast.makeText(getActivity(), R.string.PathsSortedAccordingToDistance, Toast.LENGTH_SHORT).show(); });
        binding.costRBHomeFragment.setOnClickListener((View v) -> { Toast.makeText(getActivity(), R.string.PathsSortedAccordingToCost, Toast.LENGTH_SHORT).show(); });

    }

/* M Osama: /* Will be edited based on which method we will search with PlaceName or Lat & Long*/
//    public void getStops(){
//        RetrofitClient.getInstance().getApi().getAllStops().enqueue(new Callback<AllStops>() {
//            @Override
//            public void onResponse(Call<AllStops> call, Response<AllStops> response) {
//                AllStops allStops=response.body();
//                List<StopModel> stops= allStops.getAllNodes();
//                // M Osama: delete old data if new greater data is received
//                if(stops.size()>dao.getNumberOfRowsInStopTable()) {
//                    dao.deleteAllStops();
//                    // M Osama: caching Stops in Room (only if StopsTable is empty)
//                    if (dao.getNumberOfRowsInStopTable() == 0) { for (StopModel st : stops)  dao.insertStop(st); }
//                }
//                Toast.makeText(getContext(), R.string.AvailableStopsAreUpdated, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<AllStops> call, Throwable t) {
//                if(getActivity()!=null) {
//                    Toast.makeText(getActivity(), R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    public static String[] listToArray(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++)   array[i] = list.get(i);
        return array;
    }

    /* M Osama: functions helps in dealing with autoComplete dataSource(array,list,...)*/
    public String getSelectedItem(AdapterView parent , int positionInDropList){
        return (String) parent.getItemAtPosition(positionInDropList);
    }
    public int getDataSourceIndex(String[] dataSource , String selectedItem){
        return Arrays.asList(dataSource).indexOf(selectedItem);
    }

    /* M Osama: function to update the autoCompleteTextView on every change the user write */
    public void updateDropDownListUsingGoogleMapsAPI(AutoCompleteTextView acTextView){
        stopsList.clear();
        stopsIDsList.clear();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder() /*  Use the builder to create a FindAutocompletePredictionsRequest. */
                .setLocationBias(RectangularBounds.newInstance(new LatLng(Constants.SOUTH, Constants.WEST), new LatLng(Constants.NORTH, Constants.EAST)))
                .setCountry("EGY")
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .setQuery(acTextView.getText().toString())
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            for (AutocompletePrediction p : response.getAutocompletePredictions()) {
                stopsList.add(p.getPrimaryText(null) + " | " + p.getSecondaryText(null));
                stopsIDsList.add(p.getPlaceId());
            }

            /* M Osama: Initalize AutoCompleteTextView */
            stopsArray = listToArray(stopsList);
            stopsIDsArray= listToArray(stopsIDsList);
            list = new CustomAutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, stopsArray, FOOTER);
            acTextView.setAdapter(list);

            autoCompleteOnFooterClick();

        }).addOnFailureListener((exception) -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
    }

    /* M Osama: States what will happen incase user clicked on "Set Location On Map" */
    public void autoCompleteOnFooterClick(){
        list.setOnFooterClickListener(() -> {
            Toast toast = Toast.makeText(getContext(), R.string.GoingToGoogleMaps, Toast.LENGTH_LONG);

            Uri gmmIntentUri = Uri.parse("geo:30.0444,31.2357");             // Create a Uri from an intent string. Use the result to create an Intent. /*M Osama: entered latitude & longitude for Cairo*/
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri); // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            mapIntent.setPackage("com.google.android.apps.maps");            // Make the Intent explicit by setting the Google Maps package
            startActivity(mapIntent);                                        // Attempt to start an activity that can handle the Intent

            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }

    /* M Osama: States what will happen incase user clicked clicked on specific plase */
    public void autoCompleteOnItemClick(AutoCompleteTextView acTextView,int stop){
        acTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = getSelectedItem(parent,position);
            Toast.makeText(getContext(), selectedItem, Toast.LENGTH_SHORT).show();                                              /* M Osama: Only for checking the autoCompleteOnClick is working */
            getPlaceCoordinatesUsingID(stopsIDsArray[getDataSourceIndex(stopsArray,selectedItem)],stop);
            acTextView.setText(deleteFromSequence(selectedItem," |"));
            Toast.makeText(getContext(),stopsIDsArray[getDataSourceIndex(stopsArray,selectedItem)], Toast.LENGTH_SHORT).show();  /* M Osama: Only for checking the autoCompleteOnClick is working */
        });
    }

    /* M Osama: Returns the location lat & long which can be used to search through db */
    public double[] getPlaceCoordinatesUsingID(String placeID,int stopID){
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID, Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG));
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            lats[0]=response.getPlace().getLatLng().latitude;
            lats[1]=response.getPlace().getLatLng().longitude;

            if(stopID==LOCATION) locationLats=getStopLatLong(lats[0],lats[1]);
            else                 destinationLats=getStopLatLong(lats[0],lats[1]);

            Toast.makeText(getContext(), "ID="+stopID+" "+lats[0]+","+lats[1], Toast.LENGTH_SHORT).show();                /* M Osama: Only for checking that getPlaceCoordinatesUsingID is working */

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) Toast.makeText(getContext(), ((ApiException) exception).getStatusCode(), Toast.LENGTH_SHORT).show();
        });
        return lats;
    }

    /* M Osama: Used to print only the main place name */
    public String deleteFromSequence(String inputString, String sequenceToStart) {
        int index = inputString.indexOf(sequenceToStart);
        if (index == -1) {
            // If the sequence is not found in the string, return the original string
            return inputString;
        } else {
            // Return the string after deleting all characters starting from the entered sequence of characters
            return inputString.substring(0, index);
        }
    }

    public String getStopLatLong(double lat, double lng) {
        return lat + "," + lng;
    }

    public void searchForPaths(String location,String destination){
        Intent intent = new Intent(getContext(), PathResults.class);
        intent.putExtra("LOCATION",location);
        intent.putExtra("DESTINATION",destination);
        startActivity(intent);
    }

}

