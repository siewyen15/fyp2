package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BodyDataActivity : AppCompatActivity() {
    private lateinit var weightTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var genderTextView: TextView
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_data)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize views
        weightTextView = findViewById(R.id.weightTextView)
        heightTextView = findViewById(R.id.heightTextView)
        ageTextView = findViewById(R.id.ageTextView)
        genderTextView = findViewById(R.id.genderTextView)

        // Retrieve and display body data
        retrieveAndDisplayBodyData()

        // Find the button by ID
        val addButton = findViewById<ImageButton>(R.id.btn_add_journal)

        // Set OnClickListener to handle clicks
        addButton.setOnClickListener {
            // Navigate to the activity to add a new journal entry
            val intent = Intent(this, AddBodyDataActivity::class.java)
            startActivity(intent)
        }

    }

    private fun retrieveAndDisplayBodyData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Retrieve body data from Firestore
            firestore.collection("users").document(userId)
                .collection("body_data").document("latest")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Populate views with retrieved data
                        val weight = document.getDouble("weight") ?: 0.0
                        val height = document.getDouble("height") ?: 0.0
                        val age = document.getLong("age") ?: 0
                        val gender = document.getString("gender") ?: ""

                        weightTextView.text = "Weight: $weight kg"
                        heightTextView.text = "Height: $height cm"
                        ageTextView.text = "Age: $age years"
                        genderTextView.text = "Gender: $gender"
                    } else {
                        // Document doesn't exist
                        // Handle the case when no body data is available
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
}
