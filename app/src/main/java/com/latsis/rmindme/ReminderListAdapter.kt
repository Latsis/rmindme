package com.latsis.rmindme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.latsis.rmindme.databinding.ReminderBinding
import com.latsis.rmindme.db.ReminderInfo

class ReminderListAdapter(context: Context, private val list: List<ReminderInfo>) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View? {
        var rowBinding = ReminderBinding.inflate(inflater, container, false)
        //set reminder info values to the list item
        rowBinding.reminderTitle.text = list[position].title
        rowBinding.reminderText.text = list[position].description
        rowBinding.reminderDate.text = list[position].date

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

}