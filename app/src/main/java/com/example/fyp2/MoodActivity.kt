package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MoodActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Find views by their IDs
        val moodSpinner = findViewById<Spinner>(R.id.moodSpinner)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val textViewDate = findViewById<TextView>(R.id.textView_date)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val moodTextView = findViewById<TextView>(R.id.textView_mood)
        val overallReportButton = findViewById<Button>(R.id.overallReportButton)

        // Set up spinner with mood options
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.moods, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moodSpinner.adapter = adapter

        // Set the current date in the TextView
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        textViewDate.text = "Date: $currentDate"

        saveButton.setOnClickListener {
            val selectedMood = moodSpinner.selectedItem.toString()
            val userId = mAuth.currentUser?.uid
            if (userId != null) {
                saveMood(userId, currentDate, selectedMood, moodTextView)
            }
        }

        // CalendarView date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
            )
            textViewDate.text = "Date: $selectedDate"
            displayMoodForDate(selectedDate, moodTextView)
        }

        // Initial display for today's date
        displayMoodForDate(currentDate, moodTextView)

        // Button click listener for overall report
        overallReportButton.setOnClickListener {
            val intent = Intent(this, MoodReportActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveMood(userId: String, date: String, mood: String, moodTextView: TextView) {
        val userData = hashMapOf(
            "mood" to mood
        )
        firestore.collection("users")
            .document(userId)
            .collection("moods")
            .document(date)
            .set(userData)
            .addOnSuccessListener {
                // After successfully saving the mood, display it instantly
                displayMoodForDate(date, moodTextView)
                Toast.makeText(this, "Mood saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save mood", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayMoodForDate(date: String, moodTextView: TextView) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("moods")
                .document(date)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val mood = documentSnapshot.getString("mood")
                        if (mood != null) {
                            // Display mood in the TextView
                            moodTextView.text = "Mood for $date: $mood"
                        } else {
                            moodTextView.text = "No mood recorded for $date"
                        }
                    } else {
                        moodTextView.text = "No mood recorded for $date"
                    }
                }
                .addOnFailureListener {
                    moodTextView.text = "Failed to retrieve mood for $date"
                }
        }
    }
}
