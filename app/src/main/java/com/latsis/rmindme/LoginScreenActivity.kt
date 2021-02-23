package com.latsis.rmindme

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityLoginScreenBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo
import com.latsis.rmindme.db.UserInfo

class LoginScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        // Add placeholder items and test credentials to db
        //createAdminCredentials() //add username "testuser:testpassword" and "admin_admin" to db
        //createReminderPlaceholders()

        binding.LoginButton.setOnClickListener {
            Log.d("Test", "Login Button Clicked")
            val usernameInput = findViewById<EditText>(R.id.editTextUsername).text
            if (usernameInput.trim().isNotEmpty()) {
                val passwordInput = findViewById<android.widget.EditText>(com.latsis.rmindme.R.id.editTextPassword).text
                if (passwordInput.trim().isNotEmpty()) {
                    checkLoginCredentials()
                } else {
                    Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.SignInButton.setOnClickListener {
            Log.d("Test", "Sign Up Button Clicked")
            startActivity(
                    Intent(applicationContext, RegisterScreenActivity::class.java)
            )
        }

        checkLoginStatus()
    }

    override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val loginStatus = applicationContext.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getInt("LoginStatus", 0)
        if (loginStatus == 1) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }


    private fun createAdminCredentials() {
        val userInfo1 = UserInfo(
                null,
                username = "admin",
                password = "admin"
        )
        val userInfo2 = UserInfo(
                null,
                username = "testuser",
                password = "testpassword"
        )

        AsyncTask.execute {
            val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
            ).build()
            db.userDao().insert(userInfo1).toInt()
            db.userDao().insert(userInfo2).toInt()
            db.close()
        }
    }

    private fun createReminderPlaceholders() {
        val reminderInfo1 = ReminderInfo(
                null,
                title = "Test Reminder 1",
                message = "This is a placeholder for a reminder",
                location_x = "X coordinate for location",
                location_y = "Y coordinate for location",
                reminder_time = "2021-02-02T02:02",
                creation_time = "2021-01-01T12:00:00.001",
                creator_id = "testuser",
                reminder_seen = "0"
        )

        val reminderInfo2 = ReminderInfo(
                null,
                title = "Hetki lyö",
                message = "Viime hetki lyö",
                location_x = "22.45",
                location_y = "22.22",
                reminder_time = "2021-02-02T02:02",
                creation_time = "2021-01-01T12:00:00.001",
                creator_id = "testuser",
                reminder_seen = "0"
        )

        val reminderInfo3 = ReminderInfo(
                null,
                title = "Tää yö",
                message = "Ollaan vaan ja hengaillaan",
                location_x = "22.22",
                location_y = "45.45",
                reminder_time = "2021-02-02T22:45",
                creation_time = "2021-01-01T02:00:00.001",
                creator_id = "testuser",
                reminder_seen = "0"
        )

        val reminderInfo4 = ReminderInfo(
                null,
                title = "Administrator's reminder",
                message = "For British eyes only",
                location_x = "somewhere",
                location_y = "here or there",
                reminder_time = "2021-02-02T22:45",
                creation_time = "2021-01-01T02:00:00.001",
                creator_id = "admin",
                reminder_seen = "0"
        )

        AsyncTask.execute{
            val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
            ).build()
            db.reminderDao().insert(reminderInfo1).toInt()
            db.reminderDao().insert(reminderInfo2).toInt()
            db.reminderDao().insert(reminderInfo3).toInt()
            db.reminderDao().insert(reminderInfo4).toInt()
            db.close()
        }
    }


    private fun checkLoginCredentials() {
        var loginTask = CredentialCheck()
        loginTask.execute()
    }


    @SuppressLint("StaticFieldLeak")
    inner class CredentialCheck : AsyncTask<String?, String?, UserInfo>() {
        override fun doInBackground(vararg params: String?): UserInfo {
            val usernameInput = findViewById<EditText>(R.id.editTextUsername).text
            val passwordInput = findViewById<EditText>(R.id.editTextPassword).text
            val db = Room
                    .databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    )
                    .build()
            val userInfo = db.userDao().findByName(usernameInput.toString())
            db.close()
            return userInfo
        }

        override fun onPostExecute(userInfo: UserInfo?) {
            super.onPostExecute(userInfo)
            if (userInfo != null) {
                val usernameInput = findViewById<EditText>(R.id.editTextUsername).text
                val passwordInput = findViewById<EditText>(R.id.editTextPassword).text
                if (passwordInput.toString().equals(userInfo.password)) {
                    applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE
                    ).edit().putInt("LoginStatus", 1).apply()
                    applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE
                    ).edit().putString("username", userInfo.username).apply()
                    startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                    )
                } else {
                    Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}