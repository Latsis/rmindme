package com.latsis.rmindme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Button

class LoginScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        findViewById<Button>(R.id.LoginButton).setOnClickListener {
            Log.d("Test", "Login Button Clicked")
            startActivity(
                    Intent(applicationContext, MainActivity::class.java)
            )
        }
    }
}