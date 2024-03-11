package com.example.fyp2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fyp2.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profilePassword: TextView
    private lateinit var reference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        profileName = rootView.findViewById(R.id.profileName)
        profileEmail = rootView.findViewById(R.id.profileEmail)
        profileUsername = rootView.findViewById(R.id.profileUsername)
        profilePassword = rootView.findViewById(R.id.profilePassword)

        // Get the current user from Firebase Authentication
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            // Get the user ID
            val userId = user.uid

            // Initialize Firebase database reference with the user ID 
            reference = FirebaseDatabase.getInstance().getReference("users").child("-NsP2n_3MjMU3sIlPnUI")

            // Retrieve user data from Firebase and display it
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let {
                            profileName.text = it.name
                            profileEmail.text = it.email
                            profileUsername.text = it.username
                            profilePassword.text = it.password
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        }

        return rootView
    }
}
