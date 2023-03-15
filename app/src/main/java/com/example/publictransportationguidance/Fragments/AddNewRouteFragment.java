package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.HelperClasses.Constants.IS_LOGGED_IN;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.Authentication.LoginDialog;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.UI.MainActivity;
import com.example.publictransportationguidance.UI.VerifyDialog;
import com.example.publictransportationguidance.databinding.FragmentAddNewRouteBinding;

public class AddNewRouteFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public AddNewRouteFragment() {}
    FragmentAddNewRouteBinding binding;

    ArrayAdapter<CharSequence> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_new_route,container,false);
        View rootView = binding.getRoot();

        adapter= ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.transportations, android.R.layout.simple_spinner_item);    // haidy: Creating an ArrayAdapter using the string array and a default spinner layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // haidy:  Specify the layout to use when the list of choices appears
        binding.spin.setAdapter(adapter);    // haidy: Applying the adapter to the spinner
        binding.spin.setOnItemSelectedListener(this);
        binding.submitBtn.setOnClickListener((View v)-> new VerifyDialog().show(getChildFragmentManager(), LoginDialog.TAG));

        /* M Osama: ask the user to log in if he isn't loggedIn to be able to add new route*/
        if(IS_LOGGED_IN ==0) {
            LoginDialog dialog = new LoginDialog();                     //haidy:showing the login dialog
            dialog.show(getChildFragmentManager(), LoginDialog.TAG);
            dialog.setCancelable(false);
        }

        return rootView;
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

}