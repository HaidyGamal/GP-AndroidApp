package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentContactUsBinding;

public class ContactUsFragment extends Fragment {
    public ContactUsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentContactUsBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_contact_us,container,false);
        View rootView = binding.getRoot();
        return rootView;
    }
}