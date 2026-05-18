// sid: 15932
package com.example.trimly.ui.barber.requests

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R

class BarberRequestsFragment : Fragment(R.layout.fragment_requests) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_accept_sarah)?.setOnClickListener {
            Toast.makeText(requireContext(), "Booking accepted: Sarah Ahmed at 5:00 PM", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_decline_sarah)?.setOnClickListener {
            Toast.makeText(requireContext(), "Booking declined.", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_claim_dispatch)?.setOnClickListener {
            Toast.makeText(requireContext(), "Dispatch claimed! Check your schedule.", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_claim_dispatch2)?.setOnClickListener {
            Toast.makeText(requireContext(), "Dispatch claimed! Check your schedule.", Toast.LENGTH_SHORT).show()
        }
    }
}