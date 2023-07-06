package com.example.publictransportationguidance.Authentication;

import static com.example.publictransportationguidance.helpers.GlobalVariables.FIRESTORE_AUTHENTICATION_COLLECTION_NAME;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.FragmentSignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        email = binding.em.getText().toString();
        password = binding.pass.getText().toString();
        fname = binding.FnameET.getText().toString();
        lname = binding.LnameET.getText().toString();
        phone = binding.phoneET.getText().toString();
        Matcher matcher = mobilePattern.matcher(phone);
        Map<String, Object> data = new HashMap<>();
        data.put("FirstName", fname);
        data.put("LastName", lname);
        data.put("Phone", phone);

        if (fname.isEmpty()) {
            binding.FnameET.setError("مطلوب!");
        } else if (lname.isEmpty()) {
            binding.LnameET.setError("مطلوب!");
        } else if (!email.matches(emailPattern)) {
            binding.em.setError(getString(R.string.EnterCorrectEmail));
        } else if (password.isEmpty() || password.length() < 6) {
            binding.pass.setError(getString(R.string.EnterCorrectPassword));
        } else if (phone.isEmpty() || phone.length() < 11 || phone.length() > 11 || !matcher.matches()) {
            binding.phoneET.setError("يرجى اضافة رقم هاتف صحيح");
        } else {
            dialog.setMessage(getResources().getString(R.string.PleaseWait));
            dialog.setTitle(getString(R.string.Registration));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String mail = user.getEmail();

                    db.collection(FIRESTORE_AUTHENTICATION_COLLECTION_NAME).document(mail)
                            .set(data)
                            .addOnSuccessListener(unused -> Log.d("TAG", "DocumentSnapshot added with ID: " + mail))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error adding document", e));

                    user.sendEmailVerification().addOnSuccessListener(unused -> {
                        Toast.makeText(SignUp.this, R.string.VerificationEmailHasBeenSent, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignUp.this, R.string.SuccessfulRegistration, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Log.d(TAG2, "ON Failure: Email Not sent" + e.getMessage());
                    });
                } else {
                    dialog.dismiss();
                    Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}


