package com.example.publictransportationguidance.tracking.trackingModule.trackingModule

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.publictransportationguidance.R
import com.example.publictransportationguidance.helpers.GlobalVariables.BUNDLE_PATH
import com.example.publictransportationguidance.helpers.GlobalVariables.INTENT_PATH
import com.example.publictransportationguidance.tracking.trackingModule.util.logic.PathUtils.Companion.showExpectedPath
import com.example.publictransportationguidance.tracking.trackingModule.util.ui.CameraUtils.Companion.showDefaultLocationOnMap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class TrackLiveLocation : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        @kotlin.jvm.JvmField
        var listOfActualPathNodes: ArrayList<LatLng> = ArrayList<LatLng>()
        lateinit var googleMap: GoogleMap
    }

    private lateinit var defaultLocation: LatLng                            /* M Osama: default location to move camera to */
    lateinit var pathNodes: ArrayList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_location)

        pathNodes = (intent.getBundleExtra(INTENT_PATH)?.getSerializable(BUNDLE_PATH) as? ArrayList<LatLng>)!!

        /* M Osama: buildingMap for tracking user's location(Car) */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.trackingLocationMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    
    /* M Osama: called once; only when the map is loaded on the screen */
    override fun onMapReady(googleMap: GoogleMap) {
        Companion.googleMap = googleMap
        defaultLocation = LatLng(30.0444, 31.2357)
        showDefaultLocationOnMap(googleMap,defaultLocation)
        Handler().postDelayed({ showExpectedPath(googleMap, pathNodes, Color.BLACK) }, 3000)
    }

}