// sid: 15932
package com.example.trimly.ui.client.home

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(R.layout.fragment_home) { // Make sure this matches your XML name!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var feedContainer: LinearLayout
    private lateinit var tvNoBarbers: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedContainer = view.findViewById(R.id.curated_barbers_container) ?: return
        tvNoBarbers = view.findViewById(R.id.tv_no_barbers) ?: return

        fetchTopBarbers()
    }

    private fun fetchTopBarbers() {
        // Query Firestore: Find ALL users who are registered as Barbers
        db.collection("users")
            .whereEqualTo("role", "barber")
            .get()
            .addOnSuccessListener { documents ->

                feedContainer.removeAllViews()

                if (documents.isEmpty) {
                    tvNoBarbers.visibility = View.VISIBLE
                    tvNoBarbers.text = "No barbers available in your area yet."
                    return@addOnSuccessListener
                }

                tvNoBarbers.visibility = View.GONE

                for (document in documents) {
                    val barberId = document.id
                    val name = document.getString("fullName") ?: "Premium Stylist"
                    val title = document.getString("title") ?: "Professional Barber"

                    // Build the horizontal card
                    val barberCard = createHorizontalBarberCard(name, title, barberId)
                    feedContainer.addView(barberCard)
                }
            }
            .addOnFailureListener { e ->
                tvNoBarbers.visibility = View.VISIBLE
                tvNoBarbers.text = "Error connecting to radar: ${e.message}"
            }
    }

    /**
     * Programmatically builds the 280dp wide horizontal card matching your design
     */
    private fun createHorizontalBarberCard(name: String, title: String, barberId: String): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        // The Outer Card (280dp wide)
        val card = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                (280 * density).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = (16 * density).toInt()
            }
            radius = 20f * density
            setCardBackgroundColor(Color.parseColor("#CC1A2422"))
            cardElevation = 8f * density
        }

        // Inner Padding Layout
        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((18 * density).toInt(), (18 * density).toInt(), (18 * density).toInt(), (18 * density).toInt())
        }

        // Barber Name
        val tvName = TextView(context).apply {
            text = name
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Title / Location Fake String
        val tvLocation = TextView(context).apply {
            text = "$title • ★ 4.9 (120+)"
            setTextColor(Color.parseColor("#999999"))
            textSize = 12f
            setPadding(0, (4 * density).toInt(), 0, 0)
        }

        // Open Status
        val tvStatus = TextView(context).apply {
            text = "Open until 10 PM"
            setTextColor(Color.parseColor("#00BFA5"))
            textSize = 10f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (10 * density).toInt(), 0, 0)
        }

        innerLayout.addView(tvName)
        innerLayout.addView(tvLocation)
        innerLayout.addView(tvStatus)
        card.addView(innerLayout)

        card.setOnClickListener {
            Toast.makeText(context, "Opening profile for $name...", Toast.LENGTH_SHORT).show()
        }

        return card
    }
}