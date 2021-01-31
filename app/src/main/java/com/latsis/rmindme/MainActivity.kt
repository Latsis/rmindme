package com.latsis.rmindme

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.AsyncTask
import android.provider.ContactsContract
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityLoginScreenBinding
import com.latsis.rmindme.databinding.ActivityMainBinding
import com.latsis.rmindme.databinding.ReminderBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo
import kotlinx.coroutines.selects.select

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

        createReminderPlaceholders()
        refreshListView()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
            //retrieve selected Item

            val selectedReminderInfo = listView.adapter.getItem(position) as ReminderInfo
            val message =
                "Do you want to delete ${selectedReminderInfo.title} reminder, on ${selectedReminderInfo.date}?"

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

        }
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
            val reminderInfos = db.reminderDao().getReminderInfos()
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
                    Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    companion object {
        //val paymenthistoryList = mutableListOf<PaymentInfo>()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.action_add_reminder) {
            Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show()
            return true
        }
        if (id == R.id.action_edit_profile) {
            startActivity(
                Intent(applicationContext, ProfileScreenActivity::class.java)
            )
            return true
        }
        if (id == R.id.action_sign_out) {
            Toast.makeText(this, "You have signed out", Toast.LENGTH_LONG).show()
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
            username = "testuser",
            title = "Test Reminder 1",
            description = "This is a placeholder for a reminder",
            date = "2021-02-01 Monday 10:00 UTC+2"
        )

        val reminderInfo2 = ReminderInfo(
            null,
            username = "testuser",
            title = "Test Reminder 2",
            description = "This is 2nd placeholder for a reminder",
            date = "2021-02-01 Monday 10:00 UTC+2"
        )

        val reminderInfo3 = ReminderInfo(
            null,
            username = "testuser",
            title = "Test Reminder 3",
            description = "This is 3rd placeholder for a reminder",
            date = "2021-02-01 Monday 10:00 UTC+2"
        )

        AsyncTask.execute{
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                getString(R.string.dbFileName)
            ).build()
            val uuid1 = db.reminderDao().insert(reminderInfo1).toInt()
            val uuid2 = db.reminderDao().insert(reminderInfo2).toInt()
            val uuid3 = db.reminderDao().insert(reminderInfo3).toInt()
            db.close()
        }
    }
}


/*class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listView: ListView
    var arrayList: ArrayList<ReminderPlaceHolders> = ArrayList()
    var adapter: ReminderListAdaptor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        title = "Rmind.me"
        listView = findViewById(R.id.listView)
        arrayList.add(ReminderPlaceHolders("Shopping list", " Buy milk", "2020-01-01"))
        arrayList.add(ReminderPlaceHolders("Movie night", " Go to movies", "2020-01-02"))
        arrayList.add(ReminderPlaceHolders("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(ReminderPlaceHolders("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(ReminderPlaceHolders("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(ReminderPlaceHolders("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(ReminderPlaceHolders("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(ReminderPlaceHolders("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(ReminderPlaceHolders("Morning meeting", " Early wakeup", "2020-01-03"))
        adapter = ReminderListAdaptor(this, arrayList)
        listView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.action_add_reminder) {
            Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show()
            return true
        }
        if (id == R.id.action_edit_profile) {
            startActivity(
                Intent(applicationContext, ProfileScreenActivity::class.java)
            )
            return true
        }
        if (id == R.id.action_sign_out) {
            Toast.makeText(this, "You have signed out", Toast.LENGTH_LONG).show()
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
}*/

//Class ReminderListAdaptor
class ReminderListAdaptor(private val context: Context, private val arrayList: java.util.ArrayList<ReminderPlaceHolders>) : BaseAdapter() {
    private lateinit var reminderTitle: TextView
    private lateinit var reminderText: TextView
    private lateinit var reminderDate: TextView
    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.reminder, parent, false)
        reminderTitle = convertView.findViewById(R.id.reminderTitle)
        reminderText = convertView.findViewById(R.id.reminderText)
        reminderDate = convertView.findViewById(R.id.reminderDate)
        reminderTitle.text = " " + arrayList[position].reminderTitle
        reminderText.text = arrayList[position].reminderText
        reminderDate.text = arrayList[position].reminderDate
        return convertView
    }
}
//Class ReminderPlaceHolders
class ReminderPlaceHolders(var reminderTitle: String, var reminderText: String, var reminderDate: String)