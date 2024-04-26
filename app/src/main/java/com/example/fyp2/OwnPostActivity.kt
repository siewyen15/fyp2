package com.example.fyp2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OwnPostActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var postList: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_own_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        // Fetch posts from Firestore
        fetchPostsFromFirestore()
    }

    private fun fetchPostsFromFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid // Get the current user's ID
            val db = FirebaseFirestore.getInstance()
            val userPostsCollection = db.collection("users")
                .document(user.uid)
                .collection("Posts")

            userPostsCollection.get()
                .addOnSuccessListener { documents ->
                    postList.clear()
                    for (document in documents) {
                        val date = document.getString("date") ?: ""
                        val type = document.getString("type") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val description = document.getString("description") ?: ""
                        val post = Post(userId, date, type, imageUrl, description) // Include userId
                        postList.add(post)
                    }
                    postAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                    Toast.makeText(this, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        private const val TAG = "OwnPostActivity"
    }
}