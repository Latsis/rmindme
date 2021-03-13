package com.latsis.rmindme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityProfileScreenBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.UserInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class ProfileScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileScreenBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        title = "Profile"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
        }

        fusedLocationClient.setMockMode(true)

        val currentUsername = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE
        ).getString("username", null)

        val currentFakeLocation = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE
        ).getString("fake_coordinates", "65.08238,25.44262")

        showCurrentUserName(currentUsername.toString())
        showCurrentFakeLocation()

        binding.saveProfileModificationsButton.setOnClickListener {
            val newPasswordInput = binding.editTextProfileNewPassword.text
            val newPasswordRepeatInput = binding.editTextProfileRepeatNewPassword.text

            if (newPasswordInput.isNotEmpty()) {
                if (newPasswordInput.toString().equals(newPasswordRepeatInput.toString())) {
                    AsyncTask.execute {
                        val db = Room
                                .databaseBuilder(
                                        applicationContext,
                                        AppDatabase::class.java,
                                        getString(R.string.dbFileName)
                                )
                                .build()
                        val currentUserInfo = db.userDao().findByName(currentUsername.toString())
                        val newUserInfo = UserInfo(
                                uid = currentUserInfo.uid,
                                username = currentUserInfo.username,
                                password = newPasswordInput.toString()
                        )
                        db.userDao().updateUserInfo(newUserInfo)
                        db.close()
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, "Passwords don't match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }


        binding.cancelProfileModificationsButton.setOnClickListener {
            //Log.d("Test", "Cancel Button Clicked")
            finish()
        }

        binding.setUserFakeLocationButton.setOnClickListener {
            Log.d("Test", "Location Button Clicked")
            val intent = Intent(this, MapActivity::class.java)

            val locationPrint = binding.textViewCurrentFakeLocation.text.toString()
                    .split(":")[1]
            val fakeLatitude = locationPrint.split(",")[0]
            val fakeLongitude = locationPrint.split(",")[1]
            intent.putExtra("latitude", fakeLatitude)
            intent.putExtra("longitude", fakeLongitude)

            startActivityForResult(intent, REQUEST_VIRTUAL_LOCATION)
        }
    }

    private fun showCurrentUserName(currentUsername: String) {
        val usernameTextView: TextView = binding.textViewCurrentUsername
        usernameTextView.text = ("Currently logged in as $currentUsername")
    }

    private fun showCurrentFakeLocation() {
        val currentFakeLocation = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE
        ).getString("fake_coordinates", "65.08238,25.44262")
        val fakeLocationTextView: TextView = binding.textViewCurrentFakeLocation
        fakeLocationTextView.text = "Current location: $currentFakeLocation"
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun getCurrentFakeLocation(): String {
        fusedLocationClient.setMockMode(true)
        val currentFakeLocation = fusedLocationClient.lastLocation

        return currentFakeLocation.toString()
    }

    @SuppressLint("MissingPermission", "NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If you have multiple activities returning results then you should include unique request codes for each
        if (requestCode == REQUEST_VIRTUAL_LOCATION) {
            // The result code from the activity started using startActivityForResults
            // startActivityForResult(intent, 1)
            if (resultCode == RESULT_OK) {
                Log.d("MapActivity", "RESULT_OK")
                val locationXCoordinate = data!!.getStringExtra("latitude")
                val locationYCoordinate = data!!.getStringExtra("longitude")
                val currentFakeLocation = Location(LocationManager.GPS_PROVIDER)
                currentFakeLocation.latitude = locationXCoordinate!!.toDouble()
                currentFakeLocation.longitude = locationYCoordinate!!.toDouble()
                currentFakeLocation.accuracy= 3.0f
                currentFakeLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                currentFakeLocation.time = System.currentTimeMillis()
                fusedLocationClient.setMockLocation(currentFakeLocation)
                binding.textViewCurrentFakeLocation.text = "Current location: $locationXCoordinate, $locationYCoordinate"

                applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE
                ).edit().putString("fake_coordinates", "$locationXCoordinate, $locationYCoordinate").apply()
                showCurrentFakeLocation()
            }
        }
    }
}