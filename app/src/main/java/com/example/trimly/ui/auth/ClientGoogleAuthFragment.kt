// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClientGoogleAuthFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var progressBar: ProgressBar
    private lateinit var imgSuccess: ImageView
    private lateinit var tvStatusText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_google_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progress_bar)
        imgSuccess = view.findViewById(R.id.img_success)
        tvStatusText = view.findViewById(R.id.tv_status_text)

        // Trigger the Firebase Auth Simulation
        simulateGoogleSignIn()
    }

    private fun simulateGoogleSignIn() {
        // We use a generated email to create a real backend session without needing the actual Google UI popup for now
        val mockGoogleEmail = "client_${System.currentTimeMillis()}@trimly.com"
        val mockPassword = "SecureGoogleMock123!"

        auth.createUserWithEmailAndPassword(mockGoogleEmail, mockPassword)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                // Simulating pulling data from a real Google Account
                val userProfile = hashMapOf(
                    "email" to mockGoogleEmail,
                    "fullName" to "Hassan Ahmed", // Pulled from "Google"
                    "role" to "client",
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("users").document(userId)
                    .set(userProfile)
                    .addOnSuccessListener {
                        showSuccessAndNavigate()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Database Error.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Auth Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showSuccessAndNavigate() {
        if (!isAdded) return

        // Swap the UI to show the success checkmark
        progressBar.visibility = View.GONE
        imgSuccess.visibility = View.VISIBLE
        tvStatusText.text = "Profile established instantly using Google account data."

        // Wait 1.5 seconds so the user can read the success message, then navigate
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                try {
                    findNavController().navigate(R.id.action_googleAuth_to_clientLocation)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Navigation Error to Location", Toast.LENGTH_SHORT).show()
                }
            }
        }, 1500)
    }
}