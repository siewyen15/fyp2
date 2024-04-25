package com.example.fyp2

data class JournalEntry(
    val title: String,
    val imageUrl: String?, // Nullable if no image
    val description: String,
    val mood: String,
    val date: String,
    val time: String
)
