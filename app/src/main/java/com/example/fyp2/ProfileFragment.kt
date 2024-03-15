package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profilePassword: TextView
    private var currentUser: FirebaseUser? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        profileName = rootView.findViewById(R.id.profileName)
        profileEmail = rootView.findViewById(R.id.profileEmail)
        profileUsername = rootView.findViewById(R.id.profileUsername)
        profilePassword = rootView.findViewById(R.id.profilePassword)

        // Get the current user from Firebase Authentication
        currentUser = FirebaseAuth.getInstance().currentUser

        // Initialize Firestore reference
        db = FirebaseFirestore.getInstance()

        // Initialize logout button
        val logoutButton = rootView.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Sign out the user
            FirebaseAuth.getInstance().signOut()

            // Redirect to the login activity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            // Finish the current activity to prevent the user from returning to it
            requireActivity().finish()
        }

        currentUser?.let { user ->
            // Get the user ID
            val userId = user.uid

            // Retrieve user data from Firestore and display it
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userProfile = document.toObject(UserProfile::class.java)
                        userProfile?.let {
                            profileName.text = it.name
                            profileEmail.text = it.email
                            profileUsername.text = it.username
                            profilePassword.text = it.password
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failures
                }
        }

        return rootView
    }
}
