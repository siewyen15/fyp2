package com.example.fyp2

data class ViewPost(
    val postId: String,
    val userId: String,
    val date: String,
    val type: String,
    val imageUrl: String,
    val description: String,
    var isLiked: Boolean = false // Add isLiked property with default value false
)
