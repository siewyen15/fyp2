// MoodReportActivity.kt
package com.example.fyp2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MoodReportActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_report)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userId = mAuth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("moods")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val moodCounts = mutableMapOf<String, Int>()

                    // Count occurrences of each mood
                    querySnapshot.forEach { documentSnapshot ->
                        val mood = documentSnapshot.getString("mood")
                        mood?.let {
                            moodCounts[it] = moodCounts.getOrDefault(it, 0) + 1
                        }
                    }

                    // Create mood entries for the bar chart
                    val barEntries = mutableListOf<BarEntry>()
                    var index = 0f // Initialize index

                    moodCounts.forEach { (mood, count) ->
                        barEntries.add(BarEntry(index++, count.toFloat())) // Increment index after adding entry
                    }

                    // Create BarDataSet
                    val dataSet = BarDataSet(barEntries, "Mood Distribution")

                    // Create BarData object
                    val data = BarData(dataSet)

                    // Get reference to BarChart
                    val barChart = findViewById<BarChart>(R.id.barChart)
                    barChart.data = data
                    barChart.description.isEnabled = false
                    barChart.invalidate()
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this, "Failed to retrieve mood data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
