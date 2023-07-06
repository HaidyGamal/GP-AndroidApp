package com.example.publictransportationguidance.Authentication;

import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SHARE_LOCATION_COLLECTION_NAME;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.ui.MainActivity;
import com.example.publictransportationguidance.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginDialog extends DialogFragment {
    FragmentLoginBinding binding;
    String email,password;
    ProgressDialog dialog;
    AlertDialog.Builder LoginDialogBuilder;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        View rootView = binding.getRoot();
        SharedPrefs.init(getActivity());

        email=binding.email.getText().toString();
        password=binding.password.getText().toString();

        dialog=new ProgressDialog(getActivity());
        LoginDialogBuilder =new AlertDialog.Builder(getActivity());

        firebaseInitializer();

        binding.signup.setOnClickListener((View v)-> startActivity(new Intent(getActivity().getBaseContext(), SignUp.class)));

        //haidy: to close the fragment
        binding.login.setOnClickListener((View v) ->{
            performLogin();
            IS_LOGGED_IN =1;
            SharedPrefs.write("IS_LOGGED_IN",IS_LOGGED_IN);
        });

        binding.back.setOnClickListener(v -> startActivity(new Intent(getContext(), MainActivity.class)));

        return rootView;
    }

    private void performLogin() {

        String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        email = binding.email.getText().toString();
        password = binding.password.getText().toString();

        if (!email.matches(emailPattern)) { binding.email.setError(getResources().getString(R.string.EnterCorrectEmail)); }
        else if (password.isEmpty() || password.length() < 6) { binding.password.setError(getResources().getString(R.string.EnterCorrectPassword));}
        else {
            dialog.setMessage(getString(R.string.PleaseWait));
            dialog.setTitle(getString(R.string.Login));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    FirebaseUser mUser=mAuth.getCurrentUser();
                    if(mUser!=null) {
                        if (!mUser.isEmailVerified()) {
                            LoginDialogBuilder.setMessage(R.string.ThisAccountIsNotVerified);
                            LoginDialogBuilder.setTitle(R.string.Error);
                            LoginDialogBuilder.show();              //haidy: The dialog is automatically dismissed when a dialog button is clicked.
                            LoginDialogBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> { /* Continue with delete operation */ });
                        } else {
                            dismiss();
                            docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(Objects.requireNonNull(mUser.getEmail()));
                            ensureDocumentIsExist(mUser.getEmail());
                            Toast.makeText(getActivity(), R.string.SuccessfulLogin , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else { binding.login.setError("");    dialog.dismiss();    Toast.makeText(getActivity(), R.string.InCorrectEmailOrPassword , Toast.LENGTH_LONG).show(); }
            });
        }
    }

    public static String TAG = "Dialog";

    /* M Osama: can be deleted if we used user's collection instead of FriendShip collection */
    private void ensureDocumentIsExist(String documentId){
        docRef = db.collection(SHARE_LOCATION_COLLECTION_NAME).document(documentId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {}
                else initializeAccount();
            } else Toast.makeText(getActivity(), "Failed to retrieve document", Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeAccount() {
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {}
            else {
                Map<String, Object> data = new HashMap<>();                                         // Create a new document with the user's email as the document ID

                data.put("friends", new ArrayList<Map<String,Object>>());
                data.put("lat", "");
                data.put("long", "");
                data.put("locationName", "");

                docRef.set(data)
                        .addOnSuccessListener(v -> Log.i("OSOS","Done"))
                        .addOnFailureListener(v -> Log.i("OSOS","De7k"));
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to retrieve document", Toast.LENGTH_SHORT).show());
    }

    private void firebaseInitializer(){
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }
}
