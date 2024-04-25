package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NutritionHomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nutrition_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Find the ImageButtons by their IDs
        /*val trackNutritionButton = findViewById<ImageButton>(R.id.imageButton4)
        val rateUsButton = findViewById<ImageButton>(R.id.rateus)
        val recipeButton = findViewById<ImageButton>(R.id.recipebook)
        val taskReminderButton = findViewById<ImageButton>(R.id.taskreminder)*/
        val calculateBMIButton = findViewById<ImageButton>(R.id.calculatebmi)
   /*     val logMealButton = findViewById<ImageButton>(R.id.logmeal)

        // Set click listeners for each button
        trackNutritionButton.setOnClickListener {
            startActivity(Intent(this, TrackNutritionActivity::class.java))
        }

        rateUsButton.setOnClickListener {
            startActivity(Intent(this, RateUsActivity::class.java))
        }

        recipeButton.setOnClickListener {
            startActivity(Intent(this, RecipeActivity::class.java))
        }

        taskReminderButton.setOnClickListener {
            startActivity(Intent(this, TaskReminderActivity::class.java))
        }*/

        calculateBMIButton.setOnClickListener {
            startActivity(Intent(this, BmiCalculator::class.java))
        }

      /*  logMealButton.setOnClickListener {
            startActivity(Intent(this, LogMealActivity::class.java))
        }*/
    }
}