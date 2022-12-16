package com.example.publictransportationguidance.Authentication;

import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.R;

public class VerificationCodeDialog extends DialogFragment {
     TextView count_txt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verification, container, false);
        count_txt = view.findViewById(R.id.counter_txt);

        long maxCounter = 61000;

        new CountDownTimer(maxCounter , 1000 ) {
            //haidy: built in function that takes the max counter and the time step in milli sec. and generates down counter

            public void onTick(long millisUntilFinished) { count_txt.setText( (millisUntilFinished/1000 )+"sec"); }

            public void onFinish() {}
        }.start();

        return view;

    }
    public static String TAG = "Dialog";
//    public void showSoftKeyboard(View view){
//        if(view.requestFocus()){
//            InputMethodManager imm = (InputMethodManager) getActivity().getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
//        }
//    }

}
