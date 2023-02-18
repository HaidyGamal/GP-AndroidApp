package com.example.publictransportationguidance.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.Room.DAO;
import com.example.publictransportationguidance.Room.RoomDB;
import com.example.publictransportationguidance.UI.MainActivity;

public class SettingsFragment extends Fragment{
    Button mode;
    TextView Account;
    Intent intent;
    Boolean dark;
    Switch btn;
    SharedPreferences p;
    SharedPreferences.Editor editor;
    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //haidy: Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_settings, container, false); // haidy:Inflate the layout for this fragment
        mode = view.findViewById(R.id.nav_mode);
        btn=view.findViewById(R.id.nav_mode);
        mode.setText("OFF");
        //haidy: we used Shared Preferences to save the mode if exit the app and go back again
        p= getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        dark=p.getBoolean("night",false); //light is default mode
        if(dark){ mode.setText("ON"); btn.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);}

        mode.setOnClickListener((View v)-> {                  //haidy: to toggle switch text
            if(dark){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor=p.edit();
                editor.putBoolean("night",false);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor=p.edit();
                editor.putBoolean("night",true);
            }
            editor.apply();
            if(mode.getText()=="OFF") {
                mode.setText("ON");}
            else mode.setText("OFF");
        });
        Account=view.findViewById(R.id.account_btn);
        Account.setOnClickListener(v -> {
            intent =new Intent(getActivity(), Account.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}