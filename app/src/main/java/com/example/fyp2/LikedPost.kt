package com.example.fyp2

data class LikedPost(
    val postId: String,
    val userId: String,
    val date: String,
    val type: String,
    val imageUrl: String,
    val description: String
)
