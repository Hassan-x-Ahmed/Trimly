// sid: 15932
package com.example.trimly.ui.barber.requests

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BarberRequestsFragment : Fragment(R.layout.fragment_requests) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var container: LinearLayout
    private lateinit var tvNoRequests: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Link the XML containers
        container = view.findViewById(R.id.requests_container) ?: return
        tvNoRequests = view.findViewById(R.id.tv_no_requests) ?: return

        loadPendingRequests()
    }

    private fun loadPendingRequests() {
        val barberId = auth.currentUser?.uid ?: return

        // 1. Ask Firestore for appointments assigned to ME, that are PENDING
        db.collection("appointments")
            .whereEqualTo("barberId", barberId)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { documents ->

                // Clear out any old hardcoded XML cards
                container.removeAllViews()

                if (documents.isEmpty) {
                    // No requests? Show the empty message!
                    tvNoRequests.visibility = View.VISIBLE
                    tvNoRequests.text = "No pending requests at the moment."
                } else {
                    tvNoRequests.visibility = View.GONE

                    // Loop through the live cloud data and build a card for each one
                    for (document in documents) {
                        val clientName = document.getString("clientName") ?: "Unknown Client"
                        val service = document.getString("service") ?: "Trim"
                        val time = document.getString("time") ?: "TBD"

                        // For now, we will just inject a simple text block to prove the data is flowing.
                        // (Later we can inflate your beautiful custom XML card here!)
                        val card = TextView(requireContext()).apply {
                            text = "REQUEST:\n$clientName\n$service at $time"
                            textSize = 16f
                            setTextColor(android.graphics.Color.WHITE)
                            setPadding(30, 30, 30, 30)
                            setBackgroundColor(android.graphics.Color.parseColor("#1A2422")) // Dark card background
                        }

                        container.addView(card)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading requests: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}