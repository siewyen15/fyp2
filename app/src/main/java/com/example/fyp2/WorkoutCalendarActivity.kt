package com.example.fyp2

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class WorkoutCalendarActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var yearSpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var filterButton: Button
    private lateinit var adapter: CalendarGoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_calendar)

        db = FirebaseFirestore.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        recyclerView = findViewById(R.id.recyclerView)
        yearSpinner = findViewById(R.id.yearSpinner)
        monthSpinner = findViewById(R.id.monthSpinner)
        filterButton = findViewById(R.id.filterButton)

        // Initialize RecyclerView and its adapter
        adapter = CalendarGoalAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up spinners
        setUpSpinners()

        // Set listener for filter button
        filterButton.setOnClickListener {
            val selectedYear = yearSpinner.selectedItem.toString()
            val selectedMonth = monthSpinner.selectedItem.toString()
            retrieveAndDisplayWorkoutGoals(selectedYear, selectedMonth)
        }

        // Display all workout goals by default
        retrieveAndDisplayWorkoutGoals(null, null)
    }

    private fun setUpSpinners() {
        // Set up year spinner
        val years = arrayOf("2022", "2023", "2024") // Example years, replace with your data
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        // Set up month spinner
        val months = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter
    }

    private fun retrieveAndDisplayWorkoutGoals(year: String?, month: String?) {
        val goalsQuery = db.collection("users")
            .document(currentUser)
            .collection("workoutGoals")

        // Apply filters if they are provided
        year?.let { goalsQuery.whereEqualTo("year", it) }
        month?.let { goalsQuery.whereEqualTo("month", it) }

        // Retrieve workout goals
        goalsQuery.get()
            .addOnSuccessListener { workoutGoals ->
                retrieveAndDisplayCompletedGoals(year, month, workoutGoals)
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur while retrieving goals
                // For example, log the error or display a toast message
                showToast("Failed to retrieve workout goals")
            }
    }

    private fun retrieveAndDisplayCompletedGoals(year: String?, month: String?, workoutGoals: QuerySnapshot) {
        val completedGoalsQuery = db.collection("users")
            .document(currentUser)
            .collection("workoutGoalsCompleted")

        // Apply filters if they are provided
        year?.let { completedGoalsQuery.whereEqualTo("year", it) }
        month?.let { completedGoalsQuery.whereEqualTo("month", it) }

        // Retrieve completed goals
        completedGoalsQuery.get()
            .addOnSuccessListener { completedGoals ->
                displayFilteredWorkoutGoals(completedGoals, workoutGoals)
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur while retrieving completed goals
                // For example, log the error or display a toast message
                showToast("Failed to retrieve completed goals")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayFilteredWorkoutGoals(completedGoals: QuerySnapshot, allGoals: QuerySnapshot) {
        // Create a list to hold the workout goals
        val workoutGoalsList = mutableListOf<CalendarGoal>()

        // Get the selected year and month from spinners
        val selectedYear = yearSpinner.selectedItem.toString()
        val selectedMonth = monthSpinner.selectedItem.toString()

        // Iterate through all workout goals
        allGoals.forEach { goal ->
            val exercise = goal.getString("exercise") ?: ""
            val dateString = goal.getString("date") ?: ""
            val goalValue = goal.getString("goal") ?: ""

            // Check if the goal matches the selected year and month
            val parts = dateString.split("-")
            if (parts.size == 3 && parts[0] == selectedYear && parts[1] == selectedMonth) {
                // Check if the goal is completed in workoutGoalsCompleted
                val isCompleted = completedGoals.any { completedGoal ->
                    val completedExercise = completedGoal.getString("exercise") ?: ""
                    val completedDate = completedGoal.getString("date") ?: ""
                    completedExercise == exercise && completedDate == dateString
                }
                workoutGoalsList.add(CalendarGoal(exercise, goalValue, dateString, isCompleted))
            }
        }

        // Iterate through completed goals and add them to the list
        completedGoals.forEach { completedGoal ->
            val exercise = completedGoal.getString("exercise") ?: ""
            val dateString = completedGoal.getString("date") ?: ""
            val goalValue = completedGoal.getString("goal") ?: ""

            // Check if the completed goal matches the selected year and month
            val parts = dateString.split("-")
            if (parts.size == 3 && parts[0] == selectedYear && parts[1] == selectedMonth) {
                workoutGoalsList.add(CalendarGoal(exercise, goalValue, dateString, true))
            }
        }

        // Update the adapter with the filtered workout goals
        adapter.submitList(workoutGoalsList)
    }

}
