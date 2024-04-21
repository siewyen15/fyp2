package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp2.databinding.ActivityWorkoutGoalBinding
import com.example.fyp2.databinding.ItemWorkoutGoalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutGoalBinding
    private lateinit var adapter: WorkoutGoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = WorkoutGoalAdapter(this) { goal ->
            // Handle the "Done" button click action for each workout goal
            onWorkoutGoalComplete(goal)
        }
        binding.recyclerViewGoals.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewGoals.adapter = adapter

        // Retrieve and display the workout goals
        retrieveWorkoutGoals()

        // Set click listener for the "Done" button
        binding.doneButton.setOnClickListener {
            navigateToWorkoutCompleteActivity()
        }
    }

    private fun retrieveWorkoutGoals() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        // Check if the user is authenticated
        user?.let { currentUser ->
            // Query Firestore to retrieve the workout goals
            db.collection("users")
                .document(currentUser.uid)
                .collection("workoutGoals")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val goalsList = mutableListOf<WorkoutGoal>()
                    for (document in querySnapshot.documents) {
                        // Get exercise and goal from each document
                        val exercise = document.getString("exercise") ?: ""
                        val goal = document.getString("goal") ?: ""
                        // Add the goal to the list
                        goalsList.add(WorkoutGoal(document.id, exercise, goal))
                    }
                    // Update the RecyclerView with the retrieved goals
                    adapter.submitList(goalsList)
                }
                .addOnFailureListener { e ->
                    // Show an error message if the retrieval fails
                    Toast.makeText(this, "Error retrieving workout goals: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun onWorkoutGoalComplete(goal: WorkoutGoal) {
        // Remove the completed workout goal from the list
        val currentList = adapter.currentList.toMutableList()
        currentList.remove(goal)
        adapter.submitList(currentList)
    }

    private fun navigateToWorkoutCompleteActivity() {
        val intent = Intent(this, WorkoutCompleteActivity::class.java)
        startActivity(intent)
    }
}


