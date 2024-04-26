package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityWorkoutBinding
import com.example.fyp2.databinding.ActivityYogaBinding

class YogaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYogaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYogaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set OnClickListener for each button
        binding.pushupsButton.setOnClickListener {
            navigateToActivity(YogaPose1Activity::class.java)
        }

        binding.situpsButton.setOnClickListener {
            navigateToActivity(YogaPose2Activity::class.java)
        }

        binding.squatsButton.setOnClickListener {
            navigateToActivity(YogaPose3Activity::class.java)
        }

        binding.plankButton.setOnClickListener {
            navigateToActivity(YogaPose4Activity::class.java)
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
