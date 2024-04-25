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
        val calendarButton: ImageButton = findViewById(R.id.calendarButton) // Add this line
        val calorieButton: ImageButton = findViewById(R.id.calorieButton) // Add this line
        val bodyDataButton: ImageButton = findViewById(R.id.bodyDataButton) // Add this line

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
        // Inside the OnClickListener for ImageButton 4 (Calendar)
        calendarButton.setOnClickListener {
            val calendarIntent = Intent(this, WorkoutCalendarActivity::class.java)
            startActivity(calendarIntent)
        }

        // Inside the OnClickListener for ImageButton 5 (Calorie)
        calorieButton.setOnClickListener {
            val calorieIntent = Intent(this, CalorieCountActivity::class.java)
            startActivity(calorieIntent)
        }

//         // Inside the OnClickListener for ImageButton 6 (Body)
//            val bodyIntent = Intent(this, BodyDataActivity::class.java)
//            startActivity(bodyIntent)

    }
}
