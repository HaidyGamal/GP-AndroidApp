package com.example.publictransportationguidance.shareLocation;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.publictransportationguidance.Fragments.ContactUsFragment;
import com.example.publictransportationguidance.databinding.SharingLocationRequestDialogBinding;
import com.example.publictransportationguidance.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RequestSharingLocationDialog extends DialogFragment {
    public static final String TAG = "REQUEST_SHARING_LOCATION_DIALOG";

    SharingLocationRequestDialogBinding binding;
    String friendEmail;
    ShareLocationDialogListener listener;



    public RequestSharingLocationDialog(String email) {
        friendEmail=email;
    }

    public void setShareLocationDialogListener(ShareLocationDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        binding = SharingLocationRequestDialogBinding.inflate(inflater, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();

        binding.friendEmail.setText(friendEmail);

        binding.radioButtonYes.setOnClickListener(v -> {
            listener.onOptionSelected(1,friendEmail);
            backToPreviousFragment();
            Toast.makeText(getContext(), "Yes", Toast.LENGTH_SHORT).show();
        });

        binding.radioButtonNo.setOnClickListener(v -> {
            listener.onOptionSelected(0,friendEmail);
            backToPreviousFragment();
            Toast.makeText(getContext(), "No", Toast.LENGTH_SHORT).show();
        });

        return builder.create();
    }

    private void backToPreviousFragment(){
        dismiss();
    }

}
