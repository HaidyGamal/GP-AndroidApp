package com.example.publictransportationguidance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    Button sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);
        sign = findViewById(R.id.sign_btn);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //haidy:showing Verification Dialog
                new VerificationCodeDialog().show(
                        getSupportFragmentManager(), VerificationCodeDialog.TAG);
            }
        });
}
 }
