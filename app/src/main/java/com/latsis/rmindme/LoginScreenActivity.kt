package com.latsis.rmindme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import com.latsis.rmindme.databinding.ActivityLoginScreenBinding
import com.latsis.rmindme.databinding.ActivityMainBinding


class LoginScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        binding.LoginButton.setOnClickListener {
            Log.d("Test", "Login Button Clicked")

            //Authentication goes here

            //save loginstatus

            applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
            ).edit().putInt("LoginStatus", 1).apply()

            startActivity(
                    Intent(applicationContext, MainActivity::class.java)
            )
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
}