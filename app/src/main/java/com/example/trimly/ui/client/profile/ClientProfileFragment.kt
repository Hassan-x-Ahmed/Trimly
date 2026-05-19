// sid: 15932
package com.example.trimly.ui.client.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.example.trimly.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClientProfileFragment : Fragment(R.layout.fragment_client_profile) {

    // 1. The Cloud Engines
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 2. UI Variables
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: LinearLayout
    private lateinit var btnNotifications: LinearLayout
    private lateinit var btnBiometric: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Link the UI variables to your XML Layout
        tvName = view.findViewById(R.id.tv_profile_name) ?: return
        tvEmail = view.findViewById(R.id.tv_profile_email) ?: return
        btnLogout = view.findViewById(R.id.btn_logout) ?: return
        btnNotifications = view.findViewById(R.id.btn_notifications) ?: return
        btnBiometric = view.findViewById(R.id.btn_biometric) ?: return

        // 4. Download their profile from Firebase the second the screen opens
        loadClientProfile()

        // 5. Wire up the interactive buttons
        btnLogout.setOnClickListener {
            // Tell Firebase to destroy the current session
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Teleport the user back to the login screen and wipe the back history
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Optional: Add quick toasts to the settings toggles so they feel interactive for now
        btnNotifications.setOnClickListener {
            Toast.makeText(requireContext(), "Notification preferences saved.", Toast.LENGTH_SHORT).show()
        }

        btnBiometric.setOnClickListener {
            Toast.makeText(requireContext(), "Biometric preferences saved.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Grabs the live data from Firebase Auth and Firestore, then paints it on the UI
     */
    private fun loadClientProfile() {
        val user = auth.currentUser
        val userId = user?.uid ?: return

        // We can grab the email instantly from the Auth engine without asking Firestore
        tvEmail.text = user.email ?: "No email linked"

        // Reach into Firestore to grab the actual "fullName" they typed during Sign Up
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val realName = document.getString("fullName")

                    // If they have a real name in the database, overwrite the "Hassan Ahmed" placeholder
                    if (!realName.isNullOrEmpty()) {
                        tvName.text = realName
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to sync cloud data.", Toast.LENGTH_SHORT).show()
            }
    }
}