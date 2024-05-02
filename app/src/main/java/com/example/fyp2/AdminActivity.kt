package com.example.fyp2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize RecyclerView adapter
        adapter = UserAdapter(userList)
        recyclerView.adapter = adapter

        // Fetch user data from Firestore
        fetchUsersFromFirestore()

        // Set item click listener for deletion
        adapter.setOnItemClickListener { position ->
            val user = userList[position]
            deleteUser(user.username)
        }
    }

    private fun fetchUsersFromFirestore() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val username = document.id
                    val isAdmin = document.getBoolean("isAdmin") ?: false
                    val user = User(username, isAdmin)
                    userList.add(user)
                }
                // Notify adapter of data change
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle failure to fetch users
                Log.e(TAG, "Error getting users", exception)
                Toast.makeText(this, "Error getting users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteUser(username: String) {
        // Delete user from Firestore
        db.collection("users").document(username).delete()
            .addOnSuccessListener {
                // User deleted successfully
                Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                // Also, delete user from authentication if needed
                auth.signInWithEmailAndPassword(username, "") // Sign in as the user
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            currentUser?.delete()
                                ?.addOnSuccessListener {
                                    // User deleted from authentication
                                }
                                ?.addOnFailureListener {
                                    // Handle failure to delete user from authentication
                                }
                        } else {
                            // Handle sign-in failure
                        }
                    }
            }
            .addOnFailureListener { exception ->
                // Handle failure to delete user from Firestore
                Log.e(TAG, "Error deleting user", exception)
                Toast.makeText(this, "Error deleting user: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "AdminActivity"
    }
}
