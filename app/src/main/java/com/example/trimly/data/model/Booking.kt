package com.example.trimly.data.model

data class Booking(    val uid: String = "",
                       val name: String = "",
                       val email: String = "",
                       val roles: List<String> = listOf("customer"), // Default role for everyone
                       val profileImageUrl: String? = null)
