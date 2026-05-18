// sid: 15932
package com.example.trimly.ui.admin.staff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R

class AdminStaffFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_staff_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pendingCard = view.findViewById<View>(R.id.card_pending_applicant)

        view.findViewById<Button>(R.id.btn_approve_applicant)?.setOnClickListener {
            pendingCard?.visibility = View.GONE
            Toast.makeText(requireContext(), "Nabeel Khan approved and assigned to Chair 3!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_reject_applicant)?.setOnClickListener {
            pendingCard?.visibility = View.GONE
            Toast.makeText(requireContext(), "Application declined.", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_add_staff)?.setOnClickListener {
            Toast.makeText(requireContext(), "Generating invite code for new stylist…", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btn_manage_closures)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening master salon calendar override…", Toast.LENGTH_SHORT).show()
        }
    }
}