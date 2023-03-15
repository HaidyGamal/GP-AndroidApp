package com.example.publictransportationguidance.Authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentSignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    FragmentSignupBinding binding;
    String email,password;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog dialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public static String TAG2 = "TAG22";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.fragment_signup);

        dialog=new ProgressDialog(this);

        // haidy:Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        //haidy:showing Verification Dialog
        binding.sign.setOnClickListener((View v)-> performAuth());
    }

    private void performAuth() {
        email=binding.em.getText().toString();
        password=binding.pass.getText().toString();
        if(!email.matches(emailPattern)){ binding.em.setError(getString(R.string.EnterCorrectEmail)); }
        else if(password.isEmpty()||password.length()<6){ binding.pass.setError(getString(R.string.EnterCorrectPassword)); }
        else{
            dialog.setMessage(getResources().getString(R.string.PleaseWait));
            dialog.setTitle(getString(R.string.Registration));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user =mAuth.getCurrentUser();      //haidy: sending the verification link
                    user.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(SignUp.this, R.string.VerificationEmailHasBeenSent, Toast.LENGTH_LONG).show()).addOnFailureListener(e -> Log.d(TAG2,"ON Failure: Email Not sent"+e.getMessage()));
                    Toast.makeText(SignUp.this, R.string.SuccessfulRegistration, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();                   // M Osama: return to addNewRoute & logIn dialog after registration
                }
                else {  dialog.dismiss();  Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show(); }
            });
        }}


}

