package com.latsis.rmindme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.latsis.rmindme.databinding.ActivityLoginScreenBinding
import com.latsis.rmindme.databinding.ActivityMainBinding


class LoginScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        createTestCredentials() //add username "testuser" with password "testpassword" to sharedPreferences

        binding.LoginButton.setOnClickListener {
            Log.d("Test", "Login Button Clicked")
            checkLoginCredentials()
        }

        checkLoginStatus()
    }

/*    override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }*/

    private fun checkLoginStatus() {
        val loginStatus = applicationContext.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getInt("LoginStatus", 0)
        if (loginStatus == 1) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

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
}