package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.R;

public class SettingsFragment extends Fragment {
    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings, container, false);       // Inflate the layout for this fragment
        return view;
    }
}