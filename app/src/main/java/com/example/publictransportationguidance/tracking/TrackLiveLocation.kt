package com.example.publictransportationguidance.tracking

import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.publictransportationguidance.R
import com.example.publictransportationguidance.helpers.GlobalVariables.BUNDLE_PATH
import com.example.publictransportationguidance.helpers.GlobalVariables.INTENT_PATH
import com.example.publictransportationguidance.tracking.trackingModule.locationTracker.Tracker.Companion.REQUEST_LOCATION_PERMISSION
import com.example.publictransportationguidance.tracking.trackingModule.locationTracker.Tracker.Companion.checkPermissions
import com.example.publictransportationguidance.tracking.trackingModule.locationTracker.Tracker.Companion.requestLocationUpdates
import com.example.publictransportationguidance.tracking.trackingModule.util.logic.PathUtils.Companion.showExpectedPath
import com.example.publictransportationguidance.tracking.trackingModule.util.ui.CameraUtils.Companion.showDefaultLocationOnMap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class TrackLiveLocation : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        lateinit var googleMap: GoogleMap
        var listOfActualPathNodes = ArrayList<LatLng>()
    }

    private lateinit var defaultLocation: LatLng                            /* M Osama: default location to move camera to */
    private lateinit var fusedLocationClient: FusedLocationProviderClient   // M Osama: used to return user location
    private lateinit var handler: Handler                                   // M Osama: handler that runs a timer

    lateinit var pathNodes: ArrayList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_location)

        pathNodes = (intent.getBundleExtra(INTENT_PATH)?.getSerializable(BUNDLE_PATH) as? ArrayList<LatLng>)!!

        /* M Osama: buildingMap for tracking user's location(Car) */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.trackingLocationMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /* M Osama: tracking user's latLng */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        handler = Handler()
        checkPermissions(fusedLocationClient, applicationContext, handler, this)

    }

    override fun onResume() {
        super.onResume()
        checkPermissions(fusedLocationClient, applicationContext, handler, this)
    }

    /* M Osama: called once; only when the map is loaded on the screen */
    override fun onMapReady(googleMap: GoogleMap) {
        TrackLiveLocation.googleMap = googleMap
        defaultLocation = LatLng(30.0444, 31.2357)
        showDefaultLocationOnMap(googleMap,defaultLocation)

        Handler().postDelayed({ showExpectedPath(googleMap, pathNodes, Color.BLACK) }, 3000)

    }


    /* If permissions is refused; fire a toast
     If permissions is granted; start requesting location updates */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { requestLocationUpdates(fusedLocationClient, applicationContext, handler) }
            else { Toast.makeText(this, "No permission accepted till now", Toast.LENGTH_SHORT).show() }
        }
    }

}
