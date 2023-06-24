package com.example.publictransportationguidance.Authentication;

import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class LoginDialog extends DialogFragment {
    FragmentLoginBinding binding;
    String email,password;
    ProgressDialog dialog;
    AlertDialog.Builder LoginDialogBuilder;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        View rootView = binding.getRoot();
        SharedPrefs.init(getActivity());

        email=binding.email.getText().toString();
        password=binding.password.getText().toString();

        dialog=new ProgressDialog(getActivity());
        LoginDialogBuilder =new AlertDialog.Builder(getActivity());

        // haidy:Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                            Toast.makeText(getActivity(), R.string.SuccessfulLogin , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else { binding.login.setError("");    dialog.dismiss();    Toast.makeText(getActivity(), R.string.InCorrectEmailOrPassword , Toast.LENGTH_LONG).show(); }
            });
        }
    }

    public static String TAG = "Dialog";
}
