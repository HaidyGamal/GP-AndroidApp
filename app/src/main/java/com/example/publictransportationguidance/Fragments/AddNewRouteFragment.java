package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.BuildConfig.MAPS_API_KEY;
import static com.example.publictransportationguidance.api.Api.NEO4J_BASE_URL;
import static com.example.publictransportationguidance.helpers.Functions.deleteFromFistComma;
import static com.example.publictransportationguidance.helpers.Functions.deleteFromPenultimateComma;
import static com.example.publictransportationguidance.helpers.Functions.deleteFromSequence;
import static com.example.publictransportationguidance.helpers.Functions.getDataSourceIndex;
import static com.example.publictransportationguidance.helpers.Functions.getSelectedItem;
import static com.example.publictransportationguidance.helpers.Functions.getStopLatLong;
import static com.example.publictransportationguidance.helpers.Functions.getSubstringBeforeFirstComma;
import static com.example.publictransportationguidance.helpers.Functions.listToArray;
import static com.example.publictransportationguidance.helpers.Functions.splitLatLng;
import static com.example.publictransportationguidance.helpers.Functions.stringEnhancer;
import static com.example.publictransportationguidance.helpers.GlobalVariables.DESTINATION;
import static com.example.publictransportationguidance.helpers.GlobalVariables.FIRESTORE_ADD_NEW_ROUTE_COLLECTION_NAME;
import static com.example.publictransportationguidance.helpers.GlobalVariables.FOOTER;
import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LAST_CLICKED_FOOTER_VIEW;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LATITUDE_KEY;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LOCATION;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LOCATION_NAME_KEY;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LONGITUDE_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.Authentication.LoginDialog;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.api.RetrofitClient;
import com.example.publictransportationguidance.googleMap.MapActivity;
import com.example.publictransportationguidance.helpers.GlobalVariables;
import com.example.publictransportationguidance.pojo.addNewRouteResponse.AddNewRoute;
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.ui.VerifyDialog;
import com.example.publictransportationguidance.databinding.FragmentAddNewRouteBinding;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddNewRouteFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public AddNewRouteFragment() {}
    FragmentAddNewRouteBinding bind;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    // Declare a boolean flag
    private boolean isAddToNeo4jExecuted = false;

    /* M Osama: instance of CustomAutoComplete Adapter */
    CustomAutoCompleteAdapter list;

    /* M Osama: Place AutoComplete instance */
    PlacesClient placesClient;

    /* M Osama: track the returned position from Map */
    private ActivityResultLauncher<Intent> mapActivityResultLauncher;

    /* M Osama: to store stop lat/long & to build location & destination Strings to be send to API request to search DB */
    double [] lats=new double[2];
    public static String locationLats="0.000";            /* M Osama: to prevent bugs */
    public static String destinationLats="0.000";

    /* M Osama: Google Maps API -> stops -> AutoCompleteTextView */
    String[] stopsArray ={};
    String[] stopsIDsArray={};
    String[] stopsDetailsArray={};

    private GeoApiContext geoApiContext;
    List<String> stopsDetailsList;

    FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = DataBindingUtil.inflate(inflater,R.layout.fragment_add_new_route,container,false);
        initializeMapActivityResultLauncher();
         SharedPrefs.init(getActivity());
        firebaseInitializer();
        db = FirebaseFirestore.getInstance();

        /* M Osama: initial list value to prevent NullPointerException */
        list = new CustomAutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, stopsArray, FOOTER);

        /* M Osama: Instance to deal with Google MAPS Api */
        if (!Places.isInitialized())  Places.initialize(getContext(), MAPS_API_KEY);
        placesClient = Places.createClient(getContext());

        /* M Osama: update both autoComplete using Google Maps Places API */
        onTextChangeListener(bind.tvLocation);
        onTextChangeListener(bind.tvDestination);

        /* M Osama: autoComplete onClickListeners */
        autoCompleteOnItemClick(bind.tvLocation,0);
        autoCompleteOnItemClick(bind.tvDestination,1);

        /* M Osama: autoComplete FooterClickListener */
        autoCompleteOnFooterClick(getView());

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.transportations, android.R.layout.simple_spinner_item);    // haidy: Creating an ArrayAdapter using the string array and a default spinner layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // haidy:  Specify the layout to use when the list of choices appears



        bind.spin.setAdapter(adapter);    // haidy: Applying the adapter to the spinner
        bind.spin.setOnItemSelectedListener(this);

        bind.submit.setOnClickListener((View v) -> {
            if (!isAddToNeo4jExecuted) {
                buildFirestoreSingleDataToSend(locationLats, destinationLats);
                isAddToNeo4jExecuted = true;
            }
        });

        bind.logOut.setOnClickListener(v -> performLogOut(mAuth));

        /* M Osama: ask the user to log in if he isn't loggedIn to be able to add new route*/
        if(IS_LOGGED_IN ==0) showLogInDialog();

        return bind.getRoot();
    }

    //haidy: enabling the transportation text input
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        bind.transportType.setEnabled(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        bind.transportType.setEnabled(false);
    }

    /** M Osama: firestore related functions */
    private void firebaseInitializer(){
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    private void performLogOut(FirebaseAuth auth) {
        auth.signOut();
        IS_LOGGED_IN = 0;
        SharedPrefs.write("IS_LOGGED_IN", IS_LOGGED_IN);
        Toast.makeText(getActivity(), R.string.LogoutSuccessful, Toast.LENGTH_SHORT).show();
        showLogInDialog();
    }
    public String getTransportationMode(String input) {
        String mode;

        switch (input) {
            case "أوتوبيس":
                mode = "bus";
                break;
            case "ميكروباص":
                mode = "microbus";
                break;
            case "مترو":
                mode = "metro";
                break;
            default:
                mode = "unknown";
                break;
        }

        return mode;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void hasRedundantData(String location, String destination, String cost, String pathName, String transportationType,String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection(FIRESTORE_ADD_NEW_ROUTE_COLLECTION_NAME);

        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    Map<String, Integer> dataCountMap = new HashMap<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Object nameObj = document.get("Node Name");
                        Object typeObj = document.get("Type");
                        Object fLatitudeObj = document.get("FromLatitude");
                        Object fLongitudeObj = document.get("FromLongitude");
                        Object tLatitudeObj = document.get("ToLatitude");
                        Object tLongitudeObj = document.get("ToLongitude");

                        if (nameObj instanceof String && typeObj instanceof String && fLatitudeObj instanceof String && fLongitudeObj instanceof String && tLatitudeObj instanceof String && tLongitudeObj instanceof String) {
                            String name = (String) nameObj;
                            String type = (String) typeObj;
                            String fLatitude = (String) fLatitudeObj;
                            String fLongitude = (String) fLongitudeObj;
                            String tLatitude = (String) tLatitudeObj;
                            String tLongitude = (String) tLongitudeObj;

                            String dataKey = name + "_" + fLatitude + "_" + fLongitude + "_" + tLatitude + "_" + tLongitude + "_" + type;
                            dataCountMap.put(dataKey, dataCountMap.getOrDefault(dataKey, 0) + 1);
                        }
                    }

                    // Check for redundancy and execute logs only once
                    boolean logsExecuted = false;

                    for (Map.Entry<String, Integer> entry : dataCountMap.entrySet()) {
                        int count = entry.getValue();
                        Log.i("Info", "Name: " + pathName);
                        Log.i("Info", "Type: " + transportationType);
                        Log.i("Info", "Count: " + count);

                        /* M Osama: If redundancy is >5 : No suggestions is added to firestore 5 */
                        if(count>5){deleteData(documentId);}

                        /* M Osama: if redundancy =5    : A connection is added(in case nodes are avaliable) */
                        else if (count == 5) {
                            if (!logsExecuted) {
                                addToNeo4j(location, destination, cost, pathName, getTransportationMode(transportationType));
                                logsExecuted = true;
//                                Toast.makeText(getContext(), count+"سيتم اضافة الطريق", Toast.LENGTH_SHORT).show();       /* M Osama: for debugging only*/
                            }
                        }

                        /* M Osama: if redundancy <5    : A suggestion is added to firestore */
                        else {
//                            Toast.makeText(getContext(), count+"سيتم مراجعة طلبكم", Toast.LENGTH_SHORT).show();           /* M Osama: for debugging only */
                        }

                    }
                }
            } else {
                Exception exception = task.getException();
                if (exception != null) exception.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void buildFirestoreSingleDataToSend(String location, String destination) {
        String dist;
        String name= bind.transportType.getText().toString();
        String cost = getCost(bind.costET.getText().toString().trim());

        dist="0.7";

        if(bind.tvLocation.getText().toString().isEmpty()|| bind.tvLocation.getText().toString().equals("")|| bind.tvLocation.getText().toString().equals(getString(R.string.Location)))                      bind.tvLocation.setError("مطلوب!");
        else if(bind.tvDestination.getText().toString().isEmpty()|| bind.tvDestination.getText().toString().equals("")|| bind.tvDestination.getText().toString().equals(getString(R.string.Destination)))            bind.tvDestination.setError("مطلوب!");
        else if(name.isEmpty()||name.equals("")||name.equals(getString(R.string.ConnectionName)))                                                                                                                     bind.transportType.setError("مطلوب!");
        else{
            Map<String,Object> node = buildFirestoreNode(mUser.getEmail(),name,splitLatLng(location)[0],splitLatLng(location)[1],splitLatLng(destination)[0],splitLatLng(destination)[1], getTransportationMode(bind.spin.getSelectedItem().toString()),cost,dist);
            sendData(node,cost,name);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendData(Map<String,Object> node,String cost,String name){
        db.collection(FIRESTORE_ADD_NEW_ROUTE_COLLECTION_NAME)
                .add(node)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    Log.d("TAG", "DocumentSnapshot added with ID: " + documentId);
                    hasRedundantData(locationLats, destinationLats, cost, name, bind.spin.getSelectedItem().toString(),documentId);
                    new VerifyDialog().show(getChildFragmentManager(), LoginDialog.TAG);
                    isAddToNeo4jExecuted = false;
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding document", e));
    }

    private void deleteData(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FIRESTORE_ADD_NEW_ROUTE_COLLECTION_NAME)
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "DocumentSnapshot successfully deleted.");
                    // Perform any additional actions upon successful deletion
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document", e));
    }


    private String getCost(String cost) {
        if(cost.equals("اختياري") || cost.equals("") || cost.equals("اختيارى"))    return "7";
        else                                             return cost;
    }

    Map<String,Object> buildFirestoreNode(String email,String nodeName,String locationLat,String locationLong,String destinationLat,String destinationLong,String transportationType,String cost,String distance){
        Map<String,Object> node=new HashMap<>();
        node.put("id",email);
        node.put("Node Name",nodeName);
        node.put("FromLatitude",locationLat);
        node.put("FromLongitude",locationLong);
        node.put("ToLatitude",destinationLat);
        node.put("ToLongitude",destinationLong);
        node.put("Type",transportationType);
        node.put("Cost",cost);
        node.put("Distance",distance);
        return node;
    }


    /** M Osama: common functions with HomeFragment (to be combined later)*/
    public String[] getResponseInArabic(String placeId) {
        initializeGeoApiContext();
        String[] placeFullName= new String[2];
        try {
            GeocodingResult[] results = GeocodingApi.newRequest(geoApiContext).place(placeId).language("ar").await();
            if (results != null && results.length > 0) {
                GeocodingResult result = results[0];
                placeFullName[0]=deleteFromPenultimateComma(result.formattedAddress);
                placeFullName[1]=deleteFromFistComma(deleteFromPenultimateComma(result.formattedAddress));
            } else {}
        } catch (Exception e) {}
        return placeFullName;
    }

    private void initializeGeoApiContext() {
        if (geoApiContext == null) geoApiContext = new GeoApiContext.Builder().apiKey(MAPS_API_KEY).build();
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
                    if (LAST_CLICKED_FOOTER_VIEW == R.id.tv_location) {             bind.tvLocation.setText(locationName);       locationLats = getStopLatLong(latitude, longitude);     }
                    else if (LAST_CLICKED_FOOTER_VIEW == R.id.tv_destination) {     bind.tvDestination.setText(locationName);    destinationLats = getStopLatLong(latitude, longitude);  }
                }
            }
        });
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

    /* M Osama: States what will happen in case user clicked clicked on specific please */
    public void autoCompleteOnItemClick(AutoCompleteTextView acTextView, int stop){
        acTextView.setOnItemClickListener((parent, view, position, id) -> {             /* M Osama: Only For debugging */
            String selectedItem = getSelectedItem(parent,position);
            Log.i("TAG","From (AddNewRoute) "+selectedItem);                   /* M Osama: Only for checking the autoCompleteOnClick is working */
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
            if (exception instanceof ApiException) Toast.makeText(getContext(), ((ApiException) exception).getStatusCode(), Toast.LENGTH_SHORT).show();
        });
    }


    /** M Osama: edited functions from HomeFragment */
    /* M Osama: function to update the autoCompleteTextView on every change the user write */
    public void updateDropDownListUsingGoogleMapsAPI(AutoCompleteTextView acTextView){
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
                    String[] placeInArabic = getResponseInArabic(p.getPlaceId());
                    stopsList.add(placeInArabic[0]);
                    stopsDetailsList.add(placeInArabic[1]);
                }
                else    stopsList.add(stringEnhancer(p.getPrimaryText(null) + " | " + p.getSecondaryText(null)));
            }

            /* M Osama: Initialize AutoCompleteTextView */
            stopsArray = listToArray(stopsList);
            stopsIDsArray= listToArray(stopsIDsList);
            stopsDetailsArray=listToArray(stopsDetailsList);
            list = new CustomAutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, stopsArray, FOOTER);
            acTextView.setAdapter(list);

            autoCompleteOnFooterClick(acTextView);

