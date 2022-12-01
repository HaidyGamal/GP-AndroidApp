package com.example.publictransportationguidance.Authentication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.publictransportationguidance.R;

public class SignUp extends AppCompatActivity {
    Button sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);
        sign = findViewById(R.id.sign_btn);

        //haidy:showing Verification Dialog
        sign.setOnClickListener((View v)-> {
                new VerificationCodeDialog().show(getSupportFragmentManager(), VerificationCodeDialog.TAG);
        });
    }
}
