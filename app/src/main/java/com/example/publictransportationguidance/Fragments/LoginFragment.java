package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.publictransportationguidance.R;

public class LoginFragment extends DialogFragment {

    Button log;
    // Required empty public constructor
    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login, container, false);


        return view;

    }
}