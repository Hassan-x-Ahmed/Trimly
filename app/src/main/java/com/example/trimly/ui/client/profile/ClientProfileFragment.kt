// sid: 15932
package com.example.trimly.ui.client.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.example.trimly.ui.auth.AuthActivity

class ClientProfileFragment : Fragment(R.layout.fragment_client_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_logout)?.setOnClickListener {
            Toast.makeText(requireContext(), "Securely logging out...", Toast.LENGTH_SHORT).show()

            // Trigger the master reset back to the Auth gatekeeper
            val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }
}