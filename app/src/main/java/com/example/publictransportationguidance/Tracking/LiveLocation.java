package com.example.publictransportationguidance.Tracking;

import com.example.publictransportationguidance.HelperClasses.Functions;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.LiveLocationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.checkerframework.checker.nullness.qual.NonNull;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LiveLocation extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView locationTxt;
    boolean clicked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiveLocationBinding binding = DataBindingUtil.setContentView(this, R.layout.live_location);
        locationTxt = findViewById(R.id.locationTxt);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        binding.stopBtn.setText(R.string.Stop);
        binding.stopBtn.setOnClickListener((View v)-> {
            if(binding.stopBtn.getText()=="Stop")
            { binding.stopBtn.setText(R.string.Continue);
            clicked=true;
            locationTxt.setText("أنت الان في ......");
            }
            else{
                binding.stopBtn.setText(R.string.Stop);
                clicked=false;
                getLastLocation();
            }
        });

        getLastLocation();

    }

   @SuppressLint("MissingPermission")
    private void getLastLocation(){
        do {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                try {
                                    Geocoder geocoder = new Geocoder(LiveLocation.this, new Locale("ar"));
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    locationTxt.setText("أنت الان في " + addresses.get(0).getLocality());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            }
        }while(!clicked);

    }

}
