//package com.example.fyp2
//
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.fyp2.R
//
//class AllPostAdapter(private val postList: List<Post>) : RecyclerView.Adapter{
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
//        return PostAdapter.PostViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: PostAdapter.PostViewHolder, position: Int) {
//        val currentItem = postList[position]
//        holder.bind(currentItem)
//    }
//
//    override fun getItemCount() = postList.size
//
//    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val dateTextView: TextView = itemView.findViewById(R.id.textDate)
//        private val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
//        private val postTitleTextView: TextView = itemView.findViewById(R.id.postTitleTextView)
//        private val postDescriptionTextView: TextView = itemView.findViewById(R.id.postDescriptionTextView)
//        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
//
//        fun bind(post: Post) {
//            dateTextView.text = post.date
//            Glide.with(itemView.context)
//                .load(post.imageUrl)
//                .placeholder(R.drawable.placeholder_image)
//                .into(postImageView)
//            postTitleTextView.text = post.title
//            postDescriptionTextView.text = post.description
//            userNameTextView.text = post.userName
//        }
//    }
//
//}
