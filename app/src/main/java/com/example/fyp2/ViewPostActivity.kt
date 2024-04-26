package com.example.fyp2

import android.content.ContentValues.TAG
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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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

        // Fetch current user posts
        fetchCurrentUserPosts()

        // Fetch other users' posts
        fetchOtherUsersPosts()
    }

    private fun fetchCurrentUserPosts() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        val db = FirebaseFirestore.getInstance()
        currentUserId?.let { userId ->
            val currentUserPostsCollection = db.collection("users").document(userId).collection("Posts")
            fetchPostsFromCollection(currentUserPostsCollection, userId)
        }
    }


    private fun fetchOtherUsersPosts() {
        Log.d(TAG, "Fetching other users' posts...")
        val db = FirebaseFirestore.getInstance()
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        db.collectionGroup("Posts").get() // Query all posts under "Posts" collection
            .addOnSuccessListener { postDocuments ->
                postDocuments.forEach { postDocument ->
                    val userId = postDocument.getString("userId") // Assuming the userId is stored in the post document
                    if (userId != currentUserUid) {
                        val date = postDocument.getString("date") ?: ""
                        val type = postDocument.getString("type") ?: ""
                        val imageUrl = postDocument.getString("imageUrl") ?: ""
                        val description = postDocument.getString("description") ?: ""
                        val post = Post(userId ?: "", date, type, imageUrl, description)
                        postList.add(post)
                    }
                }
                postAdapter.notifyDataSetChanged()
                Log.d(TAG, "Fetched ${postDocuments.size()} posts for other users")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting other users' posts: ", exception)
                Toast.makeText(this, "Failed to fetch other users' posts", Toast.LENGTH_SHORT).show()
            }
    }



    private fun fetchPostsFromCollection(collection: CollectionReference, userId: String) {
        collection.get()
            .addOnSuccessListener { postDocuments ->
                for (postDocument in postDocuments) {
                    val date = postDocument.getString("date") ?: ""
                    val type = postDocument.getString("type") ?: ""
                    val imageUrl = postDocument.getString("imageUrl") ?: ""
                    val description = postDocument.getString("description") ?: ""
                    val post = Post(userId, date, type, imageUrl, description)
                    postList.add(post)
                }
                postAdapter.notifyDataSetChanged()
                Log.d(TAG, "Fetched ${postDocuments.size()} posts for user $userId")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting posts for user $userId: ", exception)
                Toast.makeText(this, "Failed to fetch posts for user $userId", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        private const val TAG = "ViewPostActivity"
    }
}