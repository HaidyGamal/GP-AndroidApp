package com.example.publictransportationguidance.tracking;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.publictransportationguidance.R;

public class LoadingDialog {

    Activity activity;
    AlertDialog alertDialog;

    LoadingDialog(Activity activity){
        this.activity=activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();
    }

    void endLoadingDialog(){
        alertDialog.dismiss();
    }

}
