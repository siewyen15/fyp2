package com.example.fyp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
    private lateinit var viewPostAdapter: ViewPostAdapter
    private var postList: MutableList<ViewPost> = mutableListOf()

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
        viewPostAdapter = ViewPostAdapter(postList) { position ->
            // Handle the share button click here
            val post = postList[position]
            sharePost(post)
        }
        recyclerView.adapter = viewPostAdapter

        // Fetch posts
        fetchPosts()

        val btnLike: Button = findViewById(R.id.btnLike)
        btnLike.setOnClickListener {
            startActivity(Intent(this@ViewPostActivity, LikedPostActivity::class.java))
        }
    }

    private fun fetchPosts() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        val db = FirebaseFirestore.getInstance()
        db.collectionGroup("Posts").get() // Query all posts under "Posts" collection
            .addOnSuccessListener { postDocuments ->
                postDocuments.forEach { postDocument ->
                    val userId = postDocument.getString("userId") // Assuming the userId is stored in the post document
                    val date = postDocument.getString("date") ?: ""
                    val type = postDocument.getString("type") ?: ""
                    val imageUrl = postDocument.getString("imageUrl") ?: ""
                    val description = postDocument.getString("description") ?: ""

                    // Exclude current user's posts
                    if (userId != currentUserId) {
                        val post = ViewPost("", userId ?: "", date, type, imageUrl, description)
                        postList.add(post)
                    }
                }
                viewPostAdapter.notifyDataSetChanged()
                Log.d(TAG, "Fetched ${postDocuments.size()} posts")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting posts: ", exception)
                Toast.makeText(this, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sharePost(post: ViewPost) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this post: ${post.description}")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share post"))
    }

    companion object {
        private const val TAG = "ViewPostActivity"
    }
}
