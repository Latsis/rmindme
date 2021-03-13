package com.latsis.rmindme

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import com.latsis.rmindme.databinding.ActivityMainBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo
import java.util.concurrent.TimeUnit
import kotlin.random.Random


const val GEOFENCE_RADIUS = 500
const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 days
const val GEOFENCE_DWELL_DELAY =  10 * 1000 // 10 secs // 2 minutes
const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 13f
const val LOCATION_REQUEST_CODE = 123
const val REQUEST_LOCATION_PERMISSION = 456
const val REQUEST_VIRTUAL_LOCATION = 11111

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listView: ListView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        title = "Rmind.me"
        listView = binding.listView

        val bundle :Bundle ?=intent.extras
        if (bundle?.getInt("notified_reminder_id")!= null) {
            //TODO use this if necessary to detect whether activity started from
            // application or notification
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
        }

        fusedLocationClient.setMockMode(true)

        refreshListView()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->

            val selectedReminderInfo = listView.adapter.getItem(position) as ReminderInfo
            val message = selectedReminderInfo.uid
            startActivity(
                Intent(applicationContext, ReminderItemActivity::class.java).putExtra("selected_reminder",message)
            )
            refreshListView()
        }

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, id ->

            val selectedReminderInfo = listView.adapter.getItem(position) as ReminderInfo
            val message = "Do you want to delete entry for ${selectedReminderInfo.title}?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete reminder?")
                .setMessage(message)
                .setPositiveButton("Delete") { _, _ ->

                    //delete from database
                    AsyncTask.execute {
                        val db = Room
                            .databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                getString(R.string.dbFileName)
                            )
                            .build()
                        db.reminderDao().delete(selectedReminderInfo.uid!!)
                        //db.reminderDao().updateReminderSeen("1", selectedReminderInfo.uid!!)
                    }
                    cancelReminder(applicationContext, selectedReminderInfo.uid!!)
                    Log.d("Test", "removing reminder")

                    //refresh reminder list
                    refreshListView()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Do nothing
                    dialog.dismiss()
                }
                .show()
            true

        }
    }


    fun getUserId(): String {
        //TODO: change this to logged in user's id check
        //but since username is unique, can use that as well for now
        val prefs = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE)
        return prefs.getString("username", null).toString()
    }

    override fun onResume() {
        super.onResume()
        refreshListView()
    }

    private fun refreshListView() {
        var refreshTask = LoadReminderInfoEntries()
        refreshTask.execute()

    }

    @SuppressLint("StaticFieldLeak")
    inner class LoadReminderInfoEntries : AsyncTask<String?, String?, List<ReminderInfo>>() {
        override fun doInBackground(vararg params: String?): List<ReminderInfo> {
            val db = Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                )
                .build()
            //val reminderInfos = db.reminderDao().getReminderInfos()
            //val reminderInfos = db.reminderDao().getUserReminderInfos(getUserId())
            val reminderInfos = db.reminderDao().getPreviousUserReminderInfos(getUserId())
            db.close()
            return reminderInfos
        }

        override fun onPostExecute(reminderInfos: List<ReminderInfo>?) {
            super.onPostExecute(reminderInfos)
            if (reminderInfos != null) {
                if (reminderInfos.isNotEmpty()) {
                    val adaptor = ReminderListAdapter(applicationContext, reminderInfos)
                    listView.adapter = adaptor
                } else {
                    listView.adapter = null
                    Toast.makeText(applicationContext, "No previous reminders to show", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.action_add_reminder) {
            startActivity(
                Intent(applicationContext, ReminderItemActivity::class.java)
            )
            return true
        }
        if (id == R.id.action_show_all_reminders) {
            startActivity(
                Intent(applicationContext, ReminderListActivity::class.java)
            )
            return true
        }
        if (id == R.id.action_edit_profile) {
            startActivity(
                Intent(applicationContext, ProfileScreenActivity::class.java)
            )
            return true
        }
        if (id == R.id.action_sign_out) {
            Toast.makeText(this, "You have signed out", Toast.LENGTH_SHORT).show()
            applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
            ).edit().putInt("LoginStatus", 0).apply()
            startActivity(
                Intent(applicationContext, LoginScreenActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    companion object {

        fun removeGeofences(context: Context, triggeringGeofenceList: MutableList<Geofence>) {
            val geofenceIdList = mutableListOf<String>()
            for (entry in triggeringGeofenceList) {
                geofenceIdList.add(entry.requestId)
            }
            LocationServices.getGeofencingClient(context).removeGeofences(geofenceIdList)
        }


        fun showNotification(context: Context, message: String, reminderid: Int) {

            val rmindmeNotificationChannel = "RMINDME_NOTIFICATION_CHANNEL"
            val notificationId = Random.nextInt(10, 1000) + 5

            val notificationIntent = Intent(context, LoginScreenActivity::class.java)
            notificationIntent.putExtra("notified_reminder_id", reminderid)
            val notificationPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(notificationIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            val snoozeIntent = Intent(context, ReminderReceiver::class.java)
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0)
            snoozeIntent.putExtra("message", message)
            snoozeIntent.putExtra("uid", reminderid)

            val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminderid,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationBuilder = NotificationCompat.Builder(context, rmindmeNotificationChannel)
                    .setSmallIcon(R.drawable.ic_baseline_event_note_24)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setGroup(rmindmeNotificationChannel)
                    .setContentIntent(notificationPendingIntent)
                    .setAutoCancel(true) //remove notification when tapped
                    .addAction(R.drawable.ic_baseline_event_note_24, "Remind me in 5 minutes", snoozePendingIntent)

            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                    rmindmeNotificationChannel,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.app_name)
            }
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(notificationId, notificationBuilder.build())

        }

        fun setReminder(
            context: Context,
            uid: Int,
            timeInMillis: Long,
            message: String
        ) {

            val reminderParameters = Data.Builder()
                .putString("message", message)
                .putInt("uid", uid)
                .build()

            // get minutes from now until reminder
            var minutesFromNow = 0L
            if (timeInMillis > System.currentTimeMillis())
                minutesFromNow = timeInMillis - System.currentTimeMillis()

            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(reminderParameters)
                .setInitialDelay(minutesFromNow, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(uid.toString(), ExistingWorkPolicy.REPLACE, reminderRequest)
        }

        fun cancelReminder(context: Context, uid: Int) {
            WorkManager.getInstance(context).cancelUniqueWork(uid.toString());
        }
    }
}