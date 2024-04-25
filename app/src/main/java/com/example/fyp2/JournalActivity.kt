package com.example.fyp2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class JournalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userPostsCollection: CollectionReference
    private lateinit var adapter: JournalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        recyclerView = findViewById(R.id.recyclerView)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize userPostsCollection
        val user = FirebaseAuth.getInstance().currentUser
        userPostsCollection = firestore.collection("users").document(user!!.uid).collection("Journal")

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JournalAdapter(mutableListOf())
        recyclerView.adapter = adapter

        // Retrieve data from Firestore and update RecyclerView
        fetchData()
    }

    private fun fetchData() {
        userPostsCollection.get().addOnSuccessListener { documents ->
            val entries = mutableListOf<JournalEntry>()
            for (document in documents) {
                val title = document.getString("title") ?: ""
                val imageUrl = document.getString("imageUrl")
                val description = document.getString("description") ?: ""
                val mood = document.getString("mood") ?: ""
                val date = document.getString("date") ?: ""
                val time = document.getString("time") ?: ""
                entries.add(JournalEntry(title, imageUrl, description, mood, date, time))
            }
            adapter.updateEntries(entries)
        }.addOnFailureListener { exception ->
            // Handle failure
        }
    }

}
