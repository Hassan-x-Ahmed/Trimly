// sid: 15932
package com.example.trimly.ui.admin

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R

class AdminAnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Hook into the Marketplace Pricing Button
        view.findViewById<Button>(R.id.btn_edit_marketplace_services)?.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Initializing Firebase Cloud Pricing Controller...",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 2. Simulated Data Entrance Animation
        // This makes the "Élysian Noir Analytics" text fade in elegantly
        val headerTitle = view.findViewById<TextView>(R.id.header_admin_title) // Ensure you add this ID to your XML textview
        headerTitle?.alpha = 0f
        headerTitle?.animate()
            ?.alpha(1f)
            ?.setDuration(1000)
            ?.setInterpolator(AccelerateDecelerateInterpolator())
            ?.start()

        // 3. Interactive Data Logic (Optional)
        // You can add click listeners to your leaderboard cards here later
        // to show individual barber performance details.
    }
}