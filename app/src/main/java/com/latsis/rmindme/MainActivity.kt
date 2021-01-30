package com.latsis.rmindme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    lateinit var listView: ListView
    var arrayList: ArrayList<MyData> = ArrayList()
    var adapter: ReminderListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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