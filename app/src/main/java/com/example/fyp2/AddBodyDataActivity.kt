package com.example.fyp2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddBodyDataActivity : AppCompatActivity() {

    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var saveButton: Button
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_body_data)

        weightEditText = findViewById(R.id.weightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        ageEditText = findViewById(R.id.ageEditText)
        genderEditText = findViewById(R.id.genderEditText)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            saveBodyData()
        }
    }

    private fun saveBodyData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val weight = weightEditText.text.toString().toDoubleOrNull()
        val height = heightEditText.text.toString().toDoubleOrNull()
        val age = ageEditText.text.toString().toIntOrNull()
        val gender = genderEditText.text.toString()

        // Check if all fields are valid
        if (userId != null && weight != null && height != null && age != null && gender.isNotEmpty()) {
            // Create a unique document ID for the user's body data
            val bodyDataRef = firestore.collection("users").document(userId)
                .collection("body_data").document("latest")

            // Save body data to Firestore
            val bodyData = hashMapOf(
                "weight" to weight,
                "height" to height,
                "age" to age,
                "gender" to gender
            )

            bodyDataRef.set(bodyData)
                .addOnSuccessListener {
                    // Data saved successfully
                    Toast.makeText(this, "Body data saved successfully", Toast.LENGTH_SHORT).show()
                    // Optionally, you can navigate to another activity or finish this activity
                }
                .addOnFailureListener { e ->
                    // Error saving data
                    Toast.makeText(this, "Failed to save body data", Toast.LENGTH_SHORT).show()
                    // Optionally, you can show an error message to the user or log the error
                }
        } else {
            // Invalid input
            Toast.makeText(this, "Invalid input. Please fill in all fields.", Toast.LENGTH_SHORT).show()
            // Optionally, you can show an error message to the user
        }
    }
}
