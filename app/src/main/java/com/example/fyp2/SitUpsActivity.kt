package com.example.fyp2

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView


class SitUpsActivity : AppCompatActivity() {

    private lateinit var spinnerDuration: Spinner
    private lateinit var btnStart: Button
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var textViewTimer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sit_ups)

        // Initialize views
        spinnerDuration = findViewById(R.id.spinner_duration)
        btnStart = findViewById(R.id.btn_start)
        textViewTimer = findViewById(R.id.textViewTimer) // Initialize textViewTimer

        // Set up spinner with multiples of 5 for minutes
        val minutesList = arrayListOf<String>()
        for (i in 1..12) { // Assuming a maximum of 60 minutes
            minutesList.add((i * 5).toString())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minutesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDuration.adapter = adapter

        // Set click listener for the start button
        btnStart.setOnClickListener {
            startWorkout()
        }
    }

    private fun startWorkout() {
        val selectedMinutes = (spinnerDuration.selectedItem as String).toLong() * 60000 // Convert minutes to milliseconds
        countDownTimer = object : CountDownTimer(selectedMinutes, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Calculate remaining minutes and seconds
                val remainingMinutes = millisUntilFinished / 60000
                val remainingSeconds = (millisUntilFinished % 60000) / 1000

                // Format the time remaining
                val formattedTime = String.format("%02d:%02d", remainingMinutes, remainingSeconds)

                // Update UI with the formatted time
                textViewTimer.text = formattedTime
            }

            override fun onFinish() {
                showCongratulationsDialog()
            }
        }
        countDownTimer.start()
    }


    private fun showCongratulationsDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Congratulations!")
            .setMessage("You have completed your workout.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // You can perform any additional actions here if needed
            }
            .create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}
