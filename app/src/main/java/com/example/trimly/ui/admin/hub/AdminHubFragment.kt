// sid: 15932
package com.example.trimly.ui.admin.hub

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminViewModel

class AdminHubFragment : Fragment(R.layout.fragment_hub) {

    // 1. Connect to the master Admin Brain
    private val adminViewModel: AdminViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views
        val tvSalonName = view.findViewById<TextView>(R.id.tv_salon_name)
        val tvActiveChairs = view.findViewById<TextView>(R.id.tv_active_chairs)
        val tvTodayRevenue = view.findViewById<TextView>(R.id.tv_today_revenue)

        // 2. Observe LiveData from the ViewModel
        adminViewModel.salonName.observe(viewLifecycleOwner) { name ->
            tvSalonName.text = name
        }

        adminViewModel.activeChairCount.observe(viewLifecycleOwner) { count ->
            tvActiveChairs.text = count.toString()
        }

        // Note: Once you build your BookingRepo, you can uncomment this to watch revenue live!
        /*
        adminViewModel.todayRevenue.observe(viewLifecycleOwner) { revenue ->
            tvTodayRevenue.text = "$${String.format("%.2f", revenue)}"
        }
        */

        // 3. Quick Action Buttons (Toasts for now)
        view.findViewById<Button>(R.id.btn_action_walkin).setOnClickListener {
            Toast.makeText(requireContext(), "Walk-in feature coming soon", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_action_hours).setOnClickListener {
            Toast.makeText(requireContext(), "Store hours editor coming soon", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_action_payouts).setOnClickListener {
            Toast.makeText(requireContext(), "Staff payouts coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}