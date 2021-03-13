package com.latsis.rmindme

import android.Manifest
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.latsis.rmindme.databinding.ActivityReminderItemBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ReminderItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderItemBinding
    private lateinit var geofencingClient: GeofencingClient

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

        geofencingClient = LocationServices.getGeofencingClient(this)

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
                binding.editTextReminderTitle.setText(reminderInfo.title,
                        TextView.BufferType.EDITABLE)
                binding.editTextReminderText.setText(reminderInfo.message,
                        TextView.BufferType.EDITABLE)
                binding.editTextReminderTime.setText(dateAndTime[1])
                binding.editTextReminderDate.setText(dateAndTime[0])
                binding.editTextReminderLocationX.setText(
                        reminderInfo.location_x,
                        TextView.BufferType.EDITABLE)
                binding.editTextReminderLocationY.setText(
                        reminderInfo.location_y,
                        TextView.BufferType.EDITABLE)
            }
        }


        //val pickDateEditText = findViewById<EditText>(R.id.editTextReminderDate)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.editTextReminderDate.setOnClickListener {
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                _, year, month, day ->
                binding.editTextReminderDate.setText("" + addZeroDigit(year) + "-"
                        + addZeroDigit(month + 1) + "-" + addZeroDigit(day))
            }, day, month, year)
            datePicker.updateDate(year, month, day);
            datePicker.show()
        }

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        binding.editTextReminderTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener {
                _, hour, minute ->
                binding.editTextReminderTime.setText(""+ addZeroDigit(hour) + ":"
                        + addZeroDigit(minute))
            }, hour, minute, true)
            timePicker.show()
        }


        binding.editTextReminderLocationX.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", binding.editTextReminderLocationX.text.toString())
            intent.putExtra("longitude", binding.editTextReminderLocationY.text.toString())
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }

        binding.editTextReminderLocationY.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latitude", binding.editTextReminderLocationX.text.toString())
            intent.putExtra("longitude", binding.editTextReminderLocationY.text.toString())
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
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

            if (binding.editTextReminderDate.text.isEmpty() && binding.editTextReminderTime.text
                            .isNotEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "You must add both date and time for your reminder!",
                        Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (binding.editTextReminderTime.text.isEmpty() && binding.editTextReminderDate.text
                            .isNotEmpty()) {
                Toast.makeText(
                        applicationContext,
                        "You must add both date and time for your reminder!",
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
                    reminder_time = binding.editTextReminderDate.text.toString().trim()
                            + "T" + binding.editTextReminderTime.text.toString().trim(),
                    creator_id = loggedInUserId,
                    creation_time = LocalDateTime.now().toString(),
                    reminder_seen = "1"
            )

            val reminderCalendar=GregorianCalendar.getInstance()
            val setReminderDate = reminderInfoEdit.reminder_time.split("T")
                    .toTypedArray()[0].split("-").toTypedArray()
            val setReminderTime = reminderInfoEdit.reminder_time.split("T")
                    .toTypedArray()[1].split(":").toTypedArray()

            if (reminderInfoEdit.reminder_time != "T") {
                reminderCalendar.set(Calendar.YEAR, setReminderDate[0].toInt())
                reminderCalendar.set(Calendar.MONTH, setReminderDate[1].toInt() - 1)
                reminderCalendar.set(Calendar.DAY_OF_MONTH, setReminderDate[2].toInt())
                reminderCalendar.set(Calendar.HOUR_OF_DAY, setReminderTime[0].toInt())
                reminderCalendar.set(Calendar.MINUTE, setReminderTime[1].toInt())
                reminderCalendar.set(Calendar.SECOND, 0)
            }


            if(bundle!=null) {
                //If editing existing reminder
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

                    if (reminderInfoEdit.reminder_time != "T") {
                        //If reminder has time
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
                            if (reminderInfoEdit.location_x.isNotEmpty()) {
                                //if the reminder has location, set geofence
                                val latLngCoordinates = LatLng(binding.editTextReminderLocationX.text.toString().toDouble(), binding.editTextReminderLocationY.text.toString().toDouble())
                                createGeofence(latLngCoordinates, uuid, message, geofencingClient)
                                Log.d("TEST", "Geofence saved in coordinates $latLngCoordinates!")
                            }
                        }
                    } else {
                        //If reminder does not have time
                        if (reminderInfoEdit.location_x.isNotEmpty()) {
                            //if the reminder has location, set geofence
                            val message = "Hey ${reminderInfoEdit.creator_id}, don't forget to " +
                                    "${reminderInfoEdit.title} in " +
                                    "${reminderInfoEdit.location_x}, " +
                                    reminderInfoEdit.location_y
                            val latLngCoordinates = LatLng(
                                    binding.editTextReminderLocationX.text.toString().toDouble(),
                                    binding.editTextReminderLocationY.text.toString().toDouble())
                            createGeofence(latLngCoordinates, uuid, message, geofencingClient)
                            Log.d("TEST", "Geofence saved in coordinates $latLngCoordinates!")
                        }
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

                    if (reminderInfoEdit.reminder_time != "T") {

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
                            if (binding.editTextReminderLocationX.text.isNotEmpty()) {
                                val latLngCoordinates = LatLng(binding.editTextReminderLocationX.text.toString().toDouble(), binding.editTextReminderLocationY.text.toString().toDouble())
                                createGeofence(latLngCoordinates, uuid, message, geofencingClient)
                                Log.d("TEST", "Geofence saved in coordinates $latLngCoordinates!")
                            }
                        }
                    } else {
                        //If reminder does not have time
                        if (reminderInfoEdit.location_x.isNotEmpty()) {
                            //if the reminder has location, set geofence
                            val message = "Hey ${reminderInfoEdit.creator_id}, don't forget to " +
                                    "${reminderInfoEdit.title} in " +
                                    "${reminderInfoEdit.location_x}, " +
                                    reminderInfoEdit.location_y
                            val latLngCoordinates = LatLng(
                                    binding.editTextReminderLocationX.text.toString().toDouble(),
                                    binding.editTextReminderLocationY.text.toString().toDouble())
                            createGeofence(latLngCoordinates, uuid, message, geofencingClient)
                            Log.d("TEST", "Geofence saved in coordinates $latLngCoordinates!")
                        }
                    }
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
        if (requestCode == LOCATION_REQUEST_CODE) {

            // The result code from the activity started using startActivityForResults
            // startActivityForResult(intent, LOCATION_REQUEST_CODE)
            if (resultCode == RESULT_OK) {
                Log.d("MapActivity", "RESULT_OK")
                val locationXCoordinate = data!!.getStringExtra("latitude")
                val locationYCoordinate = data!!.getStringExtra("longitude")
                binding.editTextReminderLocationX.setText(locationXCoordinate)
                binding.editTextReminderLocationY.setText(locationYCoordinate)
            }
        }
    }

    private fun createGeofence(location: LatLng, uid: Int, message: String, geofencingClient: GeofencingClient) {
        Log.d("TEST", "Creating geofence")
        val geofence = Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
                .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
                .build()

        val geofenceRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

        val intent = Intent(this, GeofencingReceiver::class.java)
                .putExtra("uid", uid)
                .putExtra("message", message)

        val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        GEOFENCE_LOCATION_REQUEST_CODE
                )
            } else {
                geofencingClient.addGeofences(geofenceRequest, pendingIntent).run{
                    addOnSuccessListener {
                        Log.d("TEST", "GEOFENCE ADDED SUCCESSFULLY")
                    }
                    addOnFailureListener {
                        Log.d("TEST", "GEOFENCE FAILED")
                    }
                }
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, pendingIntent).run{
                addOnSuccessListener {
                    Log.d("TEST", "GEOFENCE ADDED SUCCESSFULLY")
                }
                addOnFailureListener {
                    Log.d("TEST", "GEOFENCE FAILED")
                }
            }
        }
    }

}