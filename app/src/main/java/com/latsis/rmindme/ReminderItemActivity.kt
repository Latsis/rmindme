package com.latsis.rmindme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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

                val selectedReminderTitleEditText: EditText = findViewById<EditText>(R.id.editTextReminderTitle)
                selectedReminderTitleEditText.setText(reminderInfo.title, TextView.BufferType.EDITABLE)

                val selectedReminderMessageEditText: EditText = findViewById<EditText>(R.id.editTextReminderText)
                selectedReminderMessageEditText.setText(reminderInfo.message, TextView.BufferType.EDITABLE)

                val selectedReminderTimeEditText: EditText = findViewById<EditText>(R.id.editTextReminderTime)
                selectedReminderTimeEditText.setText(dateAndTime[1])

                val selectedReminderDateEditText: EditText = findViewById<EditText>(R.id.editTextReminderDate)
                selectedReminderDateEditText.setText(dateAndTime[0])

                val selectedReminderLocationXEditText: EditText = findViewById<EditText>(R.id.editTextReminderLocationX)
                selectedReminderLocationXEditText.setText(reminderInfo.location_x, TextView.BufferType.EDITABLE)

                val selectedReminderLocationYEditText: EditText = findViewById<EditText>(R.id.editTextReminderLocationY)
                selectedReminderLocationYEditText.setText(reminderInfo.location_y, TextView.BufferType.EDITABLE)

            }

        } else {
            val selectedReminderId = null
        }


        val pickDateEditText = findViewById<EditText>(R.id.editTextReminderDate)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        pickDateEditText.setOnClickListener {
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                pickDateEditText.setText("" + addZeroDigit(year) + "-" + addZeroDigit(month + 1) + "-" + addZeroDigit(day))
            }, day, month, year)
            datePicker.updateDate(2021, 1 - 1, 1);
            datePicker.show()
        }

        val pickTimeEditText = findViewById<EditText>(R.id.editTextReminderTime)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        pickTimeEditText.setOnClickListener {
            val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                pickTimeEditText.setText(""+ addZeroDigit(hour) + ":" + addZeroDigit(minute))
            }, hour, minute, true)
            tpd.show()
        }


        binding.saveReminderButton.setOnClickListener {

            val reminderInfoEdit = ReminderInfo(
                    uid = bundle?.getInt("selected_reminder"),
                    title = findViewById<EditText>(R.id.editTextReminderTitle).text.toString(),
                    message = findViewById<EditText>(R.id.editTextReminderText).text.toString(),
                    location_x = findViewById<EditText>(R.id.editTextReminderLocationX).text.toString(),
                    location_y = findViewById<EditText>(R.id.editTextReminderLocationY).text.toString(),
                    reminder_time = findViewById<EditText>(R.id.editTextReminderDate).text.toString() + "T" + findViewById<EditText>(R.id.editTextReminderTime).text.toString(),
                    creator_id = "testuser",
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
                    val uuid1 = db.reminderDao().updateReminderInfo(reminderInfoEdit)
                    //db.close()
                }
            } else {
                AsyncTask.execute{
                    val db = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    ).build()
                    val uuid1 = db.reminderDao().insert(reminderInfoEdit).toInt()
                    //db.close()
                }
            }


            //Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show()
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