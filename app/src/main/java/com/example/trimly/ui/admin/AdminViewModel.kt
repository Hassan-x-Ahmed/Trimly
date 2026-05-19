// sid: 15932
package com.example.trimly.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 1. Core Salon Data
    private val _salonName = MutableLiveData<String>()
    val salonName: LiveData<String> = _salonName

    private val _salonAddress = MutableLiveData<String>()
    val salonAddress: LiveData<String> = _salonAddress

    private val _salonHours = MutableLiveData<String>()
    val salonHours: LiveData<String> = _salonHours

    private val _mySalonId = MutableLiveData<String>()

    // 2. Lists & Stats
    // Assuming you have data classes for Barber and Service
    private val _barbersList = MutableLiveData<List<Any>>()
    val barbersList: LiveData<List<Any>> = _barbersList

    private val _servicesList = MutableLiveData<List<Any>>()
    val servicesList: LiveData<List<Any>> = _servicesList

    private val _activeChairCount = MutableLiveData<Int>(0)
    val activeChairCount: LiveData<Int> = _activeChairCount

    // --- INITIALIZATION ---

    init {
        fetchSalonData()
    }

    private fun fetchSalonData() {
        val adminId = auth.currentUser?.uid ?: return

        // Step A: Find the Salon owned by this Admin
        db.collection("salons").whereEqualTo("ownerId", adminId).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val salonDoc = documents.documents[0]
                    _mySalonId.value = salonDoc.id

                    _salonName.value = salonDoc.getString("name") ?: ""
                    _salonAddress.value = salonDoc.getString("address") ?: ""
                    _salonHours.value = salonDoc.getString("hours") ?: ""

                    // Step B: Fetch the Barbers linked to this salon
                    fetchBarbersForSalon(salonDoc.id)
                }
            }
            .addOnFailureListener { e ->
                Log.e("AdminViewModel", "Error fetching salon", e)
            }
    }

    private fun fetchBarbersForSalon(salonId: String) {
        // Query users collection for barbers who have this salonId
        db.collection("users")
            .whereEqualTo("role", "barber")
            .whereEqualTo("salonId", salonId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Map snapshot to your Barber data class here
                    // val barbers = snapshot.documents.map { it.toObject(Barber::class.java) }
                    // _barbersList.value = barbers
                    _activeChairCount.value = snapshot.size()
                }
            }
    }

    // --- ACTIONS ---

    /**
     * Checks if an email belongs to a registered barber.
     * If yes, links them to the Admin's salon.
     */
    fun addBarberByEmail(email: String, onResult: (Boolean, String) -> Unit) {
        val currentSalonId = _mySalonId.value
        if (currentSalonId == null) {
            onResult(false, "Salon data not loaded yet.")
            return
        }

        // 1. Look for the Barber by email
        db.collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("role", "barber")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onResult(false, "No registered barber found with this email.")
                } else {
                    // 2. Found them! Update their profile to link to this salon
                    val barberDocId = documents.documents[0].id
                    db.collection("users").document(barberDocId)
                        .update("salonId", currentSalonId)
                        .addOnSuccessListener {
                            onResult(true, "Barber successfully added to your salon!")
                        }
                        .addOnFailureListener {
                            onResult(false, "Failed to link barber.")
                        }
                }
            }
            .addOnFailureListener {
                onResult(false, "Database error occurred.")
            }
    }

    fun logout() {
        auth.signOut()
    }
}