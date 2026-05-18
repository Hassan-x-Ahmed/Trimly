// sid: 15932
package com.example.trimly.ui.client.booking_success

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class BookingSuccessFragment : Fragment(R.layout.fragment_booking_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate directly to the Bookings tab (clientBookingsFragment)
        view.findViewById<View>(R.id.btn_view_bookings).setOnClickListener {
            findNavController().navigate(R.id.clientBookingsFragment)
        }

        // Go back to the home screen (pop to clientHomeFragment)
        view.findViewById<View>(R.id.btn_back_to_home).setOnClickListener {
            findNavController().popBackStack(R.id.clientHomeFragment, false)
        }
    }
}

