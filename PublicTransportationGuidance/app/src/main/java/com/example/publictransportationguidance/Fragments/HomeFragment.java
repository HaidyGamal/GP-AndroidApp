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

import com.example.publictransportationguidance.API.POJO.StopsResponse.StopModel;
import com.example.publictransportationguidance.Adapters.CustomAutoCompleteAdapter;
import com.example.publictransportationguidance.Room.DAO;
import com.example.publictransportationguidance.Room.RoomDB;
import com.example.publictransportationguidance.UI.MainActivity;
import com.example.publictransportationguidance.UI.PathResults;
import com.example.publictransportationguidance.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public HomeFragment() {}

    RadioButton costRadioBtn,distanceRadioBtn;
    AutoCompleteTextView tvLocation, tvDestination;
    Button findResults;

    String footer = "اختيار من الخريطة";

    String[] stops;
    List<String> stopsTemp =new ArrayList<>();

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

        DAO dao = RoomDB.getInstance(getContext()).Dao();

        findResults = view.findViewById(R.id.btn_ok);
        tvLocation = view.findViewById(R.id.tv_location);
        tvDestination = view.findViewById(R.id.tv_destination);
        costRadioBtn=view.findViewById(R.id.costRB_homeFragment);
        distanceRadioBtn=view.findViewById(R.id.distanceRB_homeFragment);

//        dao.deleteAllStops();
//        Toast.makeText(getActivity(), "HomeFragment:"+dao.getNumberOfRowsInStopTable()+"", Toast.LENGTH_SHORT).show();

        stopsTemp=dao.getAllStops();
        this.stops = stopsTemp.toArray(new String[0]);                                  // M Osama: ArrayList to Array
        //        List<StopModel> stops = dao.getAllStops();  // M Osama: Reading the locations cached in Room
//        for (StopModel tempStop : stops){ stopsTemp.add(tempStop.getName());}           // M Osama: Populating the ArrayList with names from Room



        Toast.makeText(getContext(), "Before populating list"+dao.getNumberOfRowsInStopTable(), Toast.LENGTH_SHORT).show();


        /* M Osama: Give inital values to the text views */
        CustomAutoCompleteAdapter list = new CustomAutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, this.stops, footer);

        /* M Osama: bind to our custom click listener interface */
        list.setOnFooterClickListener(() -> {
            Toast toast = Toast.makeText(getContext(), "جاري الذهاب إلي جوجل", Toast.LENGTH_LONG);

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
            else ;

        });

        tvDestination.setAdapter(list);
        tvDestination.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            String itemSelected = tvDestination.getText() + "";
            if (itemSelected.equals(footer)) ;
            else ;

        });

        findResults.setOnClickListener((View v) -> {
            String location = tvLocation.getText()+"";
            String destination = tvDestination.getText()+"";
            if(location!="" && destination!=""){
                if(stopsTemp.contains(location) && stopsTemp.contains(destination)) {   /* Location & Destination are stored in our DB */
                    Intent intent = new Intent(getActivity(), PathResults.class);
                    intent.putExtra("LOCATION", location);
                    intent.putExtra("DESTINATION", destination);
                    startActivity(intent);
                }
                else{ Toast.makeText(getActivity(), "عفوا يجب اختيار نقطتين متوفرتين لدينا", Toast.LENGTH_SHORT).show(); }
            }
            else { Toast.makeText(getActivity(), "يجب تحديد نقطة للانطلاق وكذلك نقطة انتهاء أولا", Toast.LENGTH_SHORT).show(); }
        });

        distanceRadioBtn.setOnClickListener((View v)-> {
                Toast.makeText(getActivity(), "يتم الترتيب طبقا للمسافة", Toast.LENGTH_SHORT).show();
        });
        costRadioBtn.setOnClickListener((View v) -> {
                Toast.makeText(getActivity(), "يتم الترتيب طبقا للسعر", Toast.LENGTH_SHORT).show();
        });

    }

}

