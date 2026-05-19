// sid: 15932
package com.example.trimly.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trimly.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class SalonService(val id: String = "", val name: String = "", val price: Int = 0)

class AdminViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val currentAdminId = auth.currentUser?.uid ?: ""

    val salonName = MutableLiveData<String>("Trimly Studio")
    val salonAddress = MutableLiveData<String>("123 Main Boulevard, DHA Phase 5")
    val salonPhone = MutableLiveData<String>("+92 300 1234567")
    val salonHours = MutableLiveData<String>("10:00 AM - 10:00 PM")
    val activeChairCount = MutableLiveData<Int>(8)
    val todayRevenue = MutableLiveData<Double>(18560.0)

    private val _barbersList = MutableLiveData<List<User>>()
    val barbersList: LiveData<List<User>> = _barbersList

    private val _servicesList = MutableLiveData<List<SalonService>>()
    val servicesList: LiveData<List<SalonService>> = _servicesList

    init {
        startRealtimeBarberListener()
        startRealtimeServiceListener()
    }

    private fun startRealtimeBarberListener() {
        firestore.collection("users")
            .whereEqualTo("role", "barber")
            .whereEqualTo("adminId", currentAdminId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    _barbersList.postValue(it.documents.mapNotNull { doc -> doc.toObject(User::class.java) })
                }
            }
    }

    private fun startRealtimeServiceListener() {
        if(currentAdminId.isEmpty()) return
        firestore.collection("salons").document(currentAdminId).collection("services")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                snapshot?.let {
                    val list = it.documents.map { doc ->
                        SalonService(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            price = doc.getLong("price")?.toInt() ?: 0
                        )
                    }
                    _servicesList.postValue(list)
                }
            }
    }

    fun addOrUpdateService(service: SalonService, onResult: (Boolean) -> Unit) {
        val docRef = if (service.id.isEmpty()) {
            firestore.collection("salons").document(currentAdminId).collection("services").document()
        } else {
            firestore.collection("salons").document(currentAdminId).collection("services").document(service.id)
        }

        val data = mapOf(
            "name" to service.name,
            "price" to service.price
        )

        docRef.set(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteService(serviceId: String, onResult: (Boolean) -> Unit) {
        firestore.collection("salons").document(currentAdminId).collection("services").document(serviceId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun addBarberByEmail(email: String, onResult: (Boolean, String) -> Unit) {
        firestore.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onResult(false, "No user found.")
                } else {
                    documents.documents[0].reference.update(mapOf("role" to "barber", "adminId" to currentAdminId))
                        .addOnSuccessListener { onResult(true, "Barber invited!") }
                }
            }
    }

    fun logout() = auth.signOut()
}