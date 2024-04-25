package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class NutritionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_nutrition, container, false)

        // Find the ImageButton for calculating BMI
        val calculateBMIButton = view.findViewById<ImageButton>(R.id.calculatebmi)

        // Set click listener for BMI calculator button
        calculateBMIButton.setOnClickListener {
            val intent = Intent(requireContext(), NutritionHomePage::class.java)
            startActivity(intent)
        }
        return view
    }
}
