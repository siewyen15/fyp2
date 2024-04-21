package com.example.fyp2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityWorkoutReportBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutReportBinding

    private var exerciseName: String? = null
    private var goal: Int = 0
    private var completedSets: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calculateButton.setOnClickListener {
            calculateCompletionPercentage()
        }
    }

    private fun calculateCompletionPercentage() {
        val enteredExerciseName = binding.exerciseNameEditText.text.toString()
        val enteredCompletedSets = binding.completedSetsEditText.text.toString().toIntOrNull()

        if (enteredExerciseName.isNotEmpty() && enteredCompletedSets != null) {
            retrieveExerciseDetails(enteredExerciseName)
        } else {
            Toast.makeText(this, "Please enter valid exercise name and completed sets", Toast.LENGTH_SHORT).show()
        }
    }

    private fun retrieveExerciseDetails(exerciseName: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("workouts")
            .document(exerciseName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    this.exerciseName = document.getString("exerciseName")
                    goal = document.getLong("goal")?.toInt() ?: 0
                    completedSets = binding.completedSetsEditText.text.toString().toInt()

                    if (goal > 0) {
                        val completionPercentage = (completedSets.toFloat() / goal.toFloat()) * 100
                        displayChart(completionPercentage)
                    } else {
                        Toast.makeText(this, "Goal not found or invalid", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Exercise not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve exercise details: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayChart(completionPercentage: Float) {
        val pieChart = binding.percentageChart

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(completionPercentage, "Completion Percentage"))

        val dataSet = PieDataSet(entries, "Completion Percentage")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data

        pieChart.description.isEnabled = false
        pieChart.centerText = "Completion Percentage"
        pieChart.setCenterTextSize(18f)

        pieChart.animateXY(1000, 1000)
        pieChart.invalidate()
    }
}
