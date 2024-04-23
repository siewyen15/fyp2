package com.example.fyp2

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WorkoutCompleteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutGoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_complete)

        recyclerView = findViewById(R.id.recyclerViewCompletedGoals)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = WorkoutGoalAdapter(this) { goal ->
            onDeleteButtonClick(goal)
        }
        recyclerView.adapter = adapter

        val activityTypes = arrayOf("All", "Push-ups", "Sit-ups", "Squats", "Plank")

        val spinnerExercise: Spinner = findViewById(R.id.spinnerExercise)
        ArrayAdapter.createFromResource(
            this,
            R.array.exercise_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerExercise.adapter = adapter
        }

        spinnerExercise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedExercise = activityTypes[position]
                retrieveAndDisplayWorkoutGoals(selectedExercise)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun retrieveAndDisplayWorkoutGoals(selectedExercise: String) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        user?.let { currentUser ->
            db.collection("users")
                .document(currentUser.uid)
                .collection("workoutGoalsCompleted")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val goalsList = mutableListOf<WorkoutGoal>()
                    for (document in querySnapshot.documents) {
                        val exercise = document.getString("exercise") ?: ""
                        val goal = document.getString("goal") ?: ""
                        val date = document.getString("date") ?: "" // Retrieve date as a string
                        if (selectedExercise == "All" || exercise == selectedExercise) {
                            goalsList.add(WorkoutGoal(document.id, exercise, goal, date))
                        }
                    }
                    adapter.submitList(goalsList.sortedByDescending { it.date })
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error retrieving workout goals: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun onDeleteButtonClick(goal: WorkoutGoal) {
        adapter.currentList.toMutableList().remove(goal)
        adapter.notifyDataSetChanged()

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        user?.let { currentUser ->
            db.collection("users")
                .document(currentUser.uid)
                .collection("workoutGoalsCompleted")
                .document(goal.id)
                .delete()
                .addOnSuccessListener {
                    // Handle success if needed
                }
                .addOnFailureListener { e ->
                    // Handle failure if needed
                }
        }
    }
}
