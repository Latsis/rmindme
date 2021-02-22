package com.latsis.rmindme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityReminderItemBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo
import java.time.LocalDateTime
import java.util.*

class ReminderItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReminderItemBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        title = "Reminder"
        val bundle :Bundle ?=intent.extras

        //TODO: change this to logged in user's id check
        val prefs = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE)
        val loggedInUserId = prefs.getString("username",null).toString()

        val selectedReminderTitleEditText: EditText = findViewById<EditText>(R.id.editTextReminderTitle)
        val selectedReminderMessageEditText: EditText = findViewById<EditText>(R.id.editTextReminderText)
        val selectedReminderTimeEditText: EditText = findViewById<EditText>(R.id.editTextReminderTime)
        val selectedReminderDateEditText: EditText = findViewById<EditText>(R.id.editTextReminderDate)
        val selectedReminderLocationXEditText: EditText = findViewById<EditText>(R.id.editTextReminderLocationX)
        val selectedReminderLocationYEditText: EditText = findViewById<EditText>(R.id.editTextReminderLocationY)


        //if previous activity sent reminder id, fill input fields with existing information
        if(bundle!=null)
        {
            val selectedReminderId = bundle.getInt("selected_reminder")

            AsyncTask.execute {
                val db = Room
                    .databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    )
                    .build()
                val reminderInfo = db.reminderDao().getReminderInfo(selectedReminderId)
                db.close()
                Log.d("Test", reminderInfo.message)

                val dateAndTime = reminderInfo.reminder_time.split("T").toTypedArray()
                selectedReminderTitleEditText.setText(reminderInfo.title, TextView.BufferType.EDITABLE)
                selectedReminderMessageEditText.setText(reminderInfo.message, TextView.BufferType.EDITABLE)
                selectedReminderTimeEditText.setText(dateAndTime[1])
                selectedReminderDateEditText.setText(dateAndTime[0])
                selectedReminderLocationXEditText.setText(reminderInfo.location_x, TextView.BufferType.EDITABLE)
                selectedReminderLocationYEditText.setText(reminderInfo.location_y, TextView.BufferType.EDITABLE)
            }
        }


        //val pickDateEditText = findViewById<EditText>(R.id.editTextReminderDate)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        selectedReminderDateEditText.setOnClickListener {
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                selectedReminderDateEditText.setText("" + addZeroDigit(year) + "-" + addZeroDigit(month + 1) + "-" + addZeroDigit(day))
            }, day, month, year)
            datePicker.updateDate(2021, 1 - 1, 1);
            datePicker.show()
        }

        //val pickTimeEditText = findViewById<EditText>(R.id.editTextReminderTime)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        selectedReminderTimeEditText.setOnClickListener {
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                selectedReminderTimeEditText.setText(""+ addZeroDigit(hour) + ":" + addZeroDigit(minute))
            }, hour, minute, true)
            timePicker.show()
        }


        binding.saveReminderButton.setOnClickListener {

            val reminderInfoEdit = ReminderInfo(
                    uid = bundle?.getInt("selected_reminder"),
                    title = selectedReminderTitleEditText.text.toString(),
                    message = selectedReminderMessageEditText.text.toString(),
                    location_x = selectedReminderLocationXEditText.text.toString(),
                    location_y = selectedReminderLocationYEditText.text.toString(),
                    reminder_time = selectedReminderDateEditText.text.toString() + "T" + selectedReminderTimeEditText.text.toString(),
                    creator_id = loggedInUserId,
                    creation_time = LocalDateTime.now().toString(),
                    reminder_seen = ""
            )

            if(bundle!=null) {
                AsyncTask.execute{
                    val db = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    ).build()
                    db.reminderDao().updateReminderInfo(reminderInfoEdit)
                    //db.close()
                }
            } else {
                AsyncTask.execute{
                    val db = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    ).build()
                    db.reminderDao().insert(reminderInfoEdit).toInt()
                    //db.close()
                }
            }

            finish()
        }

        binding.cancelReminderButton.setOnClickListener {
            Log.d("Test", "Cancel Button Clicked")
            finish()
        }
    }

    private fun addZeroDigit(number: Int): String {
        return when (number <= 9) {
            true -> "0${number}"
            false -> number.toString()
        }
    }

}