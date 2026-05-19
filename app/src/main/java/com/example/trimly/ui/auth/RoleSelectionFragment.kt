// sid: 15932
package com.example.trimly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.MainActivity
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminActivity
import com.example.trimly.ui.barber.BarberActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RoleSelectionFragment : Fragment(R.layout.fragment_role_selection) {

    // 1. Initialize our Firebase Engines
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- CLIENT ISLAND ---
        view.findViewById<MaterialCardView>(R.id.card_client)?.setOnClickListener {
            registerAndAssignRole("client")
        }

        // --- BARBER ISLAND ---
        view.findViewById<MaterialCardView>(R.id.card_barber)?.setOnClickListener {
            registerAndAssignRole("barber")
        }

        // --- ADMIN COMMAND CENTER ---
        view.findViewById<MaterialCardView>(R.id.card_admin)?.setOnClickListener {
            registerAndAssignRole("admin")
        }
    }

    /**
     * This function handles the Firebase Registration and Firestore Role Stamping
     */
    private fun registerAndAssignRole(selectedRole: String) {
        Toast.makeText(requireContext(), "Connecting to Secure Servers...", Toast.LENGTH_SHORT).show()

        // For testing purposes right now, we will create a dummy email based on the role and a random number
        // (In a real app, you would grab this from EditText fields where the user typed their actual email)
        val testEmail = "test_${selectedRole}_${System.currentTimeMillis()}@trimly.com"
        val testPassword = "Password123!"

        // 1. Create the Auth Account
        auth.createUserWithEmailAndPassword(testEmail, testPassword)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                // 2. Create the User Profile Map
                val userProfile = hashMapOf(
                    "email" to testEmail,
                    "role" to selectedRole,
                    "createdAt" to System.currentTimeMillis()
                )

                // 3. Save the Profile to Firestore
                db.collection("users").document(userId)
                    .set(userProfile)
                    .addOnSuccessListener {
                        // 4. Teleport to the correct Island only AFTER the database confirms the save
                        teleportToIsland(selectedRole)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Auth Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun teleportToIsland(role: String) {
        val destinationClass = when (role) {
            "admin" -> AdminActivity::class.java
            "barber" -> BarberActivity::class.java
            else -> MainActivity::class.java // Client
        }

        val intent = Intent(requireContext(), destinationClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}