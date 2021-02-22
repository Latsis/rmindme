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
            //Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show()
            val usernameInput = findViewById<EditText>(R.id.editTextRegisterUsername).text
            val passwordInput = findViewById<EditText>(R.id.editTextRegisterPassword).text
            val passwordRepeatInput = findViewById<EditText>(R.id.editTextRegisterRepeatPassword).text
            val userInfoInput = UserInfo(
                null,
                username = usernameInput.toString(),
                password = passwordInput.toString()
            )
            if (usernameInput.isNotEmpty()) {
                if (passwordInput.isNotEmpty()) {
                    if (passwordInput.toString().equals(passwordRepeatInput.toString())) {
                        //CreateIfDoesNotExist().execute()
                        AsyncTask.execute{
                            val db = Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                getString(R.string.dbFileName)
                            ).build()
                            //if (db.userDao().findIfExists(usernameInput.toString()).equals("false")) {
                            db.userDao().insert(userInfoInput)
                            finish()
                                //db.close()
                            //}
                            //else
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Passwords don't match!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Invalid credentials", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelProfileButton.setOnClickListener {
            finish()
        }
    }

/*    @SuppressLint("StaticFieldLeak")
    inner class CreateIfDoesNotExist : AsyncTask<String?, String?, String>() {
        override fun doInBackground(vararg params: String?): String {
            val usernameInput = findViewById<EditText>(R.id.editTextRegisterUsername).text
            val db = Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                )
                .build()
            val exists = db.userDao().findIfExists(usernameInput.toString())
            Log.d("IfExists", exists.toString())
            //db.close()
            return exists.toString()
        }

        override fun onPostExecute(exists: String?) {
            super.onPostExecute(exists)
            if (exists.equals("true")) {
                Toast.makeText(
                    applicationContext,
                    "Username already taken!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val usernameInput = findViewById<EditText>(R.id.editTextRegisterUsername).text
                val passwordInput = findViewById<EditText>(R.id.editTextRegisterPassword).text
                val userInfoInput = UserInfo(
                    null,
                    username = usernameInput.toString(),
                    password = passwordInput.toString()
                )
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                ).build()
                db.userDao().insert(userInfoInput).toInt()
                db.close()

            }
            finish()
        }
    }*/
}