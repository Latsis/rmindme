package com.latsis.rmindme

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.latsis.rmindme.databinding.ActivityLoginScreenBinding
import com.latsis.rmindme.databinding.ActivityMainBinding
import com.latsis.rmindme.databinding.ActivityProfileScreenBinding

class ProfileScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        title = "Profile"

        showCurrentUserName()

        binding.saveProfileModificationsButton.setOnClickListener {
            Log.d("Test", "Save Button Clicked")
            Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show()
        }

        binding.cancelProfileModificationsButton.setOnClickListener {
            Log.d("Test", "Cancel Button Clicked")
            finish()
        }
    }

    private fun showCurrentUserName() {
        val current_username = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("username", null)
        val usernameTextView: TextView = findViewById<TextView>(R.id.textViewCurrentUsername)
        usernameTextView.text = ("Currently logged in as $current_username")

    }
}