package com.example.fyp2

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MoodReportActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var yearSpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var showButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_report)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        yearSpinner = findViewById(R.id.yearSpinner)
        monthSpinner = findViewById(R.id.monthSpinner)
        showButton = findViewById(R.id.showButton)

        // Initialize spinners with data
        val years = arrayOf("2022", "2023", "2024") // Change with appropriate years
        val months = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        yearSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        monthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)

        // Set up click listener for the Show button
        showButton.setOnClickListener {
            fetchMoodData()
        }
    }

    private fun fetchMoodData() {
        val selectedYear = yearSpinner.selectedItem.toString()
        val selectedMonth = monthSpinner.selectedItem.toString()

        val userId = mAuth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("moods")
                .whereGreaterThanOrEqualTo("date", "$selectedYear-$selectedMonth-01")
                .whereLessThanOrEqualTo("date", "$selectedYear-$selectedMonth-31")
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

                    // Create mood entries for the pie chart
                    val pieEntries = mutableListOf<PieEntry>()
                    var totalMoodCount = 0

                    moodCounts.forEach { (mood, count) ->
                        totalMoodCount += count
                    }

                    moodCounts.forEach { (mood, count) ->
                        val percentage = (count.toFloat() / totalMoodCount) * 100
                        pieEntries.add(PieEntry(percentage, mood))
                    }

                    val customColors = listOf(
                        Color.rgb(102, 204, 0),   // Green (Happy)
                        Color.rgb(51, 102, 255),  // Dark Blue (Sad)
                        Color.rgb(255, 51, 51),   // Red (Angry)
                        Color.rgb(153, 204, 255), // Light Blue (Calm)
                        Color.rgb(255, 153, 51)   // Orange (Excited)
                    )

                    // Create PieDataSet
                    val dataSet = PieDataSet(pieEntries, "Mood Distribution")
                    dataSet.colors = customColors // Set custom colors for each entry

                    // Create PieData object
                    val data = PieData(dataSet)

                    // Get reference to PieChart
                    val pieChart = findViewById<PieChart>(R.id.pieChart)
                    pieChart.data = data
                    pieChart.description.isEnabled = false
                    pieChart.invalidate()
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this, "Failed to retrieve mood data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


}
