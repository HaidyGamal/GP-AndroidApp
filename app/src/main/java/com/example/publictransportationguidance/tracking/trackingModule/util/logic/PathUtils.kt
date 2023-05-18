package com.example.publictransportationguidance.tracking.trackingModule.util.logic

import android.content.Context
import android.graphics.Color
import com.example.publictransportationguidance.tracking.trackingModule.util.ui.AnimationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class PathUtils {

    companion object {

        private var originMarker: Marker? = null
        private var destinationMarker: Marker? = null
        private var grayPolyline: Polyline? = null
        private var blackPolyline: Polyline? = null
        private var greenPolyline: Polyline? = null
        private var bluePolyline: Polyline? = null

        /* M Osama: add Car on Map */
        fun addCarMarkerAndGet(googleMap: GoogleMap, latLng: LatLng, context: Context): Marker? {
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(context))
            return googleMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
        }

        /* M Osama: used to add the squared vertex at location & destination */
        private fun addOriginDestinationMarkerAndGet(googleMap: GoogleMap, latLng: LatLng): Marker? {
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
            return googleMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
        }

        /* M Osama: function used to draw the path on Map */
        fun showExpectedPath(googleMap: GoogleMap, latLngList: ArrayList<LatLng>, color:Int) {
            val builder = LatLngBounds.Builder()
            for (latLng in latLngList) { builder.include(latLng) }

            /* M Osama: adjust camera bounds */
            val bounds = builder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

            /* M Osama: draw the lineBackGround */
            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.GRAY)
            polylineOptions.width(5f)
            polylineOptions.addAll(latLngList)
            grayPolyline = googleMap.addPolyline(polylineOptions)

            /* M Osama: draw the lineForeGround */
            val blackPolylineOptions = PolylineOptions()
            blackPolylineOptions.color(color)
            blackPolylineOptions.width(5f)
            blackPolyline = googleMap.addPolyline(blackPolylineOptions)

            /* M Osama: draw location/destination vertices */
            originMarker = addOriginDestinationMarkerAndGet(googleMap,latLngList[0])
            originMarker?.setAnchor(0.5f, 0.5f)
            destinationMarker = addOriginDestinationMarkerAndGet(googleMap,latLngList[latLngList.size - 1])
            destinationMarker?.setAnchor(0.5f, 0.5f)

            /* M Osama: animation for drawing blackLine over greyLine */
            val polylineAnimator = AnimationUtils.polylineAnimator()
            polylineAnimator.addUpdateListener { valueAnimator ->
                val percentValue = (valueAnimator.animatedValue as Int)
                val index = (grayPolyline?.points!!.size) * (percentValue / 100.0f).toInt()
                blackPolyline?.points = grayPolyline?.points!!.subList(0, index)
            }
            polylineAnimator.start()
        }

        /* M Osama: function used to draw the path on Map */
//        fun showActualPath(googleMap: GoogleMap, latLngList: ArrayList<LatLng>, color:Int) {
//            val builder = LatLngBounds.Builder()
//            for (latLng in latLngList) { builder.include(latLng) }
//
//            /* M Osama: adjust camera bounds */
//            val bounds = builder.build()
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))
//
//            /* M Osama: draw the lineBackGround */
//            val polylineOptions = PolylineOptions()
//            polylineOptions.color(Color.BLUE)
//            polylineOptions.width(5f)
//            polylineOptions.addAll(latLngList)
//            bluePolyline = googleMap.addPolyline(polylineOptions)
//
//            /* M Osama: draw the lineForeGround */
//            val blackPolylineOptions = PolylineOptions()
//            blackPolylineOptions.color(color)
//            blackPolylineOptions.width(5f)
//            greenPolyline = googleMap.addPolyline(blackPolylineOptions)
//
//            /* M Osama: animation for drawing blackLine over greyLine */
//            val polylineAnimator = AnimationUtils.polylineAnimator()
//            polylineAnimator.addUpdateListener { valueAnimator ->
//                val percentValue = (valueAnimator.animatedValue as Int)
//                val index = (bluePolyline?.points!!.size) * (percentValue / 100.0f).toInt()
//                greenPolyline?.points = bluePolyline?.points!!.subList(0, index)
//            }
//            polylineAnimator.start()
//        }

        /* M Osama: function used to draw the path on Map */
        @JvmStatic
        fun showActualPath(googleMap: GoogleMap, latLngList: ArrayList<LatLng>, color: Int) {
            val builder = LatLngBounds.Builder()
            for (latLng in latLngList) { builder.include(latLng) }

            /* M Osama: adjust camera bounds */
            val bounds = builder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

            /* M Osama: draw the lineBackGround */
            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.BLUE)
            polylineOptions.width(5f)
            polylineOptions.addAll(latLngList)
            bluePolyline = googleMap.addPolyline(polylineOptions)

            /* M Osama: draw the lineForeGround */
            val blackPolylineOptions = PolylineOptions()
            blackPolylineOptions.color(color)
            blackPolylineOptions.width(5f)
            greenPolyline = googleMap.addPolyline(blackPolylineOptions)

            /* M Osama: animation for drawing blackLine over greyLine */
            val polylineAnimator = AnimationUtils.polylineAnimator()
            polylineAnimator.addUpdateListener { valueAnimator ->
                val percentValue = (valueAnimator.animatedValue as Int)
                val index = (bluePolyline?.points!!.size) * (percentValue / 100.0f).toInt()
                greenPolyline?.points = bluePolyline?.points!!.subList(0, index)
            }
            polylineAnimator.start()
        }


    }
}