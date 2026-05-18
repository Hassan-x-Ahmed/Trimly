// sid: 15932
package com.example.trimly.ui.barber.queue

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R

class BarberQueueFragment : Fragment(R.layout.fragment_barber_queue) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvClientName = view.findViewById<TextView>(R.id.tv_client_name)
        val tvServiceReq = view.findViewById<TextView>(R.id.tv_service_req)
        val btnMarkCompleted = view.findViewById<Button>(R.id.btn_complete_cut)

        // Mock data (replace with Firestore later)
        tvClientName?.text = "Hassan Ahmed"
        tvServiceReq?.text = "Executive Skin Fade • Rs. 3,500 • 6:30 PM"

        // Complete the cut
        btnMarkCompleted?.setOnClickListener {
            Toast.makeText(requireContext(), "Payment Collected! Rs. 3,500 added to Admin Revenue.", Toast.LENGTH_LONG).show()
            view.findViewById<View>(R.id.card_active_ticket)?.visibility = View.GONE
        }
    }
}