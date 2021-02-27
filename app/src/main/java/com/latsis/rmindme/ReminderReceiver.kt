package com.latsis.rmindme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //TODO("ReminderReceiver.onReceive() is not implemented")
        val uid = intent?.getIntExtra("uid", 0)
        val text = intent?.getStringExtra("message")

        MainActivity.showNofitication(context!!,text!!)
    }
}
