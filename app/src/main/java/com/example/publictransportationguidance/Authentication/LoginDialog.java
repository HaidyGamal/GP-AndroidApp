package com.example.publictransportationguidance.Authentication;

import static com.example.publictransportationguidance.UI.MainActivity.navController;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.publictransportationguidance.Fragments.AddNewRouteFragment;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.SharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.UI.MainActivity;
import com.example.publictransportationguidance.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginDialog extends DialogFragment {
    FragmentLoginBinding binding;
    String email,password;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog dialog;
    AlertDialog.Builder LoginDialogBuilder;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        View rootView = binding.getRoot();
        SharedPrefs.init(getActivity());

        email=binding.email.getText().toString();
        password=binding.password.getText().toString();

        dialog=new ProgressDialog(getActivity());
        LoginDialogBuilder =new AlertDialog.Builder(getActivity());

        // haidy:Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        binding.signup.setOnClickListener((View v)-> { startActivity(new Intent(getActivity().getBaseContext(), SignUp.class)); });

        //haidy: to close the fragment
        binding.login.setOnClickListener((View v) ->{
            performLogin();
            MainActivity.isLoggedIn=1;
            SharedPrefs.write("IS_LOGGED_IN",MainActivity.isLoggedIn);
        });

        return rootView;
    }

    private void performLogin() {
        email = binding.email.getText().toString();
        password = binding.password.getText().toString();

        if (!email.matches(emailPattern)) { binding.email.setError("Enter correct Email"); }
        else if (password.isEmpty() || password.length() < 6) { binding.password.setError("Enter correct password");}
        else {
            dialog.setMessage("Please wait ...");
            dialog.setTitle("LogIn");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    navController.navigate(R.id.nav_add_new_route);
                    dialog.dismiss();
                    if(mUser.isEmailVerified()){ dismiss(); Toast.makeText(getActivity(), "Successful Login", Toast.LENGTH_SHORT).show();}
                    else{
                        LoginDialogBuilder.setMessage("This Account is Not verified");
                        LoginDialogBuilder.setTitle("Error");
                        LoginDialogBuilder.show();              //haidy: The dialog is automatically dismissed when a dialog button is clicked.
                        LoginDialogBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> { /* Continue with delete operation */ });
                    }
                }
                else { binding.login.setError("");    dialog.dismiss();    Toast.makeText(getActivity(), "Incorrect Email Or Password" , Toast.LENGTH_LONG).show(); }
            });
        }
    }
    public static String TAG = "Dialog";
}
