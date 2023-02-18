package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.publictransportationguidance.R;

public class ContactUsFragment extends Fragment {
    public ContactUsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contact_us, container, false);     // Inflate the layout for this fragment
        return view;
    }
}