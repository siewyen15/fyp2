package com.example.fyp2


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LikedPostAdapter(private val likedPostList: List<ViewPost>) : RecyclerView.Adapter<LikedPostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_liked_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val likedPost = likedPostList[position]
        holder.bind(likedPost)
    }

    override fun getItemCount(): Int {
        return likedPostList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.textDate)
        private val textType: TextView = itemView.findViewById(R.id.postTitleTextView)
        private val imagePost: ImageView = itemView.findViewById(R.id.postImageView)
        private val textDescription: TextView = itemView.findViewById(R.id.postDescriptionTextView)

        fun bind(likedPost: ViewPost) {
            textDate.text = "Date: ${likedPost.date}"
            textType.text = "Type: ${likedPost.type}"
            // Load image using Glide or your preferred image loading library
            Glide.with(itemView)
                .load(likedPost.imageUrl)
                .into(imagePost)
        }
    }
}
