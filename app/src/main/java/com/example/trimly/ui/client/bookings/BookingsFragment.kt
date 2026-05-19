// sid: 15932
package com.example.trimly.ui.client.bookings

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BookingsFragment : Fragment(R.layout.fragment_bookings) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var bookingsContainer: LinearLayout
    private lateinit var tvNoBookings: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookingsContainer = view.findViewById(R.id.bookings_container) ?: return
        tvNoBookings = view.findViewById(R.id.tv_no_bookings) ?: return

        loadClientBookings()
    }

    private fun loadClientBookings() {
        val clientId = auth.currentUser?.uid ?: return

        // Look for appointments owned by this client
        db.collection("appointments")
            .whereEqualTo("clientId", clientId)
            .get()
            .addOnSuccessListener { documents ->

                bookingsContainer.removeAllViews()

                if (documents.isEmpty) {
                    tvNoBookings.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                tvNoBookings.visibility = View.GONE

                // Build a card for every appointment found
                for (document in documents) {
                    val salonName = document.getString("barberName") ?: "Premium Salon" // Adjust based on your DB fields
                    val service = document.getString("service") ?: "Haircut"
                    val time = document.getString("time") ?: "TBD"
                    val status = document.getString("status") ?: "PENDING"

                    val card = createBookingCard(salonName, service, time, status.uppercase())
                    bookingsContainer.addView(card)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load bookings", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Programmatically recreates your stunning Confirmed Booking card
     */
    private fun createBookingCard(salonName: String, service: String, time: String, status: String): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val card = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (16 * density).toInt())
            }
            radius = 24f * density
            setCardBackgroundColor(Color.parseColor("#CC1A2422"))
            cardElevation = 12f * density
            setContentPadding((22 * density).toInt(), (22 * density).toInt(), (22 * density).toInt(), (22 * density).toInt())
        }

        val innerLayout = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }

        // --- 1. STATUS ROW ---
        val statusRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        // Status color (Green for active/confirmed, Orange for pending)
        val statusColor = if (status == "CONFIRMED" || status == "ACTIVE") "#00BFA5" else "#D4AF37"

        val dot = View(context).apply {
            layoutParams = LinearLayout.LayoutParams((10 * density).toInt(), (10 * density).toInt())
            setBackgroundResource(R.drawable.ic_green_dot) // Fallback to your drawable
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor(statusColor))
        }

        val tvStatus = TextView(context).apply {
            text = status
            setTextColor(Color.parseColor(statusColor))
            textSize = 10f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding((8 * density).toInt(), 0, 0, 0)
        }

        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }

        val tvTime = TextView(context).apply {
            text = time
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        statusRow.addView(dot); statusRow.addView(tvStatus); statusRow.addView(spacer); statusRow.addView(tvTime)

        // --- 2. SALON INFO ROW ---
        val infoRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, (20 * density).toInt(), 0, 0)
        }

        val imgSalon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams((56 * density).toInt(), (56 * density).toInt())
            setImageResource(android.R.drawable.ic_menu_gallery)
            setBackgroundColor(Color.parseColor("#222222"))
            setPadding((12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt())
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (16 * density).toInt()
            }
        }

        val tvName = TextView(context).apply {
            text = salonName
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvService = TextView(context).apply {
            text = service
            setTextColor(Color.parseColor("#999999"))
            textSize = 12f
            setPadding(0, (4 * density).toInt(), 0, 0)
        }

        textLayout.addView(tvName); textLayout.addView(tvService)
        infoRow.addView(imgSalon); infoRow.addView(textLayout)

        // --- 3. DIVIDER ---
        val divider = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (1 * density).toInt()).apply {
                setMargins(0, (20 * density).toInt(), 0, (20 * density).toInt())
            }
            setBackgroundColor(Color.parseColor("#33FFFFFF"))
        }

        // --- 4. ACTION BUTTONS ---
        val actionRow = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }

        val btnDirections = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, (48 * density).toInt(), 1f)
            setBackgroundResource(R.drawable.btn_ghost) // Assumes this exists in your res/drawable
            gravity = Gravity.CENTER
            text = "Directions"
            setTextColor(Color.WHITE)
            textSize = 13f
            setTypeface(null, android.graphics.Typeface.BOLD)

            setOnClickListener {
                Toast.makeText(context, "Routing to $salonName...", Toast.LENGTH_SHORT).show()
            }
        }

        val btnSpacer = View(context).apply { layoutParams = LinearLayout.LayoutParams((16 * density).toInt(), 0) }

        val btnReschedule = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, (48 * density).toInt(), 1f)
            setBackgroundColor(Color.parseColor("#D4AF37"))
            gravity = Gravity.CENTER
            text = "Reschedule"
            setTextColor(Color.BLACK)
            textSize = 13f
            setTypeface(null, android.graphics.Typeface.BOLD)

            setOnClickListener {
                Toast.makeText(context, "Opening available calendar slots...", Toast.LENGTH_SHORT).show()
            }
        }

        actionRow.addView(btnDirections); actionRow.addView(btnSpacer); actionRow.addView(btnReschedule)

        // Assemble the card
        innerLayout.addView(statusRow)
        innerLayout.addView(infoRow)
        innerLayout.addView(divider)
        innerLayout.addView(actionRow)
        card.addView(innerLayout)

        return card
    }
}