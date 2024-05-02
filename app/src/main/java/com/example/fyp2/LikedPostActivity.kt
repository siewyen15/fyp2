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

class LikedPostActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var likedPostAdapter: LikedPostAdapter
    private var likedPostList: MutableList<ViewPost> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_liked_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        likedPostAdapter = LikedPostAdapter(likedPostList)
        recyclerView.adapter = likedPostAdapter

        // Fetch liked posts
        fetchLikedPosts()
    }

    private fun fetchLikedPosts() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        if (currentUserId != null) {
            val db = FirebaseFirestore.getInstance()
            val userLikedPostsCollection = db.collection("users")
                .document(currentUserId)
                .collection("LikedPosts")

            userLikedPostsCollection.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val postId = document.getString("postId") ?: ""
                        val userId = document.getString("userId") ?: ""
                        val date = document.getString("date") ?: ""
                        val type = document.getString("type") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val description = document.getString("description") ?: ""
                        val likedPost = ViewPost(postId, userId, date, type, imageUrl, description)
                        likedPostList.add(likedPost)
                    }
                    likedPostAdapter.notifyDataSetChanged()
                    Log.d(TAG, "Fetched ${documents.size()} liked posts")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching liked posts", exception)
                    Toast.makeText(this, "Failed to fetch liked posts", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        private const val TAG = "LikedPostActivity"
    }
}
