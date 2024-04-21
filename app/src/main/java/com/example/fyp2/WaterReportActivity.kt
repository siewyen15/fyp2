package com.example.fyp2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WaterReportActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_report)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize your bar chart
        val barChart: BarChart = findViewById(R.id.barChart)

        // Fetch and display weekly water consumption data for the current month
        fetchWeeklyWaterConsumption(barChart)
    }

    private fun fetchWeeklyWaterConsumption(barChart: BarChart) {
        val userId = mAuth.currentUser?.uid
        userId?.let { uid ->
            val currentCalendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val currentMonthYear = formatter.format(currentCalendar.time)

            // Query to fetch monthly water consumption data
            firestore.collection("users")
                .document(uid)
                .collection("water")
                .whereGreaterThanOrEqualTo("date", "$currentMonthYear-01")
                .whereLessThanOrEqualTo("date", "$currentMonthYear-31")
                .get()
                .addOnSuccessListener { documents ->
                    val dailyData = mutableMapOf<String, Float>()

                    // Initialize daily consumption data
                    val numDaysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    for (day in 1..numDaysInMonth) {
                        val date = "$currentMonthYear-${String.format("%02d", day)}"
                        dailyData[date] = 0f
                    }

                    // Calculate daily consumption
                    for (document in documents) {
                        val date = document.getString("date") ?: ""
                        val consumedAmount = document.getDouble("consumedAmount")?.toFloat() ?: 0f
                        if (dailyData.containsKey(date)) {
                            dailyData[date] = dailyData[date]?.plus(consumedAmount) ?: 0f
                        }
                    }

                    // Create entries for the bar chart
                    val entries = mutableListOf<BarEntry>()
                    dailyData.forEach { (date, amount) ->
                        entries.add(BarEntry(date.substring(8, 10).toFloat(), amount))
                    }


                    if (entries.isNotEmpty()) {
                        // Create a dataset from the entries
                        val dataSet = BarDataSet(entries, "Water Consumption")
                        val data = BarData(dataSet)

                        // Customize your chart as needed
                        barChart.data = data
                        barChart.setFitBars(true)

                        // Customize X-axis labels
                        val xAxis: XAxis = barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(dailyData.keys.toList())
                        xAxis.labelRotationAngle = -45f // Rotate labels for better readability

                        barChart.invalidate() // Refresh the chart
                    } else {
                        // Handle case when no data is available
                        // For example, show a message to the user
                        // Or hide the chart
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
}
