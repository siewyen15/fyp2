package com.example.fyp2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*

class ShowCalendarActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: String
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_calendar)

        db = FirebaseFirestore.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        showDatePicker()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)

        retrieveWorkoutData()
    }

    private fun retrieveWorkoutData() {
        db.collection("users")
            .document(currentUser)
            .collection("workoutGoalsCompleted")
            .whereEqualTo("date", selectedDate)
            .get()
            .addOnSuccessListener { completedGoals ->
                db.collection("users")
                    .document(currentUser)
                    .collection("workoutGoals")
                    .whereEqualTo("date", selectedDate)
                    .get()
                    .addOnSuccessListener { allGoals ->
                        displayWorkoutGoals(completedGoals, allGoals)
                    }
            }
    }

    private fun displayWorkoutGoals(completedGoals: QuerySnapshot, allGoals: QuerySnapshot) {
        val textView: TextView = findViewById(R.id.textView)
        var workoutGoalsText = "Workout Goals for $selectedDate:\n\n"

        workoutGoalsText += "Completed Goals:\n"
        completedGoals.forEach { completedGoal ->
            val exercise = completedGoal.getString("exercise") ?: ""
            workoutGoalsText += "$exercise - Completed\n"
        }

        workoutGoalsText += "\nUncompleted Goals:\n"
        allGoals.forEach { goal ->
            val exercise = goal.getString("exercise") ?: ""
            if (!completedGoals.any { it.getString("exercise") == exercise }) {
                workoutGoalsText += "$exercise - Uncompleted\n"
            }
        }

        textView.text = workoutGoalsText
    }
}
