package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.BuildConfig.MAPS_API_KEY;
import static com.example.publictransportationguidance.blindMode.speechToText.SpeechToTextHelper.convertHaaToTaaMarbuta;
import static com.example.publictransportationguidance.helpers.GlobalVariables.*;
import static com.example.publictransportationguidance.helpers.Functions.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.tracking.trackingModule.trackingHelpers.OnPlaceResponseListener;
import com.example.publictransportationguidance.blindMode.speechToText.SpeechToTextHelper;
import com.example.publictransportationguidance.blindMode.textToSpeech.TextToSpeechHelper;
import com.example.publictransportationguidance.googleMap.MapActivity;
import com.example.publictransportationguidance.helpers.GlobalVariables;
import com.example.publictransportationguidance.adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.tracking.PathResults;
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
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,OnPlaceResponseListener {
    public HomeFragment() {}
    FragmentHomeBinding binding;
    int pointer=0;
    /* M Osama: to store stop lat/long & to build location & destination Strings to be send to API request to search DB */
    double [] lats=new double[2];
    public static String locationLats="0.000";            /* M Osama: to prevent bugs */
    public static String destinationLats="0.000";

    /* M Osama: Google Maps API -> stops -> AutoCompleteTextView */
    String[] stopsArray ={};
    String[] stopsIDsArray={};
    String[] stopsDetailsArray={};

    /* M Osama: instance of CustomAutoComplete Adapter */
    CustomAutoCompleteAdapter list;

    /* M Osama: Place AutoComplete instance */
    PlacesClient placesClient;

    /* M Osama: track the returned position from Map */
    private ActivityResultLauncher<Intent> mapActivityResultLauncher;

    /* M Osama: instance to deal with stt*/
    private SpeechToTextHelper speechToTextHelper;
    private TextToSpeechHelper textToSpeechHelper;

    private GeoApiContext geoApiContext;
    List<String> stopsDetailsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        speechToTextHelper = SpeechToTextHelper.getInstance(ARABIC);
        SharedPrefs.init(getContext());
        SharedPrefs.registerOnSharedPreferenceChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initializeMapActivityResultLauncher();                                          /* M Osama: track the result returned from Map if user clicked on it */

        /* M Osama: initial list value to prevent NullPointerException */
        list = new CustomAutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, stopsArray, FOOTER);

        /* M Osama: Instance to deal with Google MAPS Api */
        if (!Places.isInitialized())  Places.initialize(getContext(), MAPS_API_KEY);
        placesClient = Places.createClient(getContext());

        /* M Osama: initializing tts & stt */
        initializeTTSandSTT();

        /* M Osama: update both autoComplete using Google Maps Places API */
        onTextChangeListener(binding.tvLocation,this);
        onTextChangeListener(binding.tvDestination,this);

        /* M Osama: autoComplete onClickListeners */
        autoCompleteOnItemClick(binding.tvLocation,0);
        autoCompleteOnItemClick(binding.tvDestination,1);

        /* M Osama: autoComplete FooterClickListener */
        autoCompleteOnFooterClick(getView());
        /* M Osama: decide the sorting Criteria */
        binding.orderByCost.setOnClickListener(v -> {       sortingByCostToast(getContext());       SORTING_CRITERIA=COST; pointer=1;     });
        binding.orderByDistance.setOnClickListener(v -> {   sortingByDistanceToast(getContext());   SORTING_CRITERIA=DISTANCE; pointer=2; });
        binding.orderByTime.setOnClickListener(v -> {       sortingByTimeToast(getContext());       SORTING_CRITERIA=TIME;     pointer=3; });

        binding.searchBtn.setOnClickListener(v -> {

            if(!(binding.tvLocation.getText() + "").equals("") && !(binding.tvDestination.getText() + "").equals(""))  {
                if(pointer!=0)      searchForPaths(locationLats,destinationLats,pointer);
                else                Toast.makeText(getContext(),"اختر طريقة للترتيب حسب رغبتك", Toast.LENGTH_SHORT).show(); }
            else  Toast.makeText(getContext(),R.string.AutoCompleteTextViewWarning, Toast.LENGTH_SHORT).show();
        });


    }

    public void getResponseInArabic(String placeId, OnPlaceResponseListener listener) {
        new AsyncTask<String, Void, String[]>() {
            @Override
            protected String[] doInBackground(String... params) {
                initializeGeoApiContext();
                String[] placeFullName = new String[2];
                try {
                    GeocodingResult[] results = GeocodingApi.newRequest(geoApiContext).place(params[0]).language("ar").await();
                    if (results != null && results.length > 0) {
                        GeocodingResult result = results[0];
                        placeFullName[0] = deleteFromPenultimateComma(result.formattedAddress);
                        placeFullName[1] = deleteFromFistComma(deleteFromPenultimateComma(result.formattedAddress));
                    } else Log.d("TAG", "No geocoding results found");

                } catch (Exception e) {
                    Log.e("TAG", "Geocoding request failed: " + e);
                }
                return placeFullName;
            }

            @Override
            protected void onPostExecute(String[] placeFullName) {
                listener.onPlaceResponse(placeFullName);                        // Pass the result to the listener
            }
        }.execute(placeId);
    }

    /* M Osama: function to update the autoCompleteTextView on every change the user write */
    public void updateDropDownListUsingGoogleMapsAPI(AutoCompleteTextView acTextView,OnPlaceResponseListener listener){
        List<String> stopsIDsList = new ArrayList<>();
        List<String> stopsList = new ArrayList<>();
        stopsDetailsList = new ArrayList<>();
        stopsList.clear();
        stopsIDsList.clear();
        stopsDetailsList.clear();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder() /*  Use the builder to create a FindAutocompletePredictionsRequest. */
                .setLocationBias(RectangularBounds.newInstance(new LatLng(GlobalVariables.SOUTH, GlobalVariables.WEST), new LatLng(GlobalVariables.NORTH, GlobalVariables.EAST)))
                .setCountry("EGY")
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .setQuery(acTextView.getText().toString())
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            for (AutocompletePrediction p : response.getAutocompletePredictions()) {
                stopsIDsList.add(p.getPlaceId());
                if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1){
                    getResponseInArabic(p.getPlaceId(), placeInArabic -> {
                        if (placeInArabic != null) {
                            stopsList.add(placeInArabic[0]);
                            stopsDetailsList.add(placeInArabic[1]);
                            listener.onPlaceResponse(placeInArabic);

                            if (stopsDetailsList.size() == response.getAutocompletePredictions().size()) {
                                executeAfterStopsDetailsListPopulated(acTextView);
                            }
                        }
                    });
                }
                else stopsList.add(stringEnhancer(p.getPrimaryText(null) + " | " + p.getSecondaryText(null)));
            }

            /* M Osama: Initialize AutoCompleteTextView */
            stopsArray = listToArray(stopsList);
            stopsIDsArray= listToArray(stopsIDsList);
            stopsDetailsArray=listToArray(stopsDetailsList);
            list = new CustomAutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, stopsArray, FOOTER);
            acTextView.setAdapter(list);

            autoCompleteOnFooterClick(acTextView);

            if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1) {
                if(acTextView.getId()==binding.tvLocation.getId()) execute(()->textToSpeechHelper.speak(availableStopsToBeRead(true,stopsDetailsList),()-> listenToSpecifiedLocationName(this) ));
                else if(acTextView.getId()==binding.tvDestination.getId()) execute(()->textToSpeechHelper.speak(availableStopsToBeRead(true,stopsDetailsList),()-> listenToSpecifiedDestinationName(this)));
            }

        }).addOnFailureListener((exception) -> Toast.makeText(getContext(), R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show());

    }

    /* M Osama: States what will happen in case user clicked clicked on specific please */
    public void autoCompleteOnItemClick(AutoCompleteTextView acTextView,int stop){
        acTextView.setOnItemClickListener((parent, view, position, id) -> {             /* M Osama: Only For debugging */
            String selectedItem = getSelectedItem(parent,position);
            Log.i("TAG","From(HomeFragment)"+selectedItem);             /* M Osama: Only for checking the autoCompleteOnClick is working */
            getPlaceCoordinatesUsingID(stopsIDsArray[getDataSourceIndex(stopsArray,selectedItem)],stop);
            if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1)   acTextView.setText(getSubstringBeforeFirstComma(selectedItem));
            else                                                        acTextView.setText(deleteFromSequence(selectedItem," |"));
        });
    }


    /* M Osama: Returns the location lat & long which can be used to search through db */
    public void getPlaceCoordinatesUsingID(String placeID,int stopID){
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            lats[0] = response.getPlace().getLatLng().latitude;
            lats[1] = response.getPlace().getLatLng().longitude;

            if (stopID == LOCATION) locationLats = getStopLatLong(lats[0], lats[1]);
            else if (stopID == DESTINATION) destinationLats = getStopLatLong(lats[0], lats[1]);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) Log.i("TAG","From(AddNewRoute) "+((ApiException) exception).getStatusCode());
        });
    }

    /* M Osama: function that passes chosen location & destination to PathResults which will search the API for available paths */
    public void searchForPaths(String location,String destination,int pointer){
        Intent intent = new Intent(getContext(), PathResults.class);
        intent.putExtra("LOCATION",location);
        intent.putExtra("DESTINATION",destination);
        intent.putExtra("POINTER",pointer);
        startActivity(intent);
    }

    /* M Osama: ask the user to allow using map */
    public boolean askUserToEnableLocation(Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {                            /* Check if location services are enabled */
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);                   // If location services are not enabled, prompt the user to enable them
            context.startActivity(intent);
        }
        return true;                                                                                // If location services are already enabled, return true
    }

    /* M Osama: States what will happen in case user clicked on "Set Location On MapActivity" */
    public void autoCompleteOnFooterClick(View view) {
        list.setOnFooterClickListener(() -> {
            LAST_CLICKED_FOOTER_VIEW = view.getId();
            Toast.makeText(getContext(), R.string.GoingToMap, Toast.LENGTH_SHORT).show();
            if (askUserToEnableLocation(getContext())) {
                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                mapActivityResultLauncher.launch(mapIntent);
            }
        });
    }

    /* M Osama: track the returned result from Map */
    private void initializeMapActivityResultLauncher() {
        mapActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    double latitude = data.getDoubleExtra(LATITUDE_KEY, 0.0);
                    double longitude = data.getDoubleExtra(LONGITUDE_KEY, 0.0);
                    String locationName = data.getStringExtra(LOCATION_NAME_KEY);
                    if (LAST_CLICKED_FOOTER_VIEW == R.id.tv_location) {             binding.tvLocation.setText(locationName);       locationLats = getStopLatLong(latitude, longitude);     }
                    else if (LAST_CLICKED_FOOTER_VIEW == R.id.tv_destination) {     binding.tvDestination.setText(locationName);    destinationLats = getStopLatLong(latitude, longitude);  }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        speechToTextHelper.onActivityResult(requestCode, resultCode, data);                                                  // Pass the onActivityResult event to the SpeechToTextHelper
        ArrayList<String> speechConvertedToText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        switch (requestCode){
            case LISTEN_TO_RAW_LOCATION_NAME:                                   /* M Osama: receiving the locationName from the blind */
                binding.tvLocation.setText(convertHaaToTaaMarbuta(speechConvertedToText.get(0)));
                onTextChangeListener(binding.tvLocation,this);
                break;

            case LISTEN_TO_SPECIFIED_LOCATION_NAME:                             /* M Osama: receiving the specifiedDetails of the locationName from the blind */
                for(int placeNum=0;placeNum<stopsDetailsList.size();placeNum++){
                    String place = convertToAleph(removeCommas(convertHaaToTaaMarbuta(stopsDetailsList.get(placeNum))));
                    if(place.equals(convertHaaToTaaMarbuta(speechConvertedToText.get(0)))) {
                        autoCompleteOnVoiceClick(binding.tvLocation,0,placeNum);
                        execute(() -> textToSpeechHelper.speak(getString(R.string.WhatsYourDestination),() -> listenToRawDestinationName(this)));
                    }
                    else textToSpeechHelper.speak(availableStopsToBeRead(false,stopsDetailsList),()-> listenToSpecifiedLocationName(this));
                }
                break;

            case LISTEN_TO_RAW_DESTINATION_NAME:                                /* M Osama: receiving the destinationName from the blind*/
                binding.tvDestination.setText(convertHaaToTaaMarbuta(speechConvertedToText.get(0)));
                onTextChangeListener(binding.tvDestination,this);
                break;

            case LISTEN_TO_SPECIFIED_DESTINATION_NAME:                          /* M Osama: receiving the specifiedDetails of the destinationName from the blind */
                for(int placeNum=0;placeNum<stopsDetailsList.size();placeNum++){
                    String place = convertToAleph(removeCommas(convertHaaToTaaMarbuta(stopsDetailsList.get(placeNum))));
                    if(place.equals(convertHaaToTaaMarbuta(speechConvertedToText.get(0)))) {
                        autoCompleteOnVoiceClick(binding.tvDestination,1,placeNum);
                        execute(() -> textToSpeechHelper.speak(getString(R.string.SortingCriteria), () -> listenToSortingCriteria(this)));
                    }
                    else textToSpeechHelper.speak(availableStopsToBeRead(false,stopsDetailsList),()-> listenToSpecifiedDestinationName(this));
                }
                break;

            case LISTEN_TO_SORTING_CRITERIA:                                    /* M Osama: receiving the sortingCriteria from the blind*/
                String searchingMethod=convertHaaToTaaMarbuta(speechConvertedToText.get(0));
                if(stringIsFound(searchingMethod,SORTING_CRITERIA_ACCEPTANCE)) {
                    if (searchingMethod.equals(SORTING_CRITERIA_ACCEPTANCE[4]) || searchingMethod.equals(SORTING_CRITERIA_ACCEPTANCE[5])) {       sortingByCostToast(getContext());       SORTING_CRITERIA = COST;    }
                    else if (searchingMethod.equals(SORTING_CRITERIA_ACCEPTANCE[2]) || searchingMethod.equals(SORTING_CRITERIA_ACCEPTANCE[3])) {  sortingByDistanceToast(getContext());   SORTING_CRITERIA = DISTANCE;  }
                    else if (searchingMethod.equals(SORTING_CRITERIA_ACCEPTANCE[0]) || searchingMethod.equals(SORTING_CRITERIA_ACCEPTANCE[1])) {  sortingByTimeToast(getContext());       SORTING_CRITERIA = TIME;      }
                    searchForPaths(locationLats, destinationLats,pointer);
                }
                else  textToSpeechHelper.speak(getString(R.string.SortingCriteriaSecondListen), () -> listenToSortingCriteria(this));
        }

    }


    /* M Osama: States what will happen in case user clicked clicked on specific please */
    public void autoCompleteOnVoiceClick(AutoCompleteTextView acTextView,int stop,int tempPlaceNumber){
        String selectedItem = stopsDetailsList.get(tempPlaceNumber);
        Toast.makeText(getContext(), selectedItem, Toast.LENGTH_SHORT).show();                      /* M Osama: Only for checking the autoCompleteOnClick is working */
        getPlaceCoordinatesUsingID(stopsIDsArray[tempPlaceNumber], stop);
//        acTextView.setText(getSubstringBeforeFirstComma(selectedItem));
    }

    void initializeTTSandSTT(){
        textToSpeechHelper = TextToSpeechHelper.getInstance(getContext(),ARABIC);
        speechToTextHelper = SpeechToTextHelper.getInstance(ARABIC);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("ON_BLIND_MODE")) {
            if (SharedPrefs.readMap(key,0) == 1) execute(() -> textToSpeechHelper.speak(getString(R.string.WhatsYourLocation), () -> listenToRawLocationName(this)));
        }
    }

    private void listenToRawLocationName(HomeFragment homeFragment) {
        speechToTextHelper.startSpeechRecognition(homeFragment,LISTEN_TO_RAW_LOCATION_NAME);
    }
    private void listenToRawDestinationName(HomeFragment homeFragment){
        speechToTextHelper.startSpeechRecognition(homeFragment,LISTEN_TO_RAW_DESTINATION_NAME);
    }

    private void listenToSpecifiedLocationName(HomeFragment homeFragment){
        speechToTextHelper.startSpeechRecognition(homeFragment,LISTEN_TO_SPECIFIED_LOCATION_NAME);
    }
    private void listenToSpecifiedDestinationName(HomeFragment homeFragment){
        speechToTextHelper.startSpeechRecognition(homeFragment,LISTEN_TO_SPECIFIED_DESTINATION_NAME);
    }

    private void listenToSortingCriteria(HomeFragment homeFragment){
        speechToTextHelper.startSpeechRecognition(homeFragment,LISTEN_TO_SORTING_CRITERIA);
    }

    private void onTextChangeListener(AutoCompleteTextView autoCompleteTextView,OnPlaceResponseListener listener){
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {  updateDropDownListUsingGoogleMapsAPI(autoCompleteTextView,listener); }
        });
    }

    private void initializeGeoApiContext() {
        if (geoApiContext == null) geoApiContext = new GeoApiContext.Builder().apiKey(MAPS_API_KEY).build();
    }


    @Override
    public void onPlaceResponse(String[] placeFullName) {}

    private void executeAfterStopsDetailsListPopulated(AutoCompleteTextView acTextView) {
        if (SharedPrefs.readMap("ON_BLIND_MODE", 0) == 1) {
            if (acTextView.getId() == binding.tvLocation.getId())
                textToSpeechHelper.speak(availableStopsToBeRead(true, stopsDetailsList), () -> listenToSpecifiedLocationName(this));
            else if (acTextView.getId() == binding.tvDestination.getId())
                textToSpeechHelper.speak(availableStopsToBeRead(true, stopsDetailsList), () -> listenToSpecifiedDestinationName(this));
        }
    }
}
