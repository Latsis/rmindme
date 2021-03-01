package com.latsis.rmindme

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityReminderListBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo

class ReminderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderListBinding
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = "Upcoming reminders"
        listView = binding.allReminderListView

        refreshListView()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->

            val selectedReminderInfo = listView.adapter.getItem(position) as ReminderInfo
            val message = selectedReminderInfo.uid
            startActivity(
                Intent(
                    applicationContext,
                    ReminderItemActivity::class.java
                ).putExtra("selected_reminder", message)
            )
            refreshListView()
        }

        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, id ->

            val selectedReminderInfo = listView.adapter.getItem(position) as ReminderInfo
            val message = "Do you want to remove notification for ${selectedReminderInfo.title}?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@ReminderListActivity)
            builder.setTitle("Silence reminder?")
                .setMessage(message)
                .setPositiveButton("Silence") { _, _ ->

                    //delete from database
                    AsyncTask.execute {
                        val db = Room
                            .databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                getString(R.string.dbFileName)
                            )
                            .build()
                        //db.reminderDao().delete(selectedReminderInfo.uid!!)
                        db.reminderDao().updateReminderSeen("1", selectedReminderInfo.uid!!)
                    }
                    MainActivity.cancelReminder(applicationContext, selectedReminderInfo.uid!!)
                    Log.d("Test", "silencing notification for reminder")

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
            getString(R.string.sharedPreference), Context.MODE_PRIVATE
        )
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
            val reminderInfos = db.reminderDao().getUpcomingUserReminderInfos(getUserId())
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
                    Toast.makeText(
                        applicationContext,
                        "No upcoming reminders to show",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
