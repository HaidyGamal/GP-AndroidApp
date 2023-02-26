package com.example.publictransportationguidance.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.example.publictransportationguidance.POJO.ShortestPathResponse.Shortest;
import com.example.publictransportationguidance.POJO.StopsResponse.AllStops;
import com.example.publictransportationguidance.POJO.StopsResponse.StopModel;
import com.example.publictransportationguidance.API.RetrofitClient;
import com.example.publictransportationguidance.Adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.Room.DAO;
import com.example.publictransportationguidance.Room.RoomDB;
import com.example.publictransportationguidance.Tracking.PathResults;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentHomeBinding;
import com.example.publictransportationguidance.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    public HomeFragment() {}
    FragmentHomeBinding binding;

    String footer = "اختيار من الخريطة";
    String[] stops;
    public static List<String> stopsTemp=new ArrayList<>();

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

        DAO dao = RoomDB.getInstance(getContext()).Dao();

        RetrofitClient.getInstance().getApi().getAllStops().enqueue(new Callback<AllStops>() {
            @Override
            public void onResponse(Call<AllStops> call, Response<AllStops> response) {
                AllStops allStops=response.body();
                List<StopModel> stops= allStops.getAllNodes();
                // M Osama: delete old data if new greater data is received
                if(stops.size()>dao.getNumberOfRowsInStopTable()) {
                    dao.deleteAllStops();
                    // M Osama: caching Stops in Room (only if StopsTable is empty)
                    if (dao.getNumberOfRowsInStopTable() == 0) { for (StopModel st : stops)  dao.insertStop(st); }
                }
                Toast.makeText(getContext(), R.string.AvailableStopsAreUpdated, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<AllStops> call, Throwable t) { Toast.makeText(getContext(), R.string.CheckInternetConnection, Toast.LENGTH_SHORT).show(); }
        });

        stopsTemp=dao.getAllStops();
        this.stops = Shortest.listToArray(stopsTemp);                                 // M Osama: ArrayList to Array

        /* M Osama: Give inital values to the text views */
        CustomAutoCompleteAdapter list = new CustomAutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, this.stops, footer);

        /* M Osama: bind to our custom click listener interface */
        list.setOnFooterClickListener(() -> {
            Toast toast = Toast.makeText(getContext(), R.string.GoingToGoogleMaps, Toast.LENGTH_LONG);

            Uri gmmIntentUri = Uri.parse("geo:30.0444,31.2357");             // Create a Uri from an intent string. Use the result to create an Intent. /*M Osama: entered latitude & longitude for Cairo*/
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri); // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            mapIntent.setPackage("com.google.android.apps.maps");            // Make the Intent explicit by setting the Google Maps package
            startActivity(mapIntent);                                        // Attempt to start an activity that can handle the Intent

            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

        binding.tvLocation.setAdapter(list);
        binding.tvLocation.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            String itemSelected = binding.tvLocation.getText() + "";
            if (itemSelected.equals(footer)) ;
            else ;

        });

        binding.tvDestination.setAdapter(list);
        binding.tvDestination.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            String itemSelected = binding.tvDestination.getText() + "";
            if (itemSelected.equals(footer)) ;
            else ;

        });

        binding.btnOk.setOnClickListener((View v) -> {
            String location = binding.tvLocation.getText()+"";
            String destination = binding.tvDestination.getText()+"";

            if(location!="" && destination!=""){
                if(stopsTemp.contains(location) && stopsTemp.contains(destination)) {   /* Location & Destination are stored in our DB */
                    Intent intent = new Intent(getActivity(), PathResults.class);
                    intent.putExtra("LOCATION", location);
                    intent.putExtra("DESTINATION", destination);
                    startActivity(intent);
                }
                else{ Toast.makeText(getActivity(), R.string.PleaseChooseTwoAvailableStops, Toast.LENGTH_SHORT).show(); }
            }
            else { Toast.makeText(getActivity(), R.string.LocationAndDestinationMustBeSpecified, Toast.LENGTH_SHORT).show(); }
        });

        binding.distanceRBHomeFragment.setOnClickListener((View v)-> { Toast.makeText(getActivity(), R.string.PathsSortedAccordingToDistance, Toast.LENGTH_SHORT).show(); });
        binding.costRBHomeFragment.setOnClickListener((View v) -> { Toast.makeText(getActivity(), R.string.PathsSortedAccordingToCost, Toast.LENGTH_SHORT).show(); });

    }

}

