package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fyp2.databinding.ActivityMentalWellnessBinding

class MentalWellnessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMentalWellnessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMentalWellnessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupViews()
    }

    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.yogaButton.setOnClickListener {
            val intent = Intent(this, YogaActivity::class.java)
            startActivity(intent)
        }

        binding.meditationButton.setOnClickListener {
            val intent = Intent(this, MeditationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun enableEdgeToEdge() {
        // Enable edge-to-edge display
        // You can add your edge-to-edge code here if needed
    }
}
