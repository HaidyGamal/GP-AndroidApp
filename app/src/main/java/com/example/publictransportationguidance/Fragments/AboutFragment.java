package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {
    public AboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAboutBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_about,container,false);
        return binding.getRoot();
    }
}