package com.example.fyp2

import android.content.Intent
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

class OwnPostActivity : AppCompatActivity(), PostAdapter.OnDeleteClickListener, PostAdapter.OnShareClickListener {

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
        postAdapter = PostAdapter(postList, this, this)
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
                        val postId = document.id
                        val date = document.getString("date") ?: ""
                        val type = document.getString("type") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val description = document.getString("description") ?: ""
                        val post = Post(postId, userId, date, type, imageUrl, description) // Include postId
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

    override fun onDeleteClicked(position: Int) {
        val postId = postList[position].postId
        deletePostFromFirestore(postId)
    }

    override fun onShareClicked(position: Int) {
        val post = postList[position]
        sharePost(post)
    }

    private fun deletePostFromFirestore(postId: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userPostsCollection = db.collection("users")
                .document(user.uid)
                .collection("Posts")
            userPostsCollection.document(postId)
                .delete()
                .addOnSuccessListener {
                    // Post deleted successfully
                    Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                    // Remove the post from the local list and update the adapter
                    val position = postList.indexOfFirst { it.postId == postId }
                    if (position != -1) {
                        postList.removeAt(position)
                        postAdapter.notifyItemRemoved(position)
                    }
                }
                .addOnFailureListener { e ->
                    // Failed to delete the post
                    Log.e(TAG, "Error deleting post", e)
                    Toast.makeText(this, "Failed to delete post", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun sharePost(post: Post) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this post: ${post.description}")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share post"))
    }


    companion object {
        private const val TAG = "OwnPostActivity"
    }
}
