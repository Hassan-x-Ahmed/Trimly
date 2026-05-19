// sid: 15932
package com.example.trimly.ui.admin.hub

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminHubFragment : Fragment(R.layout.fragment_hub) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var tvGreeting: TextView
    private lateinit var tvSalonName: TextView
    private lateinit var tvRevenue: TextView
    private lateinit var tvStations: TextView
    private lateinit var floorContainer: LinearLayout
    private lateinit var tvEmptyFloor: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGreeting = view.findViewById(R.id.tv_greeting) ?: return
        tvSalonName = view.findViewById(R.id.tv_salon_name) ?: return
        tvRevenue = view.findViewById(R.id.tv_revenue) ?: return
        tvStations = view.findViewById(R.id.tv_stations) ?: return
        floorContainer = view.findViewById(R.id.floor_status_container) ?: return
        tvEmptyFloor = view.findViewById(R.id.tv_empty_floor) ?: return

        loadAdminProfile()
        loadFloorStatus()

        // Button Listeners
        view.findViewById<View>(R.id.banner_pending_staff)?.setOnClickListener {
            // findNavController().navigate(R.id.adminStaffFragment) // Uncomment when ready
        }
        view.findViewById<View>(R.id.btn_action_walkin)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening quick walk‑in registration…", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_action_hours)?.setOnClickListener {
            Toast.makeText(requireContext(), "Launching store schedule override…", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_action_payouts)?.setOnClickListener {
            Toast.makeText(requireContext(), "Calculating weekly staff commissions…", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_notifications)?.setOnClickListener {
            Toast.makeText(requireContext(), "No new system alerts.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAdminProfile() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val name = doc.getString("fullName") ?: "Admin"
                    // Extracting the first name for a casual greeting
                    val firstName = name.split(" ").firstOrNull() ?: "Admin"
                    tvGreeting.text = "Good evening, $firstName"

                    // You can pull the actual Salon name from the admin's DB profile here
                    tvSalonName.text = "Élysian Noir Salon"
                }
            }
    }

    private fun loadFloorStatus() {
        // Find all Barbers in the system to populate the chairs
        db.collection("users").whereEqualTo("role", "barber").get()
            .addOnSuccessListener { documents ->
                floorContainer.removeAllViews()

                val totalChairs = documents.size()
                // For realism, let's pretend everyone is active for now
                tvStations.text = "$totalChairs / $totalChairs"

                if (documents.isEmpty) {
                    tvEmptyFloor.visibility = View.VISIBLE
                    floorContainer.visibility = View.GONE
                    tvRevenue.text = "PKR 0"
                    return@addOnSuccessListener
                }

                tvEmptyFloor.visibility = View.GONE
                floorContainer.visibility = View.VISIBLE

                // Mocking revenue based on active barbers
                tvRevenue.text = "PKR ${totalChairs * 7500}"

                var chairNumber = 1
                for (doc in documents) {
                    val barberName = doc.getString("fullName") ?: "Stylist"

                    // Alternate status for a realistic UI mockup until live booking statuses are tied in
                    val isBusy = chairNumber % 2 != 0
                    val statusText = if (isBusy) "Occupied • Cut in progress" else "Ready • Next cut soon"
                    val badgeText = if (isBusy) "BUSY" else "OPEN"
                    val badgeColor = if (isBusy) "#E57373" else "#2E7D32"

                    val chairRow = createChairRow(chairNumber, barberName, statusText, badgeText, badgeColor)
                    floorContainer.addView(chairRow)
                    chairNumber++
                }
            }
    }

    /**
     * Programmatically builds the Chair Status rows exactly like your design
     */
    private fun createChairRow(chairNum: Int, name: String, statusDesc: String, badgeText: String, colorHex: String): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, (16 * density).toInt())
        }

        // 1. Chair Number Icon
        val tvChairNum = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams((24 * density).toInt(), (24 * density).toInt())
            text = String.format("%02d", chairNum)
            setTextColor(Color.parseColor("#2E7D32"))
            textSize = 10f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#E8F5E9"))
        }

        // 2. Middle Text Area (Name & Status)
        val middleLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (12 * density).toInt()
            }
        }

        val tvName = TextView(context).apply {
            text = "Chair $chairNum: $name"
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvStatusDesc = TextView(context).apply {
            text = statusDesc
            setTextColor(if (badgeText == "OPEN") Color.parseColor("#00BFA5") else Color.parseColor("#999999"))
            textSize = 12f
        }

        middleLayout.addView(tvName); middleLayout.addView(tvStatusDesc)

        // 3. Status Badge
        val tvBadge = TextView(context).apply {
            text = badgeText
            setTextColor(Color.parseColor(colorHex))
            textSize = 10f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        row.addView(tvChairNum)
        row.addView(middleLayout)
        row.addView(tvBadge)

        return row
    }
}