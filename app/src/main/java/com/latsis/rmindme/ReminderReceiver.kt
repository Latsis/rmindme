package com.latsis.rmindme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import androidx.room.Room
import com.latsis.rmindme.db.AppDatabase
import java.util.*

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val uid = intent.getIntExtra("uid", 0)
        var text = intent.getStringExtra("message")
        if(text == null){
            text = "DEFAULT STRING"
        }
        Log.d("Test", "Snooze received")
        MainActivity.setReminder(
            context,
            uid,
            Calendar.getInstance().timeInMillis + 300000, //alert again in 5 minutes
            //Calendar.getInstance().timeInMillis + 2000, //alert again in 2 seconds
            text
        )
    }
}