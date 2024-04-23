package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp2.databinding.ActivityWorkoutGoalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        // Set click listener for the "Add Goal" button
        binding.addGoalButton.setOnClickListener {
            navigateToAddWorkoutGoalActivity()
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
                        // Get exercise, goal, and date from each document
                        val exercise = document.getString("exercise") ?: ""
                        val goal = document.getString("goal") ?: ""
                        val date = document.getString("date") ?: ""
                        // Add the goal to the list
                        goalsList.add(WorkoutGoal(document.id, exercise, goal, date.toString()))
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
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        user?.let { currentUser ->
            // Retrieve the original date from the goal
            val originalDate = goal.date

            // Move the completed workout goal to the "workoutGoalsCompleted" collection
            val updatedGoal = goal.copy(date = originalDate) // Use the original date
            db.collection("users")
                .document(currentUser.uid)
                .collection("workoutGoalsCompleted")
                .add(updatedGoal) // Add the updated goal
                .addOnSuccessListener { documentReference ->
                    // If addition to "workoutGoalsCompleted" collection succeeds, delete the goal from "workoutGoals"
                    db.collection("users")
                        .document(currentUser.uid)
                        .collection("workoutGoals")
                        .document(goal.id)
                        .delete()
                        .addOnSuccessListener {
                            // If deletion from "workoutGoals" collection succeeds, update the UI
                            val currentList = adapter.currentList.toMutableList()
                            currentList.remove(goal)
                            adapter.submitList(currentList)

                            // Pass all data to workoutGoalsComplete collection
                            db.collection("users")
                                .document(currentUser.uid)
                                .collection("workoutGoalsComplete")
                                .add(updatedGoal) // Add the updated goal to workoutGoalsComplete
                                .addOnSuccessListener {
                                    // Successfully added to workoutGoalsComplete
                                }
                                .addOnFailureListener { e ->
                                    // If addition to "workoutGoalsComplete" collection fails, show an error message
                                    Toast.makeText(this, "Error adding to workoutGoalsComplete: $e", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            // If deletion from "workoutGoals" collection fails, show an error message
                            Toast.makeText(this, "Error deleting workout goal: $e", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    // If addition to "workoutGoalsCompleted" collection fails, show an error message
                    Toast.makeText(this, "Error completing workout goal: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }



    private fun navigateToAddWorkoutGoalActivity() {
        val intent = Intent(this, AddWorkoutGoalActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToWorkoutCompleteActivity() {
        val intent = Intent(this, WorkoutCompleteActivity::class.java)
        startActivity(intent)
    }
}
