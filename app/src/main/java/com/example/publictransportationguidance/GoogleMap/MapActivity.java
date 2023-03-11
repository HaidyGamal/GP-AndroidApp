package com.example.publictransportationguidance.GoogleMap;

import static com.example.publictransportationguidance.HelperClasses.Constants.EAST;
import static com.example.publictransportationguidance.HelperClasses.Constants.LATITUDE_KEY;
import static com.example.publictransportationguidance.HelperClasses.Constants.LOCATION_NAME_KEY;
import static com.example.publictransportationguidance.HelperClasses.Constants.LONGITUDE_KEY;
import static com.example.publictransportationguidance.HelperClasses.Constants.NORTH;
import static com.example.publictransportationguidance.HelperClasses.Constants.SOUTH;
import static com.example.publictransportationguidance.HelperClasses.Constants.WEST;
import static com.example.publictransportationguidance.HelperClasses.Functions.getLocationName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.UI.MainActivity;
import com.example.publictransportationguidance.databinding.ActivityMapBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    double latitude;
    double longitude;
    com.google.android.gms.maps.GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.confirmPickUp.setOnClickListener(v -> {

            /* M Osama : pass picked lat/long to HomeFragment's parent class */
            Intent toMainActivity = new Intent(this, MainActivity.class);
            toMainActivity.putExtra(LATITUDE_KEY,latitude);
            toMainActivity.putExtra(LONGITUDE_KEY,longitude);
            toMainActivity.putExtra(LOCATION_NAME_KEY,getLocationName(getApplicationContext(),latitude,longitude)+"");
            startActivity(toMainActivity);
            Toast.makeText(this, "MapActivity 1- " + latitude + ",2- " + longitude + ",3- " + getLocationName(getApplicationContext(),latitude,longitude)+"", Toast.LENGTH_SHORT).show();    /* To be deleted */
            Toast.makeText(getApplicationContext(), "تم الاختيار بنجاح", Toast.LENGTH_SHORT).show();
        });

        binding.cancelPickUp.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "الغاء الاختيار من الخريطة", Toast.LENGTH_SHORT).show();
            finish();
        });


    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng maadi = new LatLng(29.9743704, 31.2741588);               // Add a marker in Maadi and move the camera
        this.googleMap.addMarker(new MarkerOptions().position(maadi).draggable(true).title("Marker in Maadi"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(maadi));
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);            // Enable the zoom controls for the map
        googleMap.setOnMarkerDragListener(new com.google.android.gms.maps.GoogleMap.OnMarkerDragListener(){
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {}

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {}

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {}
        });

        googleMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            markerOptions.title("Os");
            googleMap.clear();
            googleMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(SOUTH,WEST),new LatLng(NORTH,EAST)));
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 30);
            googleMap.animateCamera(location);
            googleMap.addMarker(markerOptions);

            markerOptions.flat(true);
            markerOptions.draggable(true);
            latitude=markerOptions.getPosition().latitude;
            longitude=markerOptions.getPosition().longitude;
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { super.onPointerCaptureChanged(hasCapture); }

}