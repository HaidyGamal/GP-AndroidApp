package com.example.publictransportationguidance.googleMap;

import static com.example.publictransportationguidance.helpers.GlobalVariables.EAST;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LATITUDE_KEY;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LOCATION_NAME_KEY;
import static com.example.publictransportationguidance.helpers.GlobalVariables.LONGITUDE_KEY;
import static com.example.publictransportationguidance.helpers.GlobalVariables.NORTH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.SOUTH;
import static com.example.publictransportationguidance.helpers.GlobalVariables.WEST;
import static com.example.publictransportationguidance.helpers.Functions.getLocationName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.ActivityMapBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    double latitude=30.0444;
    double longitude=31.2357;

    com.google.android.gms.maps.GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.confirmPickUp.setOnClickListener(v -> {
            String locationName = getLocationName(getApplicationContext(),latitude,longitude)+"";
            if(locationName.equals("هناك مشكلة في الانترنت")){
                latitude=0.0;
                longitude=0.0;
                locationName="";
                Toast.makeText(getApplicationContext(), "هناك مشكلة في الانترنت", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(getApplicationContext(), "تم الاختيار بنجاح", Toast.LENGTH_SHORT).show();

            /* M Osama : pass picked lat/long to HomeFragment's parent class */
            Intent resultIntent = new Intent();
            resultIntent.putExtra(LATITUDE_KEY,latitude);
            resultIntent.putExtra(LONGITUDE_KEY,longitude);
            resultIntent.putExtra(LOCATION_NAME_KEY,locationName);
            setResult(Activity.RESULT_OK,resultIntent);
            finish();
            Log.i("TAG","From(MapActivity)"+"1- " + latitude + ",2- " + longitude + ",3- " + getLocationName(getApplicationContext(),latitude,longitude)+"");       /* M Osama: For debugging only */
        });

        binding.cancelPickUp.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "الغاء الاختيار من الخريطة", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {

        this.googleMap = googleMap;
        LatLng cairo = new LatLng(30.0444, 31.2357);                                                // M Osama: Update with Cairo's coordinates
        this.googleMap.addMarker(new MarkerOptions().position(cairo).draggable(true).title("Marker in Cairo"));     // M Osama: Add a marker in Cairo & move Camera

        CameraPosition cameraPosition = new CameraPosition.Builder().target(cairo).zoom(12).build();
        this.googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));                           // M Osama: Camera Zoomed to Cairo
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);                                                // M Osama: Enable user to zoom

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