package com.example.fyp2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityAddWorkoutGoalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddWorkoutGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWorkoutGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkoutGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the spinner with exercise types
        val exerciseTypes = arrayOf("Push-ups", "Sit-ups", "Squats", "Plank")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.exerciseTypeSpinner.adapter = adapter

        // Set click listener for the save button
        binding.saveButton.setOnClickListener {
            saveWorkoutGoal()
        }
    }

    private fun saveWorkoutGoal() {
        val selectedExercise = binding.exerciseTypeSpinner.selectedItem.toString()
        val goal = binding.goalEditText.text.toString()

        // Get a reference to the Firestore collection
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        // Check if the user is authenticated
        user?.let { currentUser ->
            // Create a new workout goal document
            val workoutGoal = hashMapOf(
                "exercise" to selectedExercise,
                "goal" to goal
            )

            // Add the workout goal document to Firestore
            db.collection("users")
                .document(currentUser.uid)
                .collection("workoutGoals")
                .add(workoutGoal)
                .addOnSuccessListener { documentReference ->
                // Show a success message
                Toast.makeText(this, "Workout goal saved successfully!", Toast.LENGTH_SHORT).show()
                // Finish the activity and return to WorkoutGoalActivity
                finish()
            }
                .addOnFailureListener { e ->
                    // Show an error message
                    Toast.makeText(this, "Error saving workout goal: $e", Toast.LENGTH_SHORT).show()
                    // Finish the activity and return to WorkoutGoalActivity
                    finish()
                }

        }
    }

}
