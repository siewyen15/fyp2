package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityWorkoutBinding

class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set OnClickListener for each button
        binding.pushupsButton.setOnClickListener {
            navigateToActivity(PushUpsActivity::class.java)
        }

        binding.situpsButton.setOnClickListener {
            navigateToActivity(SitUpsActivity::class.java)
        }

        binding.squatsButton.setOnClickListener {
            navigateToActivity(SquatsActivity::class.java)
        }

        binding.plankButton.setOnClickListener {
            navigateToActivity(PlanksActivity::class.java)
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
