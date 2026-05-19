// sid: 15932
package com.example.trimly.ui.client.bookings

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class BookingSuccessFragment : Fragment(R.layout.fragment_booking_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Primary Action: View Appointments
        view.findViewById<Button>(R.id.btn_view_bookings).setOnClickListener {
            try {
                // Navigate directly to the Bookings tab
                findNavController().navigate(R.id.clientBookingsFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Navigation Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Secondary Action: Back to Home Feed
        view.findViewById<TextView>(R.id.btn_back_to_home).setOnClickListener {
            try {
                // Pop the back stack to prevent the user from hitting the Android 'Back' button
                // and returning to this success screen later.
                findNavController().popBackStack(R.id.clientHomeFragment, false)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Navigation Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}