// sid: 15932
package com.example.trimly.ui.client.checkout

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Back Anchor: Return smoothly to the Salon Details Showcase
        view.findViewById<ImageView>(R.id.btn_back_details)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // 2. Complete Checkout: Trigger confirmation toast and pop back to the main dashboard
        view.findViewById<Button>(R.id.btn_confirm_reservation)?.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Appointment Confirmed! Élysian Noir notified.",
                Toast.LENGTH_LONG
            ).show()

            // Safely unwind the navigation stack back to the root discovery screen
            // compiling instantly without relying on unmapped XML graph IDs
            findNavController().navigateUp()
            findNavController().navigateUp()
        }
    }
}