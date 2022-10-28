package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.R;

public class AddNewRouteFragment extends Fragment {
    public AddNewRouteFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_new_route, container, false);  // Inflate the layout for this fragment
        return view;
    }
}