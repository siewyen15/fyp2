package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MentalHealthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mental_health)


        // Find ImageButtons by their IDs
        val moodButton = findViewById<ImageButton>(R.id.button_mood)
        val mentalWellnessButton = findViewById<ImageButton>(R.id.button_mental_wellness)
        val journalButton = findViewById<ImageButton>(R.id.button_journal)
        val emergencyContactButton = findViewById<ImageButton>(R.id.button_emergency_contact)
        val waterButton = findViewById<ImageButton>(R.id.button_water)

        // Set click listeners for each ImageButton
        moodButton.setOnClickListener {
            // Launch MoodActivity
            startActivity(Intent(this, MoodActivity::class.java))
        }

        mentalWellnessButton.setOnClickListener {
            // Launch MentalWellnessActivity
            startActivity(Intent(this, MentalWellnessActivity::class.java))
        }

        journalButton.setOnClickListener {
            // Launch JournalActivity
            startActivity(Intent(this, JournalActivity::class.java))
        }

        emergencyContactButton.setOnClickListener {
            // Launch EmergencyContactActivity
            startActivity(Intent(this, EmergencyContactActivity::class.java))
        }

        waterButton.setOnClickListener {
            // Launch WaterActivity
            startActivity(Intent(this, WaterActivity::class.java))
        }

    }
}
