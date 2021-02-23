package com.latsis.rmindme

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityLoginScreenBinding
import com.latsis.rmindme.databinding.ActivityMainBinding
import com.latsis.rmindme.databinding.ActivityProfileScreenBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.UserInfo

class ProfileScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        title = "Profile"

        val currentUsername = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE
        ).getString("username", null)

        showCurrentUserName(currentUsername.toString())

        binding.saveProfileModificationsButton.setOnClickListener {
            val newPasswordInput = findViewById<EditText>(R.id.editTextProfileNewPassword).text
            val newPasswordRepeatInput = findViewById<EditText>(R.id.editTextProfileRepeatNewPassword).text

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
    }

    private fun showCurrentUserName(currentUsername: String) {
        val usernameTextView: TextView = findViewById<TextView>(R.id.textViewCurrentUsername)
        usernameTextView.text = ("Currently logged in as $currentUsername")

    }
}