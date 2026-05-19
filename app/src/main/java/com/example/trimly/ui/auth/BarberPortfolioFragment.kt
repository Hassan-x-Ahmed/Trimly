// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BarberPortfolioFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 1. Connect to the Shared Memory Bank
    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barber_portfolio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSubmit = view.findViewById<Button>(R.id.btn_submit_barber)

        btnSubmit.setOnClickListener {
            btnSubmit.isEnabled = false
            btnSubmit.text = "Building Profile..."

            // Assuming you have an EditText for a bio or Instagram handle
            val bio = view.findViewById<EditText>(R.id.et_bio)?.text.toString().trim()

            saveBarberToFirebase(bio, btnSubmit)
        }
    }

    private fun saveBarberToFirebase(bio: String, btnSubmit: Button) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "Auth Error: Not logged in.", Toast.LENGTH_SHORT).show()
            btnSubmit.isEnabled = true
            return
        }

        // 2. Build the Master Profile using the Memory Bank data!
        val barberData = hashMapOf(
            "userId" to userId,
            "fullName" to sharedViewModel.barberName,
            "experience" to sharedViewModel.barberExperience,
            "bio" to bio,
            "role" to "barber",
            "createdAt" to System.currentTimeMillis()
        )

        // 3. Save to Firestore (Updating their main user document)
        db.collection("users").document(userId)
            .update(barberData as Map<String, Any>)
            .addOnSuccessListener {
                // SUCCESS! Teleport to the Universal Success Screen
                findNavController().navigate(R.id.setupSuccessFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnSubmit.isEnabled = true
                btnSubmit.text = "SUBMIT PORTFOLIO"
            }
    }
}