// sid: 15932
package com.example.trimly.ui.admin.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminAnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var tvRevenue: TextView
    private lateinit var tvCompleted: TextView
    private lateinit var tvPending: TextView
    private lateinit var topServicesContainer: LinearLayout
    private lateinit var leaderboardContainer: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvRevenue = view.findViewById(R.id.tv_analytics_revenue) ?: return
        tvCompleted = view.findViewById(R.id.tv_analytics_completed) ?: return
        tvPending = view.findViewById(R.id.tv_analytics_pending) ?: return
        topServicesContainer = view.findViewById(R.id.top_services_container) ?: return
        leaderboardContainer = view.findViewById(R.id.leaderboard_container) ?: return

        // Elegant Entrance Animation
        val headerTitle = view.findViewById<TextView>(R.id.header_admin_title)
        headerTitle?.alpha = 0f
        headerTitle?.animate()?.alpha(1f)?.setDuration(1000)?.setInterpolator(AccelerateDecelerateInterpolator())?.start()

        // Hook into Marketplace Controller
        view.findViewById<Button>(R.id.btn_edit_marketplace_services)?.setOnClickListener {
            Toast.makeText(requireContext(), "Initializing Firebase Cloud Pricing Controller...", Toast.LENGTH_SHORT).show()
        }

        calculateLiveAnalytics()
    }

    private fun calculateLiveAnalytics() {
        // Pull every appointment to aggregate data
        db.collection("appointments").get().addOnSuccessListener { documents ->
            var totalRevenue = 0
            var completedCount = 0
            var pendingCount = 0

            // Maps to group data for leaderboards
            val serviceRevenueMap = mutableMapOf<String, Int>()
            val barberRevenueMap = mutableMapOf<String, Int>()

            for (doc in documents) {
                val status = doc.getString("status") ?: "PENDING"
                val priceStr = doc.getString("price") ?: "0"
                val serviceName = doc.getString("service") ?: "Unknown Service"

                // Usually barber ID is used, but for UI mockup we fallback to Barber Name if ID isn't mapped properly yet
                val barberName = doc.getString("barberName") ?: "Unknown Barber"

                val priceInt = priceStr.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

                if (status == "PENDING") {
                    pendingCount++
                } else {
                    completedCount++
                    totalRevenue += priceInt

                    // Grouping for rankings
                    serviceRevenueMap[serviceName] = serviceRevenueMap.getOrDefault(serviceName, 0) + priceInt
                    barberRevenueMap[barberName] = barberRevenueMap.getOrDefault(barberName, 0) + priceInt
                }
            }

            // Update Header Stats
            tvRevenue.text = "Rs. $totalRevenue"
            tvCompleted.text = completedCount.toString()
            tvPending.text = pendingCount.toString()

            // Build Top Services (Sort by highest revenue)
            topServicesContainer.removeAllViews()
            val sortedServices = serviceRevenueMap.entries.sortedByDescending { it.value }.take(3)

            if (sortedServices.isEmpty()) {
                val tvNoData = TextView(requireContext()).apply { text = "Not enough data."; setTextColor(Color.GRAY) }
                topServicesContainer.addView(tvNoData)
            } else {
                for (entry in sortedServices) {
                    topServicesContainer.addView(createTopServiceRow(entry.key, entry.value))
                }
            }

            // Build Leaderboard (Sort by highest revenue)
            leaderboardContainer.removeAllViews()
            val sortedBarbers = barberRevenueMap.entries.sortedByDescending { it.value }.take(3)

            for (entry in sortedBarbers) {
                leaderboardContainer.addView(createLeaderboardCard(entry.key, entry.value))
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to sync analytics.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Builds the inner rows for the Top Services Card
     */
    private fun createTopServiceRow(name: String, revenue: Int): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = (16 * density).toInt()
            }
        }

        val textRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = (8 * density).toInt()
            }
        }

        val tvName = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = name
            setTextColor(Color.WHITE)
            textSize = 13f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvRev = TextView(context).apply {
            text = "Rs. $revenue"
            setTextColor(Color.parseColor("#D4AF37"))
            textSize = 13f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        textRow.addView(tvName); textRow.addView(tvRev)

        // Progress Bar Mockup
        val barRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (6 * density).toInt())
        }

        val barFilled = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.6f)
            setBackgroundColor(Color.parseColor("#D4AF37"))
        }
        val barEmpty = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f)
            setBackgroundColor(Color.parseColor("#333333"))
        }

        barRow.addView(barFilled); barRow.addView(barEmpty)

        layout.addView(textRow)
        layout.addView(barRow)
        return layout
    }

    /**
     * Builds the individual dark cards for the Barber Leaderboard
     */
    private fun createLeaderboardCard(name: String, revenue: Int): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val card = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = (8 * density).toInt()
            }
            radius = 16f * density
            setCardBackgroundColor(Color.parseColor("#0D1211"))
            cardElevation = 0f
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
        }

        val avatar = View(context).apply {
            layoutParams = LinearLayout.LayoutParams((40 * density).toInt(), (40 * density).toInt())
            setBackgroundColor(Color.parseColor("#121212"))
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (16 * density).toInt()
            }
        }

        val tvName = TextView(context).apply {
            text = name
            setTextColor(Color.WHITE) // or Gold for #1
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val barRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (6 * density).toInt()).apply {
                topMargin = (6 * density).toInt()
            }
        }

        val barFilled = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.7f)
            setBackgroundColor(Color.parseColor("#00BFA5"))
        }
        val barEmpty = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f)
            setBackgroundColor(Color.parseColor("#222222"))
        }
        barRow.addView(barFilled); barRow.addView(barEmpty)

        textLayout.addView(tvName); textLayout.addView(barRow)

        val tvRev = TextView(context).apply {
            text = "Rs. $revenue"
            setTextColor(Color.parseColor("#FFFFFF"))
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding((16 * density).toInt(), 0, 0, 0)
        }

        layout.addView(avatar); layout.addView(textLayout); layout.addView(tvRev)
        card.addView(layout)

        return card
    }
}