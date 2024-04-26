package com.example.fyp2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddJournalActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var spinnerMood: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnAddImage: ImageButton
    private lateinit var textViewDate: TextView
    private lateinit var textViewTime: TextView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var userPostsCollection: CollectionReference
    private lateinit var storage: FirebaseStorage

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_journal)

        // Initialize views
        editTextTitle = findViewById(R.id.editText_title)
        editTextDescription = findViewById(R.id.editText_description)
        spinnerMood = findViewById(R.id.spinner_mood)
        btnSave = findViewById(R.id.btn_save)
        btnAddImage = findViewById(R.id.btn_add_image)
        textViewDate = findViewById(R.id.textView_date)
        textViewTime = findViewById(R.id.textView_time)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()

        // Initialize userPostsCollection
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userPostsCollection = firestore
                .collection("users")
                .document(user.uid)
                .collection("Journal")
        } else {
            // User is not signed in
            // Display an error message and finish the activity
            Toast.makeText(this, "Please sign in to save your journal entry", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set current date and time
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        textViewDate.text = "Date: $currentDate"
        textViewTime.text = "Time: $currentTime"

        // Set click listener for the Save button
        btnSave.setOnClickListener {
            saveJournalEntry()
        }

        // Set click listener for the Add Image button
        btnAddImage.setOnClickListener {
            openImagePicker()
        }

        // Populate Spinner with mood options
        val moodOptions = arrayOf("Happy", "Sad", "Angry", "Excited", "Relaxed")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moodOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMood.adapter = adapter
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveJournalEntry() {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val mood = spinnerMood.selectedItem.toString()
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        // Check if the title is empty
        if (title.isEmpty()) {
            // Show an error message
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if an image is selected
        if (selectedImageUri == null) {
            // Show an error message
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a reference to store the image in Firebase Storage
        val imageName = UUID.randomUUID().toString()
        val storageReference = storage.reference.child("images/$imageName")

        // Upload the image to Firebase Storage
        val uploadTask = storageReference.putFile(selectedImageUri!!)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, get the download URL
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                // Save the download URL to Firestore
                val imageUrl = uri.toString()

                // Create a HashMap to store the journal entry data
                val journalEntry = hashMapOf(
                    "title" to title,
                    "imageUrl" to imageUrl,
                    "description" to description,
                    "mood" to mood,
                    "date" to currentDate,
                    "time" to currentTime
                    // Add more fields if needed
                )

                // Add the journal entry to Firestore
                userPostsCollection.add(journalEntry)
                    .addOnSuccessListener { documentReference ->
                        // Show a success message
                        Toast.makeText(this, "Journal entry saved successfully", Toast.LENGTH_SHORT).show()
                        // Start JournalActivity
                        startActivity(Intent(this, JournalActivity::class.java))
                        // Finish this activity
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Show an error message
                        Toast.makeText(this, "Failed to save journal entry: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { e ->
            // Show an error message
            Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
