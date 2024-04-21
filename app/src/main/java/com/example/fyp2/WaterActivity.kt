package com.example.fyp2

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fyp2.databinding.ActivityWaterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WaterActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var binding: ActivityWaterBinding

    private lateinit var notificationManager: NotificationManager
    private val channelID = "water_channel"
    private val NOTIFICATION_ID = 1001

    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.apply {
            notificationButton.setOnClickListener {
                showTimePickerDialog()
            }

            submitButton.setOnClickListener {
                processConsumedAmount()
            }

            overallReportButton.setOnClickListener {
                showOverallReport()
            }
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        notificationBuilder = NotificationCompat.Builder(this, channelID)

        loadWaterIntakeData()
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

                        binding.dailyGoalEditText.setText(dailyGoal?.toString() ?: "")
                        binding.consumedAmountEditText.setText(consumedAmount?.toString() ?: "")

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
            val dailyGoal = binding.dailyGoalEditText.text.toString().toDoubleOrNull()
            val consumedAmount = binding.consumedAmountEditText.text.toString().toDoubleOrNull()

            if (dailyGoal == null || consumedAmount == null || dailyGoal <= 0) {
                showMessage("Please enter valid daily goal and consumed amount!")
                return
            }

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val waterData = hashMapOf(
                "date" to currentDate,
                "consumedAmount" to consumedAmount
            )

            val percentage = (consumedAmount / dailyGoal) * 100

            // Display the calculated percentage
            binding.percentageTextView.text = "Overall percentage: ${String.format("%.2f", percentage)}%"

            if (consumedAmount > dailyGoal) {
                showMessage("You have successfully reached your goal today!")
            }

            firestore.collection("users")
                .document(uid)
                .collection("water")
                .document(currentDate)
                .set(waterData)
                .addOnSuccessListener {
                    showMessage("Water intake data saved successfully!")
                    // Create a Calendar instance for the current time
                    val calendar = Calendar.getInstance()
                    // Schedule notification with the current time
                    scheduleNotification(calendar)
                    // If the user has specified a reminder interval, schedule a repeating notification
                    val reminderIntervalHours = binding.notificationHourEditText.text.toString().toLongOrNull()
                    reminderIntervalHours?.let {
                        if (it > 0) {
                            scheduleNotification(calendar, AlarmManager.INTERVAL_HOUR * it)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    showMessage("Failed to save water intake data!")
                    // Handle failure
                }
        }
    }



    private fun calculateAndDisplayPercentage(dailyGoal: Double?, consumedAmount: Double?) {
        if (dailyGoal != null && consumedAmount != null && dailyGoal > 0) {
            val percentage = (consumedAmount / dailyGoal) * 100
            binding.percentageTextView.text = "Overall percentage: $percentage%"

            if (consumedAmount > dailyGoal) {
                showMessage("You have successfully reached your goal today!")
            }
        }
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            this,
            0,
            0,
            true
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: android.widget.TimePicker?, hourOfDay: Int, minute: Int) {
        // Save selected notification time
        val selectedHour = binding.notificationHourEditText.text.toString().toInt()
        val selectedMinute = minute
        val calendar = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
        }

        // Schedule notification
        scheduleNotification(calendar)

        val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        binding.notificationTimeTextView.text = "Selected Notification Time: $selectedTime"
        binding.notificationTimeTextView.visibility = View.VISIBLE
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

    private fun scheduleNotification(calendar: Calendar, repeatInterval: Long = 0) {
        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
        val notificationMessage = "Reminder at $formattedTime to drink water"

        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // If repeatInterval is provided, set a repeating alarm, otherwise set a one-time alarm
        if (repeatInterval > 0) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                repeatInterval,
                pendingIntent
            )
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

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

            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_ID, notificationBuilder.build())
            }
        }
    }

    private fun showMessage(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showOverallReport() {
        // Navigate to the report page
        startActivity(Intent(this, WaterReportActivity::class.java))
    }
}
