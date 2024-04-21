package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class PhysicalHealthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_physical_health)

        // Find the ImageButtons by their IDs
        val workoutButton: ImageButton = findViewById(R.id.workoutButton)
        val reportButton: ImageButton = findViewById(R.id.reportButton)
        val goalButton: ImageButton = findViewById(R.id.goalButton) // Add this line

        // Set click listeners for the ImageButtons
        workoutButton.setOnClickListener {
            // Navigate to the WorkoutActivity
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }

        reportButton.setOnClickListener {
            // Navigate to the WorkoutReportActivity
            val intent = Intent(this, WorkoutReportActivity::class.java)
            startActivity(intent)
        }

        goalButton.setOnClickListener {
            // Navigate to the WorkoutGoalActivity
            val intent = Intent(this, WorkoutGoalActivity::class.java)
            startActivity(intent)
        }
    }
}
