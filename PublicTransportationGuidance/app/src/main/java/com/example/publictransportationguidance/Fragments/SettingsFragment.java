package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.R;

public class SettingsFragment extends Fragment{
    Button mode;
    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //haidy: Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_settings, container, false); // haidy:Inflate the layout for this fragment
        mode = view.findViewById(R.id.nav_mode);
        mode.setText("OFF");
        mode.setOnClickListener((View v)-> {                  //haidy: to toggle switch text
            if(mode.getText()=="OFF") mode.setText("ON");
            else mode.setText("OFF");
        });

        return view;
    }

}