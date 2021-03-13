package com.latsis.rmindme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.latsis.rmindme.db.AppDatabase
import java.util.*

class GeofencingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Geofence", "GEOFENCE TRIGGERED")
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val geofencingTransition = geofencingEvent.geofenceTransition

        if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            // Retrieve data from intent
            val uid = intent.getIntExtra("uid", 0)
            var text = intent.getStringExtra("message")
            if (text == null) {
                text = "DEFAULT STRING"
            }

            Log.d("Geofence", "GEOFENCE TRIGGERED")
            MainActivity.showNotification(context, text, uid)
            // remove geofence
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            MainActivity.removeGeofences(context, triggeringGeofences)
        }
    }
}