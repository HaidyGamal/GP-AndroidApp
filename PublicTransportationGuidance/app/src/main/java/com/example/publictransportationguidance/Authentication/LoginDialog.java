package com.example.publictransportationguidance.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.Authentication.SignUp;
import com.example.publictransportationguidance.R;

public class LoginDialog extends DialogFragment {
    Button log;
    TextView signup;
    Intent intent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //haidy: Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_login, container, false);
        log = view.findViewById(R.id.login);
        signup=view.findViewById(R.id.signup_btn);

        signup.setOnClickListener((View v)-> {
                intent=new Intent(getActivity().getBaseContext(), SignUp.class);
                startActivity(intent);
        });

        //haidy: to close the fragment
        log.setOnClickListener((View v) ->{ dismiss(); });

        return view;
    }
    public static String TAG = "Dialog";
}
