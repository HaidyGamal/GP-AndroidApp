package com.example.publictransportationguidance.Authentication;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.publictransportationguidance.Fragments.AddNewRouteFragment;
import com.example.publictransportationguidance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    Button sign;
    EditText em,pass;
    TextView account;
    String email,password;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog dialog;
     FirebaseAuth mAuth;
     FirebaseUser mUser;
    public static String TAG2 = "TAG22";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup);
        sign = findViewById(R.id.sign_btn);
        em=findViewById(R.id.emailET);
        pass=findViewById(R.id.passET);

        dialog=new ProgressDialog(this);

// ...
        // haidy:Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        //haidy:showing Verification Dialog
        sign.setOnClickListener((View v)-> {

                performAuth();


         });

    }

    private void performAuth() {
        email=em.getText().toString();
        password=pass.getText().toString();
        if(!email.matches(emailPattern)){
            em.setError("Enter correct Email");
        }else if(password.isEmpty()||password.length()<6){
            pass.setError("Enter correct password");
        }else{
            dialog.setMessage("Please wait ...");
            dialog.setTitle("Registration");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //haidy: sending the verification link
                        FirebaseUser user =mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignUp.this, "Verification Email has been sent", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG2,"ON Failure: Email Not sent"+e.getMessage());

                            }
                        });



                        dialog.dismiss();

                       // Toast.makeText(SignUp.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(SignUp.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }}


    }

