package com.example.fyp2

data class AllPost(
    val userId: String,
    val date: String,
    val type: String,
    val imageUrl: String,
    val description: String,
    val userName: String // Add user name field
)
