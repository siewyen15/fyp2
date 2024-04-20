package com.example.fyp2

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WaterActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var dailyGoalEditText: EditText
    private lateinit var notificationButton: Button
    private lateinit var consumedAmountEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var percentageTextView: TextView
    private lateinit var notificationTimeTextView: TextView

    private lateinit var notificationManager: NotificationManager
    private val channelID = "water_channel"
    private val NOTIFICATION_ID = 1001
    private val PERMISSION_REQUEST_CODE = 1002

    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        dailyGoalEditText = findViewById(R.id.dailyGoalEditText)
        notificationButton = findViewById(R.id.notificationButton)
        consumedAmountEditText = findViewById(R.id.consumedAmountEditText)
        submitButton = findViewById(R.id.submitButton)
        percentageTextView = findViewById(R.id.percentageTextView)
        notificationTimeTextView = findViewById(R.id.notificationTimeTextView)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        // Load user's water intake data from Firestore and display it
        loadWaterIntakeData()

        // Set onClickListener for setting drinking notifications
        notificationButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Set onClickListener for submitting consumed amount
        submitButton.setOnClickListener {
            // Process and store consumed amount in Firestore
            processConsumedAmount()
        }
    }

    private fun loadWaterIntakeData() {
        val userId = mAuth.currentUser?.uid
        userId?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val dailyGoal = documentSnapshot.getDouble("dailyGoal")
                        val consumedAmount = documentSnapshot.getDouble("consumedAmount")

                        // Update UI with loaded data
                        dailyGoalEditText.setText(dailyGoal?.toString() ?: "")
                        consumedAmountEditText.setText(consumedAmount?.toString() ?: "")

                        // Calculate and display percentage
                        calculateAndDisplayPercentage(dailyGoal, consumedAmount)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }

    private fun processConsumedAmount() {
        val userId = mAuth.currentUser?.uid
        userId?.let { uid ->
            val dailyGoal = dailyGoalEditText.text.toString().toDoubleOrNull()
            val consumedAmount = consumedAmountEditText.text.toString().toDoubleOrNull()

            if (dailyGoal != null && consumedAmount != null) {
                // Update consumed amount and goal in Firestore
                val data = hashMapOf(
                    "dailyGoal" to dailyGoal,
                    "consumedAmount" to consumedAmount
                )

                firestore.collection("users")
                    .document(uid)
                    .set(data)
                    .addOnSuccessListener {
                        // Reload data and update UI
                        loadWaterIntakeData()
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }
            }
        }
    }

    private fun calculateAndDisplayPercentage(dailyGoal: Double?, consumedAmount: Double?) {
        if (dailyGoal != null && consumedAmount != null && dailyGoal > 0) {
            val percentage = (consumedAmount / dailyGoal) * 100
            percentageTextView.text = "Overall percentage: $percentage%"
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearSelected, monthOfYear, dayOfMonthSelected ->
                calendar.set(Calendar.YEAR, yearSelected)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthSelected)
                val timePicker = TimePickerFragment()
                timePicker.show(supportFragmentManager, "time picker")
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    override fun onTimeSet(view: android.widget.TimePicker?, hourOfDay: Int, minute: Int) {
        // Schedule notification at the selected date and time
        scheduleNotification(hourOfDay, minute)

        // Update notification time TextView
        val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        notificationTimeTextView.text = "Selected Notification Time: $selectedTime"
        notificationTimeTextView.visibility = View.VISIBLE // Make it visible
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.water_channel_name)
            val channelDescription = getString(R.string.water_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
        val notificationMessage = "Reminder at $formattedTime to drink water"

        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        // Display notification after setting the reminder
        showNotification(notificationMessage)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(message: String) {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            notificationBuilder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Water Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            // Check if permission is granted
            if (checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, show the notification
                with(NotificationManagerCompat.from(this)) {
                    notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            } else {
                // Permission is not granted, request the permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY), PERMISSION_REQUEST_CODE)
            }
        } else {
            // Handle the case where notifications are disabled
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show the notification
                val notificationManager = NotificationManagerCompat.from(this)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
}
