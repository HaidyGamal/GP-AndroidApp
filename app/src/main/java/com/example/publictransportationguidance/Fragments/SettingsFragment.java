package com.example.publictransportationguidance.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.SharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment{
    Boolean dark;
    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSettingsBinding binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_settings,container,false);
        View rootView = binding.getRoot();

        SharedPrefs.init(getContext());

        binding.modeBtn.setText("OFF");

        //haidy: we used Shared Preferences to save the mode if exit the app and go back again; m Ossama: we used SingletonSharedPrefs
        dark=SharedPrefs.readMap("night",false);   // light is default mode

        if(dark){ binding.modeBtn.setText("ON"); binding.navMode.setChecked(true); AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); }

        binding.navMode.setOnClickListener((View v)-> {                  //haidy: to toggle switch text
            if(dark){ AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);    SharedPrefs.write("night",false); }
            else {    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);   SharedPrefs.write("night",true);  }

            if(binding.modeBtn.getText()=="OFF")  { binding.modeBtn.setText("ON"); }
            else                                  { binding.modeBtn.setText("OFF"); }
        });
        binding.accountBtn.setOnClickListener(v -> { startActivity(new Intent(getActivity(), com.example.publictransportationguidance.UI.Account.class)); });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}