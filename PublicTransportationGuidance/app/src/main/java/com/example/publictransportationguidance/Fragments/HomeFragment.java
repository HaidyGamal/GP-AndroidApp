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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.Adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.PathResults;
import com.example.publictransportationguidance.R;

public class HomeFragment extends Fragment {
    public HomeFragment() {}

    /* M Osama: Populating random data to be futurely replaced with real locations */
    String[] places={"Maadi","Heliopelis","Zamalek","Sheikh Zayed","October","Tagamo3"};
    String footer="Set Location On The Map";

    AutoCompleteTextView tvLocation,tvDestination;
    Button findResults;

    /* M Osama: OnCreateView used to connect the fragment java class with it's xml layout */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    /* M Osama: OnViewCreated used to write the code we want to execute in a fragment */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvLocation=view.findViewById(R.id.tv_location);
        tvDestination=view.findViewById(R.id.tv_destination);

        /* M Osama: Give inital values to the text views */
        CustomAutoCompleteAdapter list=new CustomAutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line,places,footer);

        /* M Osama: bind to our custom click listener interface */
        list.setOnFooterClickListener(()-> {
                Toast toast = Toast.makeText(getContext(), "Going to Google Maps!", Toast.LENGTH_LONG);

                Uri gmmIntentUri = Uri.parse("geo:30.0444,31.2357");             // Create a Uri from an intent string. Use the result to create an Intent. /*M Osama: entered latitude & longitude for Cairo*/
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri); // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                mapIntent.setPackage("com.google.android.apps.maps");            // Make the Intent explicit by setting the Google Maps package
                startActivity(mapIntent);                                        // Attempt to start an activity that can handle the Intent

                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
        });

        tvLocation.setAdapter(list);
        tvLocation.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id)-> {
            String itemSelected=tvLocation.getText()+"";
            if(itemSelected.equals(footer));
            else{ Toast.makeText(getActivity(), "To Be Edited", Toast.LENGTH_LONG).show(); }
        });

        tvDestination.setAdapter(list);
        tvDestination.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id)-> {
            String itemSelected=tvDestination.getText()+"";
            if(itemSelected.equals(footer));
            else{ Toast.makeText(getActivity(), "To Be Edited", Toast.LENGTH_LONG).show(); }
        });

        findResults =view.findViewById(R.id.btn_ok);
        findResults.setOnClickListener((View v) -> {
                Intent intent=new Intent(getActivity(), PathResults.class);
                startActivity(intent);
        });

    }
}


