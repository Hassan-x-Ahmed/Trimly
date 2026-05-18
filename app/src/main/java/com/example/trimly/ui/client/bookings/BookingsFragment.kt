// sid: 15932
package com.example.trimly.ui.client.bookings

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R

class BookingsFragment : Fragment(R.layout.fragment_bookings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Directions Anchor
        view.findViewById<TextView>(R.id.btn_get_directions)?.setOnClickListener {
            Toast.makeText(requireContext(), "Routing to Élysian Noir, Clifton...", Toast.LENGTH_SHORT).show()
        }

        // 2. Reschedule Anchor
        view.findViewById<TextView>(R.id.btn_reschedule)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening available calendar slots...", Toast.LENGTH_SHORT).show()
        }
    }
}

