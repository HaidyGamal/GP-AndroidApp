package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.BuildConfig.MAPS_API_KEY;
import static com.example.publictransportationguidance.HelperClasses.Constants.FOOTER;
import static com.example.publictransportationguidance.HelperClasses.Constants.LOCATION;
import static com.example.publictransportationguidance.HelperClasses.Functions.deleteFromSequence;
import static com.example.publictransportationguidance.HelperClasses.Functions.getDataSourceIndex;
import static com.example.publictransportationguidance.HelperClasses.Functions.getSelectedItem;
import static com.example.publictransportationguidance.HelperClasses.Functions.getStopLatLong;
import static com.example.publictransportationguidance.HelperClasses.Functions.listToArray;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.GoogleMap.MapActivity;
import com.example.publictransportationguidance.HelperClasses.Constants;
import com.example.publictransportationguidance.Adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Tracking.PathResults;
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

public class HomeFragment extends Fragment{
    public HomeFragment() {}
    FragmentHomeBinding binding;

    /* M Osama: to store stop lat/long & to build location & destination Strings to be send to API request to search DB */
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        View rootView = binding.getRoot();
        return rootView;
    }

    /* M Osama: OnViewCreated used to write the code we want to execute in a fragment */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        /* M Osama: inital list value to prevent NullPointerException */
        list = new CustomAutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, stopsArray, FOOTER);

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

        /* M Osama: autoComplete FooterClickListener */
        autoCompleteOnFooterClick();

        binding.searchBtn.setOnClickListener(v -> {
            if(binding.tvLocation.getText()+""!="" && binding.tvDestination.getText()+""!="") searchForPaths(locationLats,destinationLats);
            else                    Toast.makeText(getContext(), "لا يمكن ترك أحد نقطتي الانطلاق أو الانتهاء فارغة", Toast.LENGTH_SHORT).show();

            Toast.makeText(getContext(), lats[0]+","+lats[1], Toast.LENGTH_SHORT).show();       //M Osama: for checking it will be deleted
        });

        binding.distanceRBHomeFragment.setOnClickListener((View v)-> Toast.makeText(getActivity(), R.string.PathsSortedAccordingToDistance, Toast.LENGTH_SHORT).show());
        binding.costRBHomeFragment.setOnClickListener((View v) -> Toast.makeText(getActivity(), R.string.PathsSortedAccordingToCost, Toast.LENGTH_SHORT).show());

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

    /* M Osama: States what will happen incase user clicked on "Set Location On MapActivity" */
    public void autoCompleteOnFooterClick(){
        list.setOnFooterClickListener(() -> {
//            Toast.makeText(getContext(), getView().getId()+"", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "جاري الذهاب إلي الخريطة", Toast.LENGTH_SHORT).show();
            if(askUserToEnableLocation(getContext())==true)     startActivity(new Intent(getContext(), MapActivity.class));
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
    public void getPlaceCoordinatesUsingID(String placeID,int stopID){
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
    }

    /* M Osama: function that passes chosen location & destination to PathResults which will search the API for available paths */
    public void searchForPaths(String location,String destination){
        Intent intent = new Intent(getContext(), PathResults.class);
        intent.putExtra("LOCATION",location);
        intent.putExtra("DESTINATION",destination);
        startActivity(intent);
    }

    /* M Osama: ask the user to allow using map */
    public boolean askUserToEnableLocation(Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Check if location services are enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // If location services are not enabled, prompt the user to enable them
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
            return true;
        } else {
            // If location services are already enabled, return true
            return true;
        }
    }



}

