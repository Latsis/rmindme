package com.latsis.rmindme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        binding.saveProfileModificationsButton.setOnClickListener {
            Log.d("Test", "Save Button Clicked")
        }

            binding.cancelProfileModificationsButton.setOnClickListener {
            Log.d("Test", "Cancel Button Clicked")
                finish()
        }
    }
}