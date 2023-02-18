package com.example.publictransportationguidance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class LoginDialog extends DialogFragment {
    Button log;
    TextView signup;
    Intent intent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //haidy: Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_login, container, false);
        log = view.findViewById(R.id.login);
        signup=view.findViewById(R.id.signup_btn);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(getActivity().getBaseContext(), SignUp.class);
                startActivity(intent);
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();                     //haidy: to close the fragment
            }
        });
        return view;
    }
    public static String TAG = "Dialog";
}
