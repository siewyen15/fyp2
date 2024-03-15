package com.example.fyp2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddNewWorkoutActivity : AppCompatActivity() {

    private lateinit var spinnerWorkout: Spinner
    private lateinit var spinnerDuration: Spinner
    private lateinit var spinnerSets: Spinner
    private lateinit var editTextWorkoutName: EditText
    private lateinit var buttonAdd: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_workout)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        spinnerWorkout = findViewById(R.id.spinner_workout)
        spinnerDuration = findViewById(R.id.spinner_duration)
        spinnerSets = findViewById(R.id.spinner_sets)
        editTextWorkoutName = findViewById(R.id.editText_workout_name)
        buttonAdd = findViewById(R.id.button_add)

        // Populate workout options dropdown
        val workoutOptions = arrayOf("Push-ups", "Sit-ups", "Squats", "Plank")
        val workoutAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workoutOptions)
        workoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWorkout.adapter = workoutAdapter

        // Populate duration options dropdown
        val durationOptions = arrayOf("5", "10", "15", "20", "25", "30")
        val durationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durationOptions)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDuration.adapter = durationAdapter

        // Populate sets options dropdown
        val setsOptions = arrayOf("1", "2", "3", "4", "5")
        val setsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, setsOptions)
        setsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSets.adapter = setsAdapter

        buttonAdd.setOnClickListener {
            val selectedWorkout = spinnerWorkout.selectedItem.toString()
            val selectedDuration = spinnerDuration.selectedItem.toString()
            val selectedSets = spinnerSets.selectedItem.toString()
            val workoutName = editTextWorkoutName.text.toString()

            // Get current user ID
            val currentUser = auth.currentUser
            currentUser?.let { user ->
                // Save workout data to Firestore
                val workoutData = hashMapOf(
                    "workout" to selectedWorkout,
                    "duration" to selectedDuration.toInt(),
                    "sets" to selectedSets.toInt(),
                    "workoutName" to workoutName
                )

                firestore.collection("users")
                    .document(user.uid)
                    .collection("workouts")
                    .add(workoutData)
                    .addOnSuccessListener {
                        // Workout data saved successfully
                        // Display a toast message
                        Toast.makeText(this, "Workout created successfully", Toast.LENGTH_SHORT).show()
                        // Return to WorkoutActivity
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors
                        Toast.makeText(this, "Failed to create workout: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
