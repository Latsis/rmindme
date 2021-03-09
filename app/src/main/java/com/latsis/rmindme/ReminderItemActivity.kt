package com.latsis.rmindme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityReminderItemBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
                binding.editTextReminderTitle.setText(reminderInfo.title, TextView.BufferType.EDITABLE)
                binding.editTextReminderText.setText(reminderInfo.message, TextView.BufferType.EDITABLE)
                binding.editTextReminderTime.setText(dateAndTime[1])
                binding.editTextReminderDate.setText(dateAndTime[0])
                binding.editTextReminderLocationX.setText(reminderInfo.location_x, TextView.BufferType.EDITABLE)
                binding.editTextReminderLocationY.setText(reminderInfo.location_y, TextView.BufferType.EDITABLE)
            }
        }


        //val pickDateEditText = findViewById<EditText>(R.id.editTextReminderDate)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.editTextReminderDate.setOnClickListener {
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                binding.editTextReminderDate.setText("" + addZeroDigit(year) + "-" + addZeroDigit(month + 1) + "-" + addZeroDigit(day))
            }, day, month, year)
            datePicker.updateDate(year, month, day);
            datePicker.show()
        }

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        binding.editTextReminderTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                binding.editTextReminderTime.setText(""+ addZeroDigit(hour) + ":" + addZeroDigit(minute))
            }, hour, minute, true)
            timePicker.show()
        }


        binding.editTextReminderLocationX.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", binding.editTextReminderLocationX.text.toString())
            intent.putExtra("longitude", binding.editTextReminderLocationY.text.toString())
            startActivityForResult(intent, 1)
        }

        binding.editTextReminderLocationY.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", binding.editTextReminderLocationX.text.toString())
            intent.putExtra("longitude", binding.editTextReminderLocationY.text.toString())
            startActivityForResult(intent, 1)
        }

        binding.saveReminderButton.setOnClickListener {

            if (binding.editTextReminderTitle.text.isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "You must add a title for your reminder!",
                        Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (binding.editTextReminderDate.text.isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "You must add date and time for your reminder!",
                        Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (binding.editTextReminderTime.text.isEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "You must add date and time for your reminder!",
                        Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val reminderInfoEdit = ReminderInfo(
                    uid = bundle?.getInt("selected_reminder"),
                    title = binding.editTextReminderTitle.text.toString(),
                    message = binding.editTextReminderText.text.toString(),
                    location_x = binding.editTextReminderLocationX.text.toString(),
                    location_y = binding.editTextReminderLocationY.text.toString(),
                    reminder_time = binding.editTextReminderDate.text.toString() + "T" + binding.editTextReminderTime.text.toString(),
                    creator_id = loggedInUserId,
                    creation_time = LocalDateTime.now().toString(),
                    reminder_seen = "1"
            )

            val reminderCalendar=GregorianCalendar.getInstance()
            val setReminderDate = reminderInfoEdit.reminder_time.split("T").toTypedArray()[0].split("-").toTypedArray()
            val setReminderTime = reminderInfoEdit.reminder_time.split("T").toTypedArray()[1].split(":").toTypedArray()

            reminderCalendar.set(Calendar.YEAR,setReminderDate[0].toInt())
            reminderCalendar.set(Calendar.MONTH,setReminderDate[1].toInt()-1)
            reminderCalendar.set(Calendar.DAY_OF_MONTH,setReminderDate[2].toInt())
            reminderCalendar.set(Calendar.HOUR_OF_DAY, setReminderTime[0].toInt())
            reminderCalendar.set(Calendar.MINUTE, setReminderTime[1].toInt())
            reminderCalendar.set(Calendar.SECOND, 0)


            if(bundle!=null) {
                AsyncTask.execute{
                    val db = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    ).build()
                    db.reminderDao().updateReminderInfo(reminderInfoEdit)
                    val uuid = bundle.getInt("selected_reminder")
                    //db.close()
                    Log.d("TEST", uuid.toString())

                    if (reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis) {
                        db.reminderDao().updateReminderSeen("0", uuid)
                        // if new time is set in the future, set (replace) reminder
                        val message =
                            "Hey ${reminderInfoEdit.creator_id}, don't forget to ${reminderInfoEdit.title} at ${reminderInfoEdit.reminder_time}"
                        MainActivity.setReminder(
                            applicationContext,
                            uuid,
                            reminderCalendar.timeInMillis,
                            message
                        )
                    }
                }
            } else {
                AsyncTask.execute{
                    val db = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    ).build()
                    val uuid = db.reminderDao().insert(reminderInfoEdit).toInt()
                    //db.close()
                    Log.d("TEST", uuid.toString())

                    if (reminderCalendar.timeInMillis > Calendar.getInstance().timeInMillis) {
                        db.reminderDao().updateReminderSeen("0", uuid)
                        // if reminder time is set in the future, set reminder
                        Log.d("Test: reminder_time in millis", reminderCalendar.timeInMillis.toString())
                        Log.d("Test", "setting reminder")
                        val message =
                                "Hey ${reminderInfoEdit.creator_id}, don't forget to ${reminderInfoEdit.title} at ${reminderInfoEdit.reminder_time}"
                        MainActivity.setReminder(
                                applicationContext,
                                uuid,
                                reminderCalendar.timeInMillis,
                                message
                        )
                    }
                }

                if(reminderCalendar.timeInMillis>Calendar.getInstance().timeInMillis){
                    Toast.makeText(
                            applicationContext,
                            "Reminder saved!",
                            Toast.LENGTH_SHORT
                    ).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If you have multiple activities returning results then you should include unique request codes for each
        if (requestCode == 1) {

            // The result code from the activity started using startActivityForResults
            if (resultCode == RESULT_OK) {
                Log.d("MapActivity", "RESULT_OK")
                val locationXCoordinate = data!!.getStringExtra("latitude")
                val locationYCoordinate = data!!.getStringExtra("longitude")
                binding.editTextReminderLocationX.setText(locationXCoordinate)
                binding.editTextReminderLocationY.setText(locationYCoordinate)

            }
        }
    }

}