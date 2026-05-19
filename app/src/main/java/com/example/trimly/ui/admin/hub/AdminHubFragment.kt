// sid: 15932
package com.example.trimly.ui.admin.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminViewModel
import com.google.android.material.card.MaterialCardView

// Data class specifically for the UI Analytics
data class BarberPerformance(
    val name: String,
    val cutsToday: Int,
    val maxCapacity: Int = 10
)

class AdminHubFragment : Fragment(R.layout.fragment_hub) {

    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var performanceAdapter: PerformanceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvAdminName = view.findViewById<TextView>(R.id.tv_admin_name)
        val tvRevenue = view.findViewById<TextView>(R.id.tv_revenue_amount)
        val tvChairs = view.findViewById<TextView>(R.id.tv_active_chairs)
        val rvPerformance = view.findViewById<RecyclerView>(R.id.rv_team_performance)

        // Set up the RecyclerView
        performanceAdapter = PerformanceAdapter(emptyList())
        rvPerformance.layoutManager = LinearLayoutManager(requireContext())
        rvPerformance.adapter = performanceAdapter

        // Update the header data
        adminViewModel.salonName.observe(viewLifecycleOwner) { name ->
            tvAdminName.text = name.ifEmpty { "Admin" }
        }
        adminViewModel.todayRevenue.observe(viewLifecycleOwner) { revenue ->
            tvRevenue.text = "PKR ${String.format("%,.0f", revenue)}"
        }

        // --- REAL-TIME ANALYTICS MAGIC ---
        adminViewModel.barbersList.observe(viewLifecycleOwner) { barbers ->
            // Update the "Active Chairs" stat card to show total registered barbers
            tvChairs.text = "${barbers.size} / 12"

            // Map real Firebase barbers to our Analytics UI
            val analyticsList = barbers.map { user ->
                val barberName = user.name ?:   "Unknown"

                // SIMULATED DATA: We use a math trick based on name length to generate a
                // fake number of cuts (between 2 and 9) just so the UI looks alive.
                // TODO: Replace this with real Firebase database queries when Bookings are built!
                val simulatedCuts = (barberName.length * 3) % 8 + 2

                BarberPerformance(
                    name = barberName,
                    cutsToday = simulatedCuts
                )
            }

            // Sort by top performers first!
            val sortedList = analyticsList.sortedByDescending { it.cutsToday }
            performanceAdapter.updateData(sortedList)
        }
    }

    // --- INNER ADAPTER FOR PERFORMANCE ROWS ---
    inner class PerformanceAdapter(private var performances: List<BarberPerformance>) : RecyclerView.Adapter<PerformanceAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_perf_name)
            val tvCount: TextView = view.findViewById(R.id.tv_perf_count)
            val progressBar: ProgressBar = view.findViewById(R.id.progress_perf)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team_performance, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val perf = performances[position]
            holder.tvName.text = perf.name
            holder.tvCount.text = "${perf.cutsToday} / ${perf.maxCapacity}"
            holder.progressBar.max = perf.maxCapacity
            holder.progressBar.progress = perf.cutsToday

            // Color coding the progress bar based on performance
            if (perf.cutsToday >= 8) {
                holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#00B894")) // Green for excellent
            } else if (perf.cutsToday >= 4) {
                holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#6C5CE7")) // Purple for average
            } else {
                holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FDCB6E")) // Yellow for slow day
            }
        }

        override fun getItemCount(): Int = performances.size

        fun updateData(newList: List<BarberPerformance>) {
            performances = newList
            notifyDataSetChanged()
        }
    }
}