package com.example.publictransportationguidance.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.UI.MainActivity;

public class VerifyDialog extends DialogFragment {
    Button ok;
    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_submit_route, container, false);
        ok = view.findViewById(R.id.ok_btn);

        ok.setOnClickListener((View v)-> {
                dismiss();
                intent=new Intent(getActivity().getBaseContext(), MainActivity.class);  //haidy: to return back to home page
                startActivity(intent);
        });
        return view;
    }
    public static String TAG = "Dialog";
}
