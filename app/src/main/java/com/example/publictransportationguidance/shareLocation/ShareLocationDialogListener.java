package com.example.publictransportationguidance.shareLocation;

public interface ShareLocationDialogListener {
    void onOptionSelected(int option,String email);
    void onOptionSelected(int option,String trackingEmail,String trackedEmail);
}
