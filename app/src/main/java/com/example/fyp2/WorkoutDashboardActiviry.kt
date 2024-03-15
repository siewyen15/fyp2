package com.example.fyp2

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutDashboardActivity : AppCompatActivity() {

    private lateinit var pushupsChart: PieChart
    private lateinit var situpsChart: PieChart
    private lateinit var squatsChart: PieChart
    private lateinit var plankChart: PieChart
    private lateinit var goalDropdownPushups: Spinner
    private lateinit var goalDropdownSitups: Spinner
    private lateinit var goalDropdownSquats: Spinner
    private lateinit var goalDropdownPlank: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_dashboard_activity)

        // Initialize views
        pushupsChart = findViewById(R.id.chart_pushups)
        situpsChart = findViewById(R.id.chart_situps)
        squatsChart = findViewById(R.id.chart_squats)
        plankChart = findViewById(R.id.chart_plank)
        goalDropdownPushups = findViewById(R.id.spinner_pushups)
        goalDropdownSitups = findViewById(R.id.spinner_situps)
        goalDropdownSquats = findViewById(R.id.spinner_squats)
        goalDropdownPlank = findViewById(R.id.spinner_plank)

        // Set up charts
        setupChart(pushupsChart)
        setupChart(situpsChart)
        setupChart(squatsChart)
        setupChart(plankChart)

        // Populate dropdown lists with weekly goals
        val goals = arrayOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "100")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, goals)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        goalDropdownPushups.adapter = adapter
        goalDropdownSitups.adapter = adapter
        goalDropdownSquats.adapter = adapter
        goalDropdownPlank.adapter = adapter

        // Calculate completion percentage and update pie charts
        calculateAndUpdateCompletionPercentage("Push-ups", pushupsChart)
        calculateAndUpdateCompletionPercentage("Sit-ups", situpsChart)
        calculateAndUpdateCompletionPercentage("Squats", squatsChart)
        calculateAndUpdateCompletionPercentage("Plank", plankChart)
    }

    fun onAddButtonClick(view: View) {
        val intent = Intent(this, WorkoutActivity::class.java)
        startActivity(intent)
    }

    private fun setupChart(chart: PieChart) {
        // Chart setup code here...
    }

    private fun calculateAndUpdateCompletionPercentage(workoutType: String, chart: PieChart) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val firestore = FirebaseFirestore.getInstance()
            val workoutsRef = firestore.collection("workoutTypes").document(workoutType)
                .collection(user.uid)

            workoutsRef.get()
                .addOnSuccessListener { querySnapshot ->
                    var totalSets = 0
                    querySnapshot.forEach { document ->
                        val setsCompleted = document.getLong("setsCompleted") ?: 0
                        totalSets += setsCompleted.toInt()
                    }
                    // Calculate completion percentage
                    val weeklyGoal = goalDropdownPushups.selectedItem.toString().toInt()
                    val completionPercentage =
                        (totalSets.toFloat() / weeklyGoal.toFloat()) * 100
                    updatePieChart(chart, completionPercentage)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting data for $workoutType: ${e.message}")
                }
        }
    }

    private fun updatePieChart(chart: PieChart, completionPercentage: Float) {
        // Update pie chart with completion percentage
        val remainingPercentage = 100 - completionPercentage

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(completionPercentage, "Completed"))
        entries.add(PieEntry(remainingPercentage, "Remaining"))

        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = mutableListOf(
            android.R.color.holo_green_light,
            android.R.color.holo_red_light
        )

        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(resources.getColor(android.R.color.black))

        chart.data = data
        chart.invalidate()
    }
}
