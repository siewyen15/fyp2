package com.example.fyp2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CalorieCountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_count)

        val textViewCalorieBurnPushups = findViewById<TextView>(R.id.textViewCalorieBurnPushups)
        val textViewCalorieBurnSitups = findViewById<TextView>(R.id.textViewCalorieBurnSitups)
        val textViewCalorieBurnSquats = findViewById<TextView>(R.id.textViewCalorieBurnSquats)
        val textViewCalorieBurnPlank = findViewById<TextView>(R.id.textViewCalorieBurnPlank)
        val spinnerExercise = findViewById<Spinner>(R.id.spinnerExercise)
        val editTextMinutes = findViewById<EditText>(R.id.editTextMinutes)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val textViewResult = findViewById<TextView>(R.id.textViewResult)

        // Set calorie burn per minute for each exercise
        textViewCalorieBurnPushups.text = "Pushups: 10.0"
        textViewCalorieBurnSitups.text = "Situps: 8.0"
        textViewCalorieBurnSquats.text = "Squats: 7.0"
        textViewCalorieBurnPlank.text = "Plank: 5.0"

        // Calculate calorie burn on button click
        btnCalculate.setOnClickListener {
            val exercise = spinnerExercise.selectedItem.toString()
            val minutes = editTextMinutes.text.toString().toDoubleOrNull() ?: 0.0

            val calorieBurn = calculateCalorieBurn(exercise, minutes)
            textViewResult.text = "Result: $calorieBurn calories"
        }
    }

    private fun calculateCalorieBurn(exercise: String, minutes: Double): Double {
        // Define calorie burn per minute for each exercise
        val calorieBurnMap = mapOf(
            "Pushups" to 10.0,
            "Situps" to 8.0,
            "Squats" to 7.0,
            "Plank" to 5.0
        )

        // Calculate total calorie burn
        return calorieBurnMap[exercise]!! * minutes
    }
}