//            if(SharedPrefs.readMap("ON_BLIND_MODE",0)==1) {
//                if(acTextView.getId()==binding.tvLocation.getId())          textToSpeechHelper.speak(availableStopsToBeRead(stopsDetailsList),()-> listenToSpecifiedLocationName(this));
//                else if(acTextView.getId()==binding.tvDestination.getId())  textToSpeechHelper.speak(availableStopsToBeRead(stopsDetailsList),()-> listenToSpecifiedDestinationName(this));
//            }

        }).addOnFailureListener((exception) -> Toast.makeText(getContext(), R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show());

    }

    private void onTextChangeListener(AutoCompleteTextView autoCompleteTextView){
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {  updateDropDownListUsingGoogleMapsAPI(autoCompleteTextView); }
        });
    }

    /** M Osama: other functions */
    private void showLogInDialog(){
        LoginDialog dialog = new LoginDialog();                     //haidy:showing the login dialog
        dialog.show(getChildFragmentManager(), LoginDialog.TAG);
        dialog.setCancelable(false);
    }

    public void addToNeo4j(String location,String destination,String cost,String name,String transportationType){
        RetrofitClient.getInstance(NEO4J_BASE_URL).getApi().addNewRoute(location,destination,cost,name,transportationType).enqueue(new Callback<AddNewRoute>() {
            @Override
            public void onResponse(Call<AddNewRoute> call, Response<AddNewRoute> response) {            Log.i("Info","Addition Done");      }

            @Override
            public void onFailure(Call<AddNewRoute> call, Throwable t) {                                Log.i("Info","Addition Failure");    }
        });
    }

}


