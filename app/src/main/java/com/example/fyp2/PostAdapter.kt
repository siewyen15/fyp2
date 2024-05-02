package com.example.fyp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(
    private val postList: MutableList<Post>,
    private val onDeleteClickListener: OnDeleteClickListener,
    private val onShareClickListener: OnShareClickListener
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    // Define the OnDeleteClickListener interface
    interface OnDeleteClickListener {
        fun onDeleteClicked(position: Int)
    }

    interface OnShareClickListener {
        fun onShareClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
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
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        private val btnShare: Button = itemView.findViewById(R.id.btnShare)

        fun bind(post: Post) {
            textDate.text = "Date: ${post.date}"
            textType.text = "Type: ${post.type}"
            textDescription.text = "Description: ${post.description}"
            // Load image using your preferred image loading library
            // For example, using Glide:
            Glide.with(itemView)
                .load(post.imageUrl)
                .into(imagePost)

            btnDelete.setOnClickListener {
                onDeleteClickListener.onDeleteClicked(adapterPosition)
            }

            btnShare.setOnClickListener {
                onShareClickListener.onShareClicked(adapterPosition)
            }

        }
    }
}
