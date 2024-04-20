package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class HealthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_health, container, false)

        // Find buttons by their IDs
        val physicalHealthButton = view.findViewById<ImageButton>(R.id.button_physical_health)
        val mentalHealthButton = view.findViewById<ImageButton>(R.id.button_mental_health)

        // Set click listeners for the buttons
        physicalHealthButton.setOnClickListener {
            // Handle physical health button click
            val intent = Intent(requireContext(), WorkoutDashboardActivity::class.java)
            startActivity(intent)
        }

        mentalHealthButton.setOnClickListener {
            // Handle mental health button click
            val intent = Intent(requireContext(), MentalHealthActivity::class.java)
            startActivity(intent)
        }


        return view
    }
}
