package com.example.publictransportationguidance.tracking.trackingModule.backGroundCurrentLocationTracker

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult

    class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action == ACTION_PROCESS_UPDATES) {
                LocationResult.extractResult(intent)?.let { locationResult ->
                    // Handle location result
                }
            }
        }

        companion object {
            const val ACTION_PROCESS_UPDATES = "com.example.locationbroadcastreceiver.action" + ".PROCESS_UPDATES"

            fun getPendingIntent(context: Context): PendingIntent {
                val intent = Intent(context, LocationBroadcastReceiver::class.java)
                intent.action = ACTION_PROCESS_UPDATES
                return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }

        }
}