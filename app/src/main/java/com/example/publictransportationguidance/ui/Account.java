package com.example.publictransportationguidance.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.AccountBinding;

public class Account extends AppCompatActivity {
    public Account() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountBinding binding = DataBindingUtil.setContentView(this,R.layout.account);
}}
