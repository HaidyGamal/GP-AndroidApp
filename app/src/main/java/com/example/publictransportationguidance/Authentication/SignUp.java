package com.example.publictransportationguidance.Authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentSignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    FragmentSignupBinding binding;
    String email,password,fname,lname,phone;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Pattern mobilePattern = Pattern.compile("^01.*");
    ProgressDialog dialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;

    public static String TAG2 = "TAG22";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.fragment_signup);

        dialog=new ProgressDialog(this);

        // haidy:Initialize Firebase Auth
        db=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        //haidy:showing Verification Dialog
        binding.sign.setOnClickListener((View v)-> performAuth());
    }

    private void performAuth() {
        email=binding.em.getText().toString();
        String mail=mUser.getEmail();
        password=binding.pass.getText().toString();
        fname=binding.FnameET.getText().toString();
        lname=binding.LnameET.getText().toString();
        phone=binding.phoneET.getText().toString();
        //haidy: Create a Matcher object and apply the pattern to the input string
        Matcher matcher = mobilePattern.matcher(phone);
        //haidy: sending data to Firestore
        Map<String,Object> data=new HashMap<>();
        data.put("FirstName",fname);
        data.put("LastName",lname);
        data.put("Phone",phone);
        db.collection("users").document(mail)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + mail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error adding document", e);
                    }
                });
        if(fname.isEmpty())     {binding.FnameET.setError("Required field!");}
        else if(lname.isEmpty()) {binding.LnameET.setError("Required field!");}
        else if(!email.matches(emailPattern)){ binding.em.setError(getString(R.string.EnterCorrectEmail)); }
        else if(password.isEmpty()||password.length()<6){ binding.pass.setError(getString(R.string.EnterCorrectPassword)); }
        else if (phone.isEmpty()||phone.length()<11||phone.length()>11||!matcher.matches()) {
            binding.phoneET.setError("Enter Correct Mobile Number"); }
        else{
            dialog.setMessage(getResources().getString(R.string.PleaseWait));
            dialog.setTitle(getString(R.string.Registration));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user =mAuth.getCurrentUser();      //haidy: sending the verification link

                    user.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(SignUp.this, R.string.VerificationEmailHasBeenSent, Toast.LENGTH_LONG).show()).addOnFailureListener(e -> Log.d(TAG2,"ON Failure: Email Not sent"+e.getMessage()));
//
                    Toast.makeText(SignUp.this, R.string.SuccessfulRegistration, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();                   // M Osama: return to addNewRoute & logIn dialog after registration
                }
                else {  dialog.dismiss();  Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show(); }
            });
        }}


}

