package com.example.trimly.data.model

data class Salon(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val imageUrl: String? = null, // We will use this later for the pictures!
    val rating: Double = 0.0
)