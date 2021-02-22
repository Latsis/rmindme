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
import com.latsis.rmindme.db.UserInfo

class LoginScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        //createTestCredentials() //add username "testuser" with password "testpassword" to sharedPreferences
        //createAdminCredentials() //add username "testuser:testpassword" and "admin_admin" to db

        binding.LoginButton.setOnClickListener {
            Log.d("Test", "Login Button Clicked")
            val usernameInput = findViewById<EditText>(R.id.editTextUsername).text
            if (usernameInput.trim().isNotEmpty()) {
                val passwordInput = findViewById<android.widget.EditText>(com.latsis.rmindme.R.id.editTextPassword).text
                if (passwordInput.trim().isNotEmpty()) {
                    //checkDbLoginCredentials()
                    //CredentialCheck().execute()
                    checkLoginCredentials()
                } else {
                    Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.SignInButton.setOnClickListener {
            //Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show()
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

/*
    private fun checkLoginCredentials() {
        val username_input = findViewById<EditText>(R.id.editTextUsername).text
        val password_input = findViewById<EditText>(R.id.editTextPassword).text
        val prefs = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE)
        val username_stored = prefs.getString("username",null)
        val password_stored = prefs.getString("password",null)
        Log.d("checkLoginCredentials username is ", username_input.toString())
        Log.d("username_stored is ", username_stored)
        if (username_input.toString().equals(username_stored)) {
            if (password_input.toString().equals(password_stored)) {
                applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE
                ).edit().putInt("LoginStatus", 1).apply()
                startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                )
            } else{
                Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
            }
        } else{
            Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
        }
    }


    private fun createTestCredentials() {
        applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).edit().putString("username","testuser").apply()

        applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).edit().putString("password","testpassword").apply()
    }
*/
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

/*
    private fun checkDbLoginCredentials() {
        val username_input = findViewById<EditText>(R.id.editTextUsername).text
        val password_input = findViewById<EditText>(R.id.editTextPassword).text

        AsyncTask.execute {
            val db = Room
                    .databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            getString(R.string.dbFileName)
                    )
                    .build()
            if (db.userDao().findIfExists(username_input.toString())) {
                val userInfo = db.userDao().findByName(username_input.toString())
                //val username_stored = userInfo.username
                val password_stored = userInfo.password
                db.close()
                if (password_input.toString().equals(password_stored)) {
                    applicationContext.getSharedPreferences(
                            getString(R.string.sharedPreference),
                            Context.MODE_PRIVATE
                    ).edit().putInt("LoginStatus", 1).apply()
                    applicationContext.getSharedPreferences(
                            getString(R.string.sharedPreference),
                            Context.MODE_PRIVATE
                    ).edit().putString("username",userInfo.username).apply()
                    startActivity(
                            Intent(applicationContext, MainActivity::class.java)
                    )
                } else {
                    //Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
                }
            } else {
                //Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
            }
        }
    }
*/

    private fun checkLoginCredentials() {
        var loginTask = CredentialCheck()
        loginTask.execute()
    }


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