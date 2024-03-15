package com.example.fyp2

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompletionData(
    val completionStatus: String // Example property, replace with actual completion data properties
) : Parcelable

class WorkoutDetailsActivity : AppCompatActivity() {

    private lateinit var buttonComplete: Button
    private lateinit var workoutId: String
    private lateinit var workoutType: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        // Initialize views
        val workoutTitle: TextView = findViewById(R.id.workout_title)
        val workoutDuration: TextView = findViewById(R.id.workout_duration)
        val workoutSets: TextView = findViewById(R.id.workout_sets)
        buttonComplete = findViewById(R.id.button_complete)

        // Get workoutId from intent
        workoutId = intent.getStringExtra("workoutId") ?: ""

        // Get current user ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        userId = currentUser?.uid ?: ""

        // Initialize Firestore instance
        val firestore = FirebaseFirestore.getInstance()
        val userWorkoutsRef = firestore.collection("users").document(userId).collection("workouts")

        // Retrieve workout details based on workoutId
        userWorkoutsRef.document(workoutId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val workout = documentSnapshot.toObject(Workout::class.java)
                    workout?.let {
                        // Update UI with retrieved workout details
                        workoutTitle.text = it.workoutName
                        workoutDuration.text = it.duration.toString()
                        workoutSets.text = it.sets.toString()
//                        workoutType = it.type
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        // Button click listener for completion
        buttonComplete.setOnClickListener {
            // User selected completion status as "Completed"
            val completionData = CompletionData("Completed")
            returnResult(completionData)
        }
    }


    private fun returnResult(completionData: CompletionData) {
        val firestore = FirebaseFirestore.getInstance()
        val userWorkoutsRef = firestore.collection("users").document(userId).collection("workouts")
        val workoutRef = userWorkoutsRef.document(workoutId)

        // Update completion status in the workout document
        workoutRef.update("completionStatus", completionData.completionStatus)
            .addOnSuccessListener {
                Log.d(TAG, "Completion status updated successfully")
                // Finish the activity
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating completion status", e)
                // Handle failure here
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

        // Calculate and update total sets for the workout type
        val workoutTypeRef = firestore.collection("workoutTypes").document(workoutType)
        val userWorkoutTypeRef = workoutTypeRef.collection(userId)
        userWorkoutTypeRef.get()
            .addOnSuccessListener { snapshot ->
                var totalSets = 0
                for (doc in snapshot) {
                    val sets = doc.getLong("sets") ?: 0
                    totalSets += sets.toInt()
                }
                workoutTypeRef.update("totalSets", totalSets)
                    .addOnSuccessListener {
                        Log.d(TAG, "Total sets updated successfully for workout type: $workoutType")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating total sets for workout type: $workoutType", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents for workout type: $workoutType", e)
            }
    }
}
