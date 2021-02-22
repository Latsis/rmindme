package com.latsis.rmindme

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.AsyncTask
import android.widget.*
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityMainBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        title = "Rmind.me"
        listView = binding.listView

        // Add placeholder items to reminder table
        //createReminderPlaceholders()

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
                    // Update UI

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
                    }

                    //refresh payments list
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
            val reminderInfos = db.reminderDao().getUserReminderInfos(getUserId())
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
                    Toast.makeText(applicationContext, "No reminders to show", Toast.LENGTH_SHORT).show()
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
            //Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(applicationContext, ReminderItemActivity::class.java)
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


    private fun createReminderPlaceholders() {
        val reminderInfo1 = ReminderInfo(
            null,
            title = "Test Reminder 1",
            message = "This is a placeholder for a reminder",
            location_x = "X coordinate for location",
            location_y = "Y coordinate for location",
            reminder_time = "2021-02-02T02:02",
            creation_time = "2021-01-01T12:00:00.001",
            creator_id = "testuser",
            reminder_seen = "0"
        )

        val reminderInfo2 = ReminderInfo(
                null,
                title = "Hetki lyö",
                message = "Viime hetki lyö",
                location_x = "X coordinate for location",
                location_y = "Y coordinate for location",
                reminder_time = "2021-02-02T02:02",
                creation_time = "2021-01-01T12:00:00.001",
                creator_id = "testuser",
                reminder_seen = "0"
        )

        val reminderInfo3 = ReminderInfo(
                null,
                title = "Tää yö",
                message = "Ollaan vaan ja hengaillaan",
                location_x = "X coordinate for location",
                location_y = "Y coordinate for location",
                reminder_time = "2021-02-02T22:45",
                creation_time = "2021-01-01T02:00:00.001",
                creator_id = "testuser",
                reminder_seen = "0"
        )

        val reminderInfo4 = ReminderInfo(
                null,
                title = "admin only",
                message = "Administrator's reminder",
                location_x = "somewhere",
                location_y = "here or there",
                reminder_time = "2021-02-02T22:45",
                creation_time = "2021-01-01T02:00:00.001",
                creator_id = "admin",
                reminder_seen = "0"
        )

        AsyncTask.execute{
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                getString(R.string.dbFileName)
            ).build()
            db.reminderDao().insert(reminderInfo1).toInt()
            db.reminderDao().insert(reminderInfo2).toInt()
            db.reminderDao().insert(reminderInfo3).toInt()
            db.reminderDao().insert(reminderInfo4).toInt()
            db.close()
        }
    }
}