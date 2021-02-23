package com.latsis.rmindme

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import com.latsis.rmindme.databinding.ActivityProfileScreenBinding
import com.latsis.rmindme.databinding.ActivityRegisterScreenBinding
import com.latsis.rmindme.db.AppDatabase
import com.latsis.rmindme.db.ReminderInfo
import java.time.LocalDateTime
import com.latsis.rmindme.db.UserInfo

class RegisterScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        title = "Register"

        binding.registerProfileButton.setOnClickListener {
            val usernameInput = findViewById<EditText>(R.id.editTextRegisterUsername).text
            val passwordInput = findViewById<EditText>(R.id.editTextRegisterPassword).text
            val passwordRepeatInput = findViewById<EditText>(R.id.editTextRegisterRepeatPassword).text

            if (usernameInput.isNotEmpty()) {
                if (passwordInput.isNotEmpty()) {
                    if (passwordInput.toString().equals(passwordRepeatInput.toString())) {
                        CreateIfDoesNotExist().execute()
                    } else {
                        Toast.makeText(applicationContext,
                            "Passwords don't match!",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelProfileButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class CreateIfDoesNotExist : AsyncTask<String?, String?, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean {
            val usernameInput = findViewById<EditText>(R.id.editTextRegisterUsername).text
            val passwordInput = findViewById<EditText>(R.id.editTextRegisterPassword).text
            val db = Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                ).build()
            val exists = db.userDao().findIfExists(usernameInput.toString())
            if (!exists) {
                val userInfoInput = UserInfo(
                        null,
                        username = usernameInput.toString(),
                        password = passwordInput.toString()
                )
                db.userDao().insert(userInfoInput)
                db.close()
                finish()
            }
            Log.d("IfExists", exists.toString())
            db.close()
            return exists
        }

        override fun onPostExecute(exists: Boolean?) {
            super.onPostExecute(exists)
            Log.d("exists", exists.toString())
            if (exists==true) {
                Toast.makeText(
                        applicationContext,
                        "Username already taken!",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}