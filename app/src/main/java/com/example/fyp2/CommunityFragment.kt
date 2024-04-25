package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth

class CommunityFragment : Fragment() {

    private lateinit var addPostButton: ImageButton
    private lateinit var ownPostButton: ImageButton
    private lateinit var viewPostButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        // Find views
        addPostButton = view.findViewById(R.id.button_add_post)
        ownPostButton = view.findViewById(R.id.button_own_post)
        viewPostButton = view.findViewById(R.id.button_view_post)

        // Set click listeners
        viewPostButton.setOnClickListener {
            // Navigate to ViewPostActivity
            val intent = Intent(requireContext(), ViewPostActivity::class.java)
            startActivity(intent)
        }

        addPostButton.setOnClickListener {
            // Navigate to AddPostActivity
            val intent = Intent(requireContext(), AddPostActivity::class.java)
            startActivity(intent)
        }

        ownPostButton.setOnClickListener {
            // Navigate to OwnPostActivity
            val intent = Intent(requireContext(), OwnPostActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
