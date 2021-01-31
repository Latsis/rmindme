package com.latsis.rmindme

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import com.latsis.rmindme.databinding.ActivityLoginScreenBinding
import com.latsis.rmindme.databinding.ActivityMainBinding
import com.latsis.rmindme.db.ReminderInfo
import com.latsis.rmindme.databinding.ReminderBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var listView: ListView
    var arrayList: ArrayList<MyData> = ArrayList()
    var adapter: ReminderListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        title = "Rmind.me"
        listView = findViewById(R.id.listView)
        arrayList.add(MyData("Shopping list", " Buy milk", "2020-01-01"))
        arrayList.add(MyData("Movie night", " Go to movies", "2020-01-02"))
        arrayList.add(MyData("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(MyData("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(MyData("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(MyData("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(MyData("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(MyData("Morning meeting", " Early wakeup", "2020-01-03"))
        arrayList.add(MyData("Morning meeting", " Early wakeup", "2020-01-03"))
        adapter = ReminderListAdapter(this, arrayList)
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
            Toast.makeText(this, "Item One Clicked", Toast.LENGTH_LONG).show()
            return true
        }
        if (id == R.id.action_edit_profile) {
            Toast.makeText(this, "Edit your profile", Toast.LENGTH_LONG).show()
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
                    Intent(applicationContext, LoginScreenActivity::class.java)
            )
            return true
        }

        return super.onOptionsItemSelected(item)

    }
}

//Class ReminderListAdapter
class ReminderListAdapter(private val context: Context, private val arrayList: java.util.ArrayList<MyData>) : BaseAdapter() {
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
//Class MyData
class MyData(var reminderTitle: String, var reminderText: String, var reminderDate: String)

/*
class ReminderTestAdaptor(context: Context, private val list: List<ReminderInfo>) : BaseAdapter() {

    private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View? {
        var rowBinding = ReminderBinding.inflate(inflater, container, false)
        //set reminder info values to the list item
        rowBinding.txtReminderUsername.text = list[position].username
        rowBinding.txtReminderTitle.text = list[position].title
        rowBinding.txtReminderDescription.text = list[position].description
        rowBinding.txtReminderDate.text = list[position].date

        return rowBinding.root
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}*/