package com.example.publictransportationguidance.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.Authentication.LoginDialog;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.UI.VerifyDialog;

public class AddNewRouteFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public AddNewRouteFragment() {}
    Spinner spinner;
    EditText input;
    ArrayAdapter<CharSequence> adapter;
    Button submit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_new_route, container, false);  // Inflate the layout for this fragment

        spinner = view.findViewById(R.id.spin);
        // haidy: Creating an ArrayAdapter using the string array and a default spinner layout
        adapter= ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.transportations, android.R.layout.simple_spinner_item);
        // haidy:  Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // haidy: Applying the adapter to the spinner
        spinner.setAdapter(adapter);

        input=view.findViewById(R.id.transport);
        submit=view.findViewById(R.id.submit_btn);
        spinner.setOnItemSelectedListener(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VerifyDialog().show(getChildFragmentManager(), LoginDialog.TAG);

            }
        });
        //haidy:showing the login fragment
        new LoginDialog().show(getChildFragmentManager(), LoginDialog.TAG);
        return view;
}
    //haidy: enabling the transportation text input
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(spinner.getSelectedItem().equals("Microbus"))  input.setEnabled(false);
        else input.setEnabled(true);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        input.setEnabled(false);
    }
}