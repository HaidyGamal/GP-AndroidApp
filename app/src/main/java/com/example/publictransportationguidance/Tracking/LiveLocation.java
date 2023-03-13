package com.example.publictransportationguidance.Tracking;

import static com.example.publictransportationguidance.HelperClasses.Constants.GPS_DISTANCE;
import static com.example.publictransportationguidance.HelperClasses.Constants.GPS_TIME_INTERVAL;
import static com.example.publictransportationguidance.HelperClasses.Constants.HANDLER_DELAY;
import static com.example.publictransportationguidance.HelperClasses.Constants.PERMISSIONS;
import static com.example.publictransportationguidance.HelperClasses.Constants.PERMISSION_ALL;
import static com.example.publictransportationguidance.HelperClasses.Constants.START_HANDLER_DELAY;
import static com.example.publictransportationguidance.HelperClasses.Constants.TRACKER_BASE_TXT;
import static com.example.publictransportationguidance.HelperClasses.Functions.getLocationName;

import com.example.publictransportationguidance.R;
import com.example.publictransportationguidance.databinding.LiveLocationBinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
public class LiveLocation extends AppCompatActivity implements LocationListener {
    LiveLocationBinding binding;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.live_location);

        if (Build.VERSION.SDK_INT >= 23) { requestPermissions(PERMISSIONS, PERMISSION_ALL); }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        delayerHandler();
    }

    /* M Osama: update location coordinates on location Changed */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        String stop = getLocationName(getApplicationContext(),location.getLatitude(),location.getLongitude());
        binding.locationTxt.setText(TRACKER_BASE_TXT+stop);

        Log.d("OsOs","Got Stop: "+stop+"Got Location: " + location.getLatitude() + ", " + location.getLongitude());                                                         /* M Osama: to assure for us that it's working */
        Toast.makeText(getApplicationContext(), "Got Stop: " + stop+" Got Coordinates: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();  /* M Osama: to be used to track user */

        locationManager.removeUpdates(this);
    }

    /* M Osama: start tracking if user accepted that his mobile's location can be used */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)  delayerHandler();
        else  finish();
    }

    /* M Osama: to ask the user to enable using his mobile Location */
    @SuppressLint("MissingPermission")
    private void requestLocation() {
        if (locationManager == null)  locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, GPS_DISTANCE, this);
            }

        }
    }

    /* M Osama: to send current location every 5 minutes */
    public void delayerHandler(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() { requestLocation(); handler.postDelayed(this, HANDLER_DELAY); }
        }, START_HANDLER_DELAY);
    }

}
