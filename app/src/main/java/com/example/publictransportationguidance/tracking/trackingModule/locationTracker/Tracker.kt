package com.example.publictransportationguidance.tracking.trackingModule.locationTracker
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.publictransportationguidance.tracking.TrackLiveLocation
import com.example.publictransportationguidance.tracking.trackingModule.backGroundCurrentLocationTracker.LocationBroadcastReceiver
import com.example.publictransportationguidance.tracking.trackingModule.util.logic.MapUtils
import com.example.publictransportationguidance.tracking.trackingModule.util.logic.PathUtils
import com.example.publictransportationguidance.tracking.trackingModule.util.ui.CameraUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class Tracker {

    companion object {

        const val REQUEST_LOCATION_PERMISSION = 1                             // M Osama: constant for the permission req. code

        /* M Osama: returns lastTrackedLocation incase User granted permissions*/
        @SuppressLint("MissingPermission")
        fun getCurrentLocation(fusedLocationClient: FusedLocationProviderClient, context: Context, googleMap: GoogleMap) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    val currentLatLng = LatLng(location!!.latitude,location.longitude)
                    TrackLiveLocation.listOfActualPathNodes.add(currentLatLng)
//                    MainActivity.listOfActualPathNodes.addAll(MainActivity.listOfActualPathNodes)
                    MapUtils.moveCar(googleMap,context, currentLatLng)
                    CameraUtils.moveCamera(googleMap,currentLatLng)
                    PathUtils.showActualPath(googleMap, TrackLiveLocation.listOfActualPathNodes, Color.BLACK)
                    location.let { Toast.makeText(context, "Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_SHORT).show() }
                }
            }
        }

        /* M Osama: track LocationUpdates event if the app isn't running*/
        private fun getLocationBroadCastReceiverIntent(context: Context) = LocationBroadcastReceiver.getPendingIntent(context)

        /* M Osama: locationRequest parameters */
        private fun getLocationRequest() = LocationRequest.create().apply {
            interval = 5000                                     // M Osama: LocationUpdates interval = 5 seconds
            fastestInterval = 3000                              // M Osama: fastest LocationUpdates interval = 3 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY   // M Osama: LocationUpdates of high Accuracy
            smallestDisplacement = 1f                           // M Osama: Min triggered displacement is 1 meter
        }

        /* M Osama: requestLocationUpdate every 10sec by most */
        @SuppressLint("MissingPermission")
        fun requestLocationUpdates(fusedLocationClient:FusedLocationProviderClient,context: Context,handler: android.os.Handler) {
            fusedLocationClient.requestLocationUpdates(getLocationRequest(), getLocationBroadCastReceiverIntent(context))

            /* M Osama: Every 10 seconds this object this object calls getCurrentLocation() ;*/
            val locationRunnable = object : Runnable {
                override fun run() {
                    getCurrentLocation(fusedLocationClient,context,TrackLiveLocation.googleMap)
                    handler.postDelayed(this, 10000)
                }
            }

            handler.postDelayed(locationRunnable, 10000)    /* M Osama: start the timer */
        }

        /* M Osama: checkPermissions for every the app was destroyed & started again to run*/
        fun checkPermissions(fusedLocationClient: FusedLocationProviderClient, context: Context, handler: android.os.Handler, activity: Activity) {
                /* If permissions isn't granted; ask the user for permissions
               If permissions is already granted; start requesting location updates */
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)  }
            else { requestLocationUpdates(fusedLocationClient, context, handler) }
        }



    }





}