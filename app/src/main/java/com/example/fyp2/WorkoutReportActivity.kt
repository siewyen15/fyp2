package com.example.fyp2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityWorkoutReportBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutReportBinding
    private var selectedYear = 0
    private var selectedMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calculateButton.setOnClickListener {
            // Ensure the year, month, exercise name, and completed sets are valid
            val enteredExerciseName = binding.exerciseNameEditText.text.toString()
            val enteredCompletedSets = binding.completedSetsEditText.text.toString().toIntOrNull()
            val enteredYear = binding.yearEditText.text.toString().toIntOrNull()
            val enteredMonth = binding.monthEditText.text.toString().toIntOrNull()

            if (enteredExerciseName.isNotEmpty() && enteredCompletedSets != null && enteredYear != null && enteredMonth != null) {
                selectedYear = enteredYear
                selectedMonth = enteredMonth
                retrieveExerciseDetails(enteredExerciseName)
            } else {
                Toast.makeText(
                    this,
                    "Please enter valid exercise name, completed sets, year, and month",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun retrieveExerciseDetails(exerciseName: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("workoutGoalsCompleted")
                .whereEqualTo("exercise", exerciseName)
                .get()
                .addOnSuccessListener { documents ->
                    var totalGoal = 0
                    if (!documents.isEmpty) { // Check if documents list is not empty
                        documents.forEach { document ->
                            val goal = document.getString("goal")?.toIntOrNull() ?: 0
                            val date = document.getString("date")
                            if (date != null && isDateInSelectedYearAndMonth(date)) {
                                totalGoal += goal
                            }
                        }
                        // Log the retrieved totalGoal value for debugging
                        Log.d("WorkoutReportActivity", "Total goal: $totalGoal")
                        // Call calculateCompletionPercentage() here after totalGoal is retrieved
                        calculateCompletionPercentage(totalGoal)
                    } else {
                        // If no documents are retrieved, display an appropriate message
                        Toast.makeText(
                            this,
                            "No workout goals found for the selected period",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to retrieve exercise details: $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun isDateInSelectedYearAndMonth(dateString: String): Boolean {
        val parts = dateString.split("-")
        if (parts.size == 3) {
            val year = parts[0].toIntOrNull()
            val month = parts[1].toIntOrNull()
            return year != null && month != null && year == selectedYear && month == selectedMonth
        }
        return false
    }


    private fun calculateCompletionPercentage(totalGoal: Int) {
        // Log the totalGoal value for debugging
        Log.d("WorkoutReportActivity", "Total goal in calculateCompletionPercentage(): $totalGoal")
        val enteredCompletedSets = binding.completedSetsEditText.text.toString().toIntOrNull()
        Log.d("WorkoutReportActivity", "Entered completed sets: $enteredCompletedSets")
        val completionPercentage = if (enteredCompletedSets != null && totalGoal > 0) {
            (enteredCompletedSets.toFloat() / totalGoal.toFloat()) * 100
        } else {
            0f
        }
        displayCompletionPercentage(completionPercentage, enteredCompletedSets ?: 0, totalGoal)
    }


    private fun displayCompletionPercentage(completionPercentage: Float, enteredCompletedSets: Int, totalGoal: Int) {
        val percentageText = "Completion Percentage: $completionPercentage%\n\n" +
                "Total Completed Sets: $enteredCompletedSets\n" +
                "Total Goal: $totalGoal\n\n" +
                "Calculation Steps:\n" +
                "$enteredCompletedSets / $totalGoal * 100 = $completionPercentage%"
        binding.completionPercentageTextView.text = percentageText
    }
}
