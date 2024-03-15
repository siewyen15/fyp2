package com.example.fyp2

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class WorkoutActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorkoutAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var workoutList: ArrayList<Workout> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView_workouts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = WorkoutAdapter(this, workoutList)
        recyclerView.adapter = adapter

        // Retrieve workout data from Firestore
        retrieveWorkouts()
    }

    // Retrieve workout data from Firestore
    private fun retrieveWorkouts() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            // Retrieve workouts for the current user from Firestore
            firestore.collection("users")
                .document(user.uid)
                .collection("workouts")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val workout = document.toObject(Workout::class.java)
                        workoutList.add(workout)
                    }
                    adapter.notifyDataSetChanged() // Notify adapter about data change
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error retrieving workouts: $e")
                }
        }
    }
}
