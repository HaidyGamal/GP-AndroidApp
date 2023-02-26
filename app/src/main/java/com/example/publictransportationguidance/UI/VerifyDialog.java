package com.example.publictransportationguidance.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentSettingsBinding;
import com.example.publictransportationguidance.databinding.FragmentSubmitRouteBinding;

public class VerifyDialog extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentSubmitRouteBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_submit_route,container,false);
        View rootView = binding.getRoot();

        //haidy: to return back to home page
        binding.okBtn.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(getActivity().getBaseContext(), MainActivity.class));
        });

        return rootView;
    }
    public static String TAG = "Dialog";
}
