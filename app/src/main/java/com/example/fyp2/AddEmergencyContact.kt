// AddEmergencyContact.kt

package com.example.fyp2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityAddEmergencyContactBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEmergencyContact : AppCompatActivity() {

    private lateinit var binding: ActivityAddEmergencyContactBinding
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        binding = ActivityAddEmergencyContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener { saveEmergencyContact() }
    }

    private fun saveEmergencyContact() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val name = binding.nameEditText.text.toString().trim()
            val countryCode = binding.countryCodeSpinner.selectedItem.toString()
            val phoneNumber = binding.phoneNumberEditText.text.toString().trim()

            if (name.isEmpty() || phoneNumber.isEmpty()) {
                // Show error message if any field is empty
                // You can implement this part based on your UI design
                return
            }

            val contact = hashMapOf(
                "name" to name,
                "countryCode" to countryCode,
                "phoneNumber" to phoneNumber
            )

            // Add the contact to the user's collection in Firestore
            firestore.collection("users").document(userId)
                .collection("emergency_contacts")
                .add(contact)
                .addOnSuccessListener { documentReference ->
                    finish() // Finish the activity to return to the previous page
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        } else {
            // Handle case when user is not logged in
        }
    }

}
