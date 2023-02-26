package com.example.publictransportationguidance.Authentication;

import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.VerificationBinding;

public class VerificationCodeDialog extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VerificationBinding binding = DataBindingUtil.inflate(inflater,R.layout.verification,container,false);
        View rootView = binding.getRoot();

        long maxCounter = 61000;

        //haidy: built in function that takes the max counter and the time step in milli sec. and generates down counter
        new CountDownTimer(maxCounter , 1000 ) {
            public void onTick(long millisUntilFinished) { binding.counterTxt.setText( (millisUntilFinished/1000 )+"sec"); }
            public void onFinish() {}
        }.start();

        return rootView;
    }
    public static String TAG = "Dialog";
//    public void showSoftKeyboard(View view){
//        if(view.requestFocus()){
//            InputMethodManager imm = (InputMethodManager) getActivity().getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
//        }
//    }

//
}
