package com.example.trimly.data.model

data class Booking(
    val id: String = "",
    val clientId: String = "",
    val barberId: String? = null,
    val salonId: String = "",
    val serviceIds: List<String> = emptyList(),
    val date: String = "",
    val timeSlot: String = "",
    val status: String = "",
    val totalPrice: Double = 0.0,
    val paymentMethod: String = ""
)