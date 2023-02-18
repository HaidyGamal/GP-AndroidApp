package com.example.publictransportationguidance.Authentication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentDialog;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.Authentication.SignUp;
import com.example.publictransportationguidance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginDialog extends DialogFragment {
    Button log;
    TextView signup;
    Intent intent;
    EditText em,pass;
    TextView account;
    String email,password;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog dialog;
    AlertDialog.Builder d;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //haidy: Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_login, container, false);
        log = view.findViewById(R.id.login);
        signup=view.findViewById(R.id.signup_btn);
        em=view.findViewById(R.id.username);
        pass=view.findViewById(R.id.password);
        email=em.getText().toString();
        password=pass.getText().toString();

        dialog=new ProgressDialog(getActivity());
        d=new AlertDialog.Builder(getActivity());

// ...
        // haidy:Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        signup.setOnClickListener((View v)-> {
                intent=new Intent(getActivity().getBaseContext(), SignUp.class);
                startActivity(intent);
        });

        //haidy: to close the fragment
        log.setOnClickListener((View v) ->{
            performLogin();
             });

        return view;
    }

    private void performLogin() {
        email = em.getText().toString();
        password = pass.getText().toString();
        if (!email.matches(emailPattern)) {
            em.setError("Enter correct Email");
        } else if (password.isEmpty() || password.length() < 6) {
            pass.setError("Enter correct password");
        } else {
            dialog.setMessage("Please wait ...");
            dialog.setTitle("LogIn");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        if(mUser.isEmailVerified()){
                        dismiss();
                        Toast.makeText(getActivity(), "Successful Login", Toast.LENGTH_SHORT).show();
                    }else{
                            d.setMessage("This Account is Not verified");
                            d.setTitle("Error");
                            d.show();
                            //haidy: The dialog is automatically dismissed when a dialog button is clicked.
                                    d.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    });

                        }

                    }else {
                        log.setError("");
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Incorrect Email Or Password" , Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public static String TAG = "Dialog";
}
