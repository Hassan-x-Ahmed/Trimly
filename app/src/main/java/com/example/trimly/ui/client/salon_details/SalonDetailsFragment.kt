// sid: 15932
package com.example.trimly.ui.client.salon_details

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class SalonDetailsFragment : Fragment(R.layout.fragment_salon_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Back Anchor: Return to the active Discovery Map
        view.findViewById<ImageView>(R.id.btn_back_discovery)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // 2. Booking Anchor: Bridge forward into the Time-Slot Checkout confirmation
        view.findViewById<Button>(R.id.btn_proceed_checkout)?.setOnClickListener {
            findNavController().navigate(R.id.action_details_to_checkout)
        }
    }
}