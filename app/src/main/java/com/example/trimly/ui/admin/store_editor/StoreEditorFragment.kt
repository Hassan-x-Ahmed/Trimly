// sid: 15932
package com.example.trimly.ui.admin.store_editor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminViewModel
import com.example.trimly.ui.auth.AuthActivity

class StoreEditorFragment : Fragment(R.layout.fragment_store_profile) {

    private val adminViewModel: AdminViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tv_profile_name)
        val tvAddress = view.findViewById<TextView>(R.id.tv_profile_address)

        // 1. Observe data from the "Brain"
        adminViewModel.salonName.observe(viewLifecycleOwner) { name ->
            tvName.text = name.ifEmpty { "Name not set" }
        }

        adminViewModel.salonAddress.observe(viewLifecycleOwner) { address ->
            tvAddress.text = address.ifEmpty { "Address not set" }
        }

        // 2. Edit Buttons (Placeholders for now)
        view.findViewById<TextView>(R.id.btn_edit_details).setOnClickListener {
            Toast.makeText(requireContext(), "Edit Details coming soon", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_manage_services).setOnClickListener {
            Toast.makeText(requireContext(), "Service Manager coming soon", Toast.LENGTH_SHORT).show()
        }

        // 3. The Logout Protocol
        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        // Clear Firebase session via ViewModel
        adminViewModel.logout()

        // Teleport back to AuthActivity and clear the backstack
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Kill the AdminActivity so the user can't press back
        requireActivity().finish()
    }
}