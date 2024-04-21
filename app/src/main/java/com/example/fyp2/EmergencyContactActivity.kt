// EmergencyContactActivity.kt

package com.example.fyp2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityEmergencyContactBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmergencyContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmergencyContactBinding
    private lateinit var emergencyContacts: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        val searchEditText = binding.searchEditText
        val emergencyContactListView = binding.emergencyContactListView
        val addEmergencyContactButton = binding.addEmergencyContactButton

        // Initialize emergency contacts list
        emergencyContacts = ArrayList()

        // Populate the list view
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emergencyContacts)
        emergencyContactListView.adapter = adapter

        // Search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle button click to add emergency contact
        addEmergencyContactButton.setOnClickListener {
            startActivity(Intent(this, AddEmergencyContact::class.java))
        }

        // Handle item click to initiate phone call
        emergencyContactListView.setOnItemClickListener { _, _, position, _ ->
            val selectedContact = emergencyContacts[position]
            val phoneNumber = extractPhoneNumber(selectedContact)
            phoneNumber?.let {
                val countryCode = extractCountryCode(selectedContact)
                val phoneNumberWithCountryCode = countryCode + it
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumberWithCountryCode")
                }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshEmergencyContactsList()
    }

    private fun refreshEmergencyContactsList() {
        // Fetch emergency contacts from Firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .collection("emergency_contacts")
                .get()
                .addOnSuccessListener { result ->
                    emergencyContacts.clear()
                    for (document in result) {
                        val countryCode = document.getString("countryCode") ?: ""
                        val phoneNumber = document.getString("phoneNumber") ?: ""
                        emergencyContacts.add("$countryCode - $phoneNumber")
                    }
                    // Add Police and Ambulance numbers
                    emergencyContacts.add("Police: 999")
                    emergencyContacts.add("Ambulance: 110")

                    // Notify the adapter that the dataset has changed
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }

    private fun extractPhoneNumber(contact: String): String? {
        val parts = contact.split(" - ")
        return if (parts.size >= 3) {
            parts[2]
        } else {
            null
        }
    }

    private fun extractCountryCode(contact: String): String {
        val parts = contact.split(" - ")
        return if (parts.size >= 2) {
            parts[1]
        } else {
            ""
        }
    }
}
