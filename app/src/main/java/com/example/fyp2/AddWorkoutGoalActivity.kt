package com.example.fyp2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityAddWorkoutGoalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddWorkoutGoalActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityAddWorkoutGoalBinding
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkoutGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the spinner with exercise types
        val exerciseTypes = arrayOf("Push-ups", "Sit-ups", "Squats", "Plank")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, exerciseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.exerciseTypeSpinner.adapter = adapter

        // Set click listener for the select date button
        binding.selectDateButton.setOnClickListener {
            showDatePicker()
        }

        // Set click listener for the save button
        binding.saveButton.setOnClickListener {
            saveWorkoutGoal()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedDate.set(Calendar.YEAR, year)
        selectedDate.set(Calendar.MONTH, month)
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateEditText()
    }

    private fun updateDateEditText() {
        val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
        binding.dateEditText.setText(dateFormat.format(selectedDate.time))
    }

    private fun saveWorkoutGoal() {
        val selectedExercise = binding.exerciseTypeSpinner.selectedItem.toString()
        val goal = binding.goalEditText.text.toString()

        // Get a reference to the Firestore collection
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        // Check if the user is authenticated
        user?.let { currentUser ->
            // Format the selected date as a string
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(selectedDate.time)

            // Create a new workout goal document
            val workoutGoal = hashMapOf(
                "exercise" to selectedExercise,
                "goal" to goal,
                "date" to dateString // Save date as string
            )

            // Check if the selected date is before today's date
            if (selectedDate.before(Calendar.getInstance())) {
                // If so, mark the goal as completed
                // If the goal is completed, mark it as such
                workoutGoal["completed"] = true.toString()
                // Add the completed workout goal document to Firestore
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("completeWorkoutGoals")
                    .add(workoutGoal)
                    .addOnSuccessListener { documentReference ->
                        // Show a success message
                        Toast.makeText(this, "Workout goal completed and saved successfully!", Toast.LENGTH_SHORT).show()
                        // Finish the activity and return to WorkoutGoalActivity
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Show an error message
                        Toast.makeText(this, "Error saving completed workout goal: $e", Toast.LENGTH_SHORT).show()
                        // Finish the activity and return to WorkoutGoalActivity
                        finish()
                    }
            } else {
                // If not, add the workout goal to the regular collection
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

}
