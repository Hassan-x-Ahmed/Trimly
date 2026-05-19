// sid: 15932
package com.example.trimly.ui.auth

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    // The "Memory Bank" for Admin Salon Setup
    var salonName: String = ""
    var salonAddress: String = ""
    var salonPhone: String = ""
    var salonHours: String = ""

    // The "Memory Bank" for Barber Setup
    var barberName: String = ""
    var barberExperience: String = ""

    // Role selected during onboarding
    var selectedRole: String = "client"
}