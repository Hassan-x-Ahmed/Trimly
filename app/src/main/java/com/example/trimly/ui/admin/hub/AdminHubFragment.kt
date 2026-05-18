// sid: 15932
package com.example.trimly.ui.admin.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class AdminHubFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pending staff banner navigates to staff manager
        view.findViewById<View>(R.id.banner_pending_staff)?.setOnClickListener {
            findNavController().navigate(R.id.adminStaffFragment)
        }

        // Quick actions – replace with real intent later
        view.findViewById<View>(R.id.btn_action_walkin)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening quick walk‑in registration…", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_action_hours)?.setOnClickListener {
            Toast.makeText(requireContext(), "Launching store schedule override…", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_action_payouts)?.setOnClickListener {
            Toast.makeText(requireContext(), "Calculating weekly staff commissions…", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_notifications)?.setOnClickListener {
            Toast.makeText(requireContext(), "No new system alerts.", Toast.LENGTH_SHORT).show()
        }
    }
}