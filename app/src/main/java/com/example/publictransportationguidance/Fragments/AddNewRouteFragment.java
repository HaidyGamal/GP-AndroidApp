package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.Authentication.LoginDialog;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.ui.VerifyDialog;
import com.example.publictransportationguidance.databinding.FragmentAddNewRouteBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AddNewRouteFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public AddNewRouteFragment() {}
    FragmentAddNewRouteBinding binding;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_new_route,container,false);

        SharedPrefs.init(getActivity());
        mAuth=FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.transportations, android.R.layout.simple_spinner_item);    // haidy: Creating an ArrayAdapter using the string array and a default spinner layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // haidy:  Specify the layout to use when the list of choices appears

        binding.spin.setAdapter(adapter);    // haidy: Applying the adapter to the spinner
        binding.spin.setOnItemSelectedListener(this);

        binding.submit.setOnClickListener((View v)-> new VerifyDialog().show(getChildFragmentManager(), LoginDialog.TAG));
        binding.logOut.setOnClickListener(v -> performLogOut(mAuth));

        /* M Osama: ask the user to log in if he isn't loggedIn to be able to add new route*/
        if(IS_LOGGED_IN ==0) showLogInDialog();

        return binding.getRoot();
    }


    //haidy: enabling the transportation text input
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        binding.transportType.setEnabled(!binding.spin.getSelectedItem().equals("Microbus"));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        binding.transportType.setEnabled(false);
    }

    private void showLogInDialog(){
        LoginDialog dialog = new LoginDialog();                     //haidy:showing the login dialog
        dialog.show(getChildFragmentManager(), LoginDialog.TAG);
        dialog.setCancelable(false);
    }

    private void performLogOut(FirebaseAuth auth) {
        auth.signOut();
        IS_LOGGED_IN = 0;
        SharedPrefs.write("IS_LOGGED_IN", IS_LOGGED_IN);
        Toast.makeText(getActivity(), R.string.LogoutSuccessful, Toast.LENGTH_SHORT).show();
        showLogInDialog();
    }
}