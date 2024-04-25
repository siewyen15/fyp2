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

class ViewPostActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var postList: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_post)
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
        val db = FirebaseFirestore.getInstance()
        val allPostsCollection = db.collection("posts") // Assuming "posts" is the collection name where posts from all users are stored

        allPostsCollection.get()
            .addOnSuccessListener { documents ->
                postList.clear()
                for (document in documents) {
                    val userId = document.getString("userId") ?: ""
                    val date = document.getString("date") ?: ""
                    val type = document.getString("type") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val description = document.getString("description") ?: ""
                    val post = Post(userId, date, type, imageUrl, description)
                    postList.add(post)
                }
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        private const val TAG = "ViewPostActivity"
    }
}
