// sid: 15932
package com.example.trimly.ui.barber.schedule

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BarberScheduleFragment : Fragment(R.layout.fragment_schedule) { // Make sure this layout name is correct!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var container: LinearLayout
    private lateinit var tvNoSchedule: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make sure you added these IDs to your fragment_schedule.xml!
        container = view.findViewById(R.id.schedule_container) ?: return
        tvNoSchedule = view.findViewById(R.id.tv_no_schedule) ?: return

        loadConfirmedSchedule()
    }

    private fun loadConfirmedSchedule() {
        val barberId = auth.currentUser?.uid ?: return

        // 1. Ask Firestore for appointments assigned to ME, that are CONFIRMED
        db.collection("appointments")
            .whereEqualTo("barberId", barberId)
            .whereEqualTo("status", "confirmed")
            .get()
            .addOnSuccessListener { documents ->

                container.removeAllViews()

                if (documents.isEmpty) {
                    tvNoSchedule.visibility = View.VISIBLE
                    tvNoSchedule.text = "Your schedule is clear!"
                } else {
                    tvNoSchedule.visibility = View.GONE

                    for (document in documents) {
                        val clientName = document.getString("clientName") ?: "Unknown Client"
                        val service = document.getString("service") ?: "Trim"
                        val time = document.getString("time") ?: "TBD"

                        val card = TextView(requireContext()).apply {
                            text = "CONFIRMED APPOINTMENT:\n$clientName\n$service at $time"
                            textSize = 16f
                            setTextColor(android.graphics.Color.WHITE)
                            setPadding(30, 30, 30, 30)
                            setBackgroundColor(android.graphics.Color.parseColor("#00BFA5")) // Green tint for confirmed
                        }

                        container.addView(card)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading schedule: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}