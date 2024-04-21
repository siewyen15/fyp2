package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp2.databinding.ActivityYogaBinding

class YogaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYogaBinding
    private lateinit var countDownTimer: CountDownTimer

    private val yogaPostureImages = listOf(
        R.drawable.yoga1,
        R.drawable.yoga2,
        R.drawable.yoga3
        // Add more images as needed
    )
    private var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYogaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            startButton.setOnClickListener {
                startCountdownAndTimer()
            }

            stopButton.setOnClickListener {
                resetTimer()
            }
        }
    }

    private fun startCountdownAndTimer() {
        binding.apply {
            // Hide start button, description, and stop button
            startButton.visibility = View.GONE
            descriptionTextView.visibility = View.GONE
            stopButton.visibility = View.VISIBLE

            // Show countdown image and hide other images
            countdownImageView.visibility = View.VISIBLE
            yogaImageView.visibility = View.GONE
            restImageView.visibility = View.GONE

            // Display countdown message "3, 2, 1 start"
            timerTextView.text = "3"
            timerTextView.postDelayed({
                timerTextView.text = "2"
                timerTextView.postDelayed({
                    timerTextView.text = "1"
                    timerTextView.postDelayed({
                        // Start the timer after countdown
                        timerTextView.text = ""
                        startYogaTimer()
                    }, 1000)
                }, 1000)
            }, 1000)
        }
    }

    private fun startYogaTimer() {
        binding.apply {
            // Hide countdown image and show yoga image
            countdownImageView.visibility = View.GONE
            yogaImageView.visibility = View.VISIBLE

            // Set the first yoga image
            yogaImageView.setImageResource(yogaPostureImages[currentImageIndex])

            // Set initial timer text
            timerTextView.text = "10:00"

            // Start the timer for 10 minutes
            countDownTimer = object : CountDownTimer(10 * 60 * 1000, 1000) { // 10 minutes with 1-second interval
                override fun onTick(millisUntilFinished: Long) {
                    // Update timer text
                    val minutes = millisUntilFinished / (60 * 1000)
                    val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                    timerTextView.text = String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    // Yoga posture display finished, show rest message
                    timerTextView.text = "Rest for 5:00"
                    // Start a 5-minute timer for rest
                    startRestTimer()
                }
            }.start()
        }
    }

    private fun startRestTimer() {
        binding.apply {
            // Show rest image and hide yoga image
            restImageView.visibility = View.VISIBLE
            yogaImageView.visibility = View.GONE

            // Start the timer for 5 minutes
            countDownTimer = object : CountDownTimer(5 * 60 * 1000, 1000) { // 5 minutes with 1-second interval
                override fun onTick(millisUntilFinished: Long) {
                    // Update timer text
                    val minutes = millisUntilFinished / (60 * 1000)
                    val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                    timerTextView.text = String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    // Rest time finished, display next yoga posture
                    currentImageIndex++
                    if (currentImageIndex < yogaPostureImages.size) {
                        startYogaTimer()
                    } else {
                        // All yoga postures displayed, show congratulations message
                        timerTextView.text = "Congratulations!"
                        // Return to Yoga activity after a delay
                        stopButton.postDelayed({
                            startActivity(Intent(this@YogaActivity, YogaActivity::class.java))
                            finish()
                        }, 3000) // Delay for 3 seconds before returning to Yoga activity
                    }
                }
            }.start()
        }
    }

    private fun resetTimer() {
        binding.apply {
            // Cancel the timer
            countDownTimer.cancel()
            // Hide countdown and rest images
            countdownImageView.visibility = View.GONE
            restImageView.visibility = View.GONE
            // Reset currentImageIndex to start from the beginning
            currentImageIndex = 0
            // Set initial timer text to 10 minutes
            timerTextView.text = "10:00"
            // Show start button and description again
            startButton.visibility = View.VISIBLE
            descriptionTextView.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the timer when the activity is destroyed to avoid memory leaks
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}
