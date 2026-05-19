// sid: 15932
package com.example.trimly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminActivity
import com.example.trimly.ui.barber.BarberActivity
import com.example.trimly.ui.client.ClientActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SetupSuccessFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Give the user 2 seconds to see the "Success" screen
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                routeUserToDashboard()
            }
        }, 2000)
    }

    private fun routeUserToDashboard() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "Critical Error: User lost.", Toast.LENGTH_SHORT).show()
            return
        }

        // Ask Firebase what role this user just signed up as
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")

                    // Teleport to the correct dashboard
                    val destinationClass = when (role) {
                        "admin" -> AdminActivity::class.java
                        "barber" -> BarberActivity::class.java
                        else -> ClientActivity::class.java  // Client or any fallback
                    }

                    val intent = Intent(requireContext(), destinationClass)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()

                } else {
                    Toast.makeText(requireContext(), "Profile not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Network Error. Please restart app.", Toast.LENGTH_SHORT).show()
            }
    }
}