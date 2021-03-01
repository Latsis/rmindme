package com.latsis.rmindme

import android.content.Context
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.latsis.rmindme.db.AppDatabase

class ReminderWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext,workerParameters) {

    override fun doWork(): Result {
        val text = inputData.getString("message") // this comes from the reminder parameters
        val uid = inputData.getInt("uid", 0)
        MainActivity.showNotification(applicationContext,text!!, uid)
        val db = Room
                .databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "com.latsis.rmindme"
                )
                .build()
        db.reminderDao().updateReminderSeen("1", uid)
        return   Result.success()
    }
}