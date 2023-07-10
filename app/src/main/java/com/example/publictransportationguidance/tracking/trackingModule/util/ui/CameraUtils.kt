package com.example.publictransportationguidance.tracking.trackingModule.util.ui

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class CameraUtils {

    companion object {

        /* M Osama: animate Camera transversing between location to another */
        fun animateCamera(googleMap: GoogleMap, latLng: LatLng) {
            val cameraPosition = CameraPosition.Builder().target(latLng).zoom(20.5f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        /* M Osama: moves the camera to the default location */
        fun showDefaultLocationOnMap(googleMap: GoogleMap,latLng: LatLng) {
            moveCamera(googleMap,latLng)
            animateCamera(googleMap,latLng)
        }

        /* M Osama: moves googleMaps Camera to passed LatLng */
        @JvmStatic
        fun moveCamera(googleMap: GoogleMap, currentLocation: LatLng) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        }

    }

}