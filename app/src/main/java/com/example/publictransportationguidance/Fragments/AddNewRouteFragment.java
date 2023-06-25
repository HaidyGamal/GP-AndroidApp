package com.example.publictransportationguidance.Fragments;

import static com.example.publictransportationguidance.helpers.GlobalVariables.IS_LOGGED_IN;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.publictransportationguidance.Authentication.LoginDialog;
import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.firestore.FirestoreDocumentCounter;
import com.example.publictransportationguidance.sharedPrefs.SharedPrefs;
import com.example.publictransportationguidance.ui.VerifyDialog;
import com.example.publictransportationguidance.databinding.FragmentAddNewRouteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewRouteFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public AddNewRouteFragment() {}
    FragmentAddNewRouteBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;
    EditText from,to,line;
    String id,name,flat,flong,tlat,tlong,type,cost,dist;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_new_route,container,false);

        SharedPrefs.init(getActivity());
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.transportations, android.R.layout.simple_spinner_item);    // haidy: Creating an ArrayAdapter using the string array and a default spinner layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // haidy:  Specify the layout to use when the list of choices appears
        from=binding.fromET;
        to=binding.toET;
        line=binding.transportType;
        focus(from,"نقطةالانطلاق");
        focus(to,"الوجهة");
        focus(line,"اسم الخط");
        focus(binding.costET,"اختيارى");
        binding.spin.setAdapter(adapter);    // haidy: Applying the adapter to the spinner
        binding.spin.setOnItemSelectedListener(this);

        binding.submit.setOnClickListener((View v)-> sendData());
        binding.logOut.setOnClickListener(v -> performLogOut(mAuth));

        /* M Osama: ask the user to log in if he isn't loggedIn to be able to add new route*/
        if(IS_LOGGED_IN ==0) showLogInDialog();

        return binding.getRoot();
    }

    private void sendData() {
        id=mUser.getEmail();
        name= binding.transportType.getText().toString();
        flat="123456";
        flong="123456";
        tlat="123456";
        tlong="123456";
        type=binding.spin.getSelectedItem().toString();
        if(!binding.costET.getText().equals("")||!binding.costET.getText().equals("اختيارى"))      cost=binding.costET.getText().toString();
        else cost="7";
        dist="0.7";
        db = FirebaseFirestore.getInstance();
        if(from.getText().toString().isEmpty()||from.getText().toString().equals("")||from.getText().toString().equals("نقطةالانطلاق"))     {from.setError("مطلوب!");}
        else if(to.getText().toString().isEmpty()||to.getText().toString().equals("")||to.getText().toString().equals("الوجهة"))     {to.setError("مطلوب!");}
        else if(name.isEmpty()||name.equals("")||name.equals("اسم الخط"))     {line.setError("مطلوب!");}

else{
        Map<String,Object> node=new HashMap<>();
        node.put("id",id);
        node.put("Node Name",name);
        node.put("FromLatitude",flat);
        node.put("FromLongitude",flong);
        node.put("ToLatitude",tlat);
        node.put("ToLongitude",tlat);
        node.put("Type",type);
        node.put("Cost",cost);
        node.put("Distance",dist);
        db.collection("Nodes")
                .add(node)
                .addOnSuccessListener(unused -> Log.d("TAG", "DocumentSnapshot added with ID: " + id))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding document", e));
            FirestoreDocumentCounter obj=new FirestoreDocumentCounter();
            obj.countRedundantData();
        new VerifyDialog().show(getChildFragmentManager(), LoginDialog.TAG);
    }}


    //haidy: enabling the transportation text input
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        binding.transportType.setEnabled(true);
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
    private void focus(EditText editText,String text){
        editText.setText(text);
        editText.setTextColor(Color.GRAY);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (editText.getCurrentTextColor() == Color.GRAY) {
                        editText.setText("");
                        editText.setTextColor(Color.BLACK);
                    }
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && editText.getText().toString().isEmpty()) {
                    editText.setText(text);
                    editText.setTextColor(Color.GRAY);
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

}