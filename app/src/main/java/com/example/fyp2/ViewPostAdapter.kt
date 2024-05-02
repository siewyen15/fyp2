package com.example.fyp2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewPostAdapter(
    private val postList: MutableList<ViewPost>,
    private val onShareClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<ViewPostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.textDate)
        private val textType: TextView = itemView.findViewById(R.id.postTitleTextView)
        private val imagePost: ImageView = itemView.findViewById(R.id.postImageView)
        private val textDescription: TextView = itemView.findViewById(R.id.postDescriptionTextView)
        private val btnShare: Button = itemView.findViewById(R.id.btnShare)
        private val btnLike: Button = itemView.findViewById(R.id.btnLike)

        fun bind(post: ViewPost) {
            textDate.text = "Date: ${post.date}"
            textType.text = "Type: ${post.type}"
            textDescription.text = "Description: ${post.description}"
            // Load image using Glide
            Glide.with(itemView)
                .load(post.imageUrl)
                .into(imagePost)

            btnShare.setOnClickListener {
                // Invoke the onShareClickListener when the share button is clicked
                onShareClickListener.invoke(adapterPosition)
            }

            btnLike.setOnClickListener {
                // Toggle like state and update UI accordingly
                val isLiked = !post.isLiked
                post.isLiked = isLiked
                updateLikeButtonUI(isLiked)

                // Show toast message
                val message = if (isLiked) "Liked" else "Unliked"
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()

                // Save liked post to database if needed
                if (isLiked) {
                    saveLikedPost(post)
                }
            }

            // Set initial like button state
            updateLikeButtonUI(post.isLiked)
        }

        private fun updateLikeButtonUI(isLiked: Boolean) {
            btnLike.text = if (isLiked) "Liked" else "Like"
        }

        private fun saveLikedPost(post: ViewPost) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val db = FirebaseFirestore.getInstance()
                val userLikedPostsCollection = db.collection("users")
                    .document(user.uid)
                    .collection("LikedPosts")

                // Add the liked post to the LikedPosts collection
                userLikedPostsCollection.add(post)
                    .addOnSuccessListener {
                        Log.d(TAG, "Liked post saved successfully")
                        Toast.makeText(itemView.context, "Post liked and saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error saving liked post", e)
                        Toast.makeText(itemView.context, "Failed to save liked post", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    companion object {
        private const val TAG = "ViewPostAdapter"
    }
}
