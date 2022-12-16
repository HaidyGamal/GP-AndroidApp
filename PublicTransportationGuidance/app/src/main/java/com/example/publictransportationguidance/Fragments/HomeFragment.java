package com.example.publictransportationguidance.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.publictransportationguidance.API.POJO.StopsResponse.StopModel;
import com.example.publictransportationguidance.Adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.Room.RoomDB;
import com.example.publictransportationguidance.UI.PathResults;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.UI.StopViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public HomeFragment() {}

    RadioButton costRadioBtn;
    RadioButton distanceRadioBtn;

    String footer = "Set Location On The Map";

    String[] stops;
    ArrayList<String> stopsTemp =new ArrayList<>();

    AutoCompleteTextView tvLocation, tvDestination;
    Button findResults;


    /* M Osama: OnCreateView used to connect the fragment java class with it's xml layout */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    /* M Osama: OnViewCreated used to write the code we want to execute in a fragment */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        tvLocation = view.findViewById(R.id.tv_location);
        tvDestination = view.findViewById(R.id.tv_destination);

        costRadioBtn=view.findViewById(R.id.costRB_homeFragment);
        distanceRadioBtn=view.findViewById(R.id.distanceRB_homeFragment);

        List<StopModel> stops = RoomDB.getInstance(getActivity()).Dao().getAllStops();  // M Osama: Reading the locations cached in Room
        for (StopModel tempStop : stops){
            stopsTemp.add(tempStop.getName());}           // M Osama: Populating the ArrayList with names from Room
        this.stops = stopsTemp.toArray(new String[0]);                                      // M Osama: ArrayList to Array

        /* M Osama: Give inital values to the text views */
        CustomAutoCompleteAdapter list = new CustomAutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, this.stops, footer);

        /* M Osama: bind to our custom click listener interface */
        list.setOnFooterClickListener(() -> {
            Toast toast = Toast.makeText(getContext(), "Going to Google Maps!", Toast.LENGTH_LONG);

            Uri gmmIntentUri = Uri.parse("geo:30.0444,31.2357");             // Create a Uri from an intent string. Use the result to create an Intent. /*M Osama: entered latitude & longitude for Cairo*/
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri); // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            mapIntent.setPackage("com.google.android.apps.maps");            // Make the Intent explicit by setting the Google Maps package
            startActivity(mapIntent);                                        // Attempt to start an activity that can handle the Intent

            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

        tvLocation.setAdapter(list);
        tvLocation.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            String itemSelected = tvLocation.getText() + "";
            if (itemSelected.equals(footer)) ;
            else Toast.makeText(getActivity(), "To Be Edited", Toast.LENGTH_LONG).show();

        });

        tvDestination.setAdapter(list);
        tvDestination.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            String itemSelected = tvDestination.getText() + "";
            if (itemSelected.equals(footer)) ;
            else Toast.makeText(getActivity(), "To Be Edited", Toast.LENGTH_LONG).show();

        });

        findResults = view.findViewById(R.id.btn_ok);
        findResults.setOnClickListener((View v) -> {
            String location = tvLocation.getText()+"";
            String destination = tvDestination.getText()+"";
            if(location!="" && destination!=""){
                Intent intent = new Intent(getActivity(), PathResults.class);
                intent.putExtra("LOCATION",location);
                intent.putExtra("DESTINATION",destination);
                startActivity(intent);
            }
            else {
                Toast.makeText(getActivity(), "Choose Location & Destination First", Toast.LENGTH_SHORT).show();
            }
        });

        distanceRadioBtn.setOnClickListener((View v)-> {
                Toast.makeText(getActivity(), "Distance", Toast.LENGTH_SHORT).show();
        });

        costRadioBtn.setOnClickListener((View v) -> {
                Toast.makeText(getActivity(), "Cost", Toast.LENGTH_SHORT).show();
        });

    }

}




// CustomAutoCompleteAdapter list=new CustomAutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line,stations.toArray(new String[0]), footer);