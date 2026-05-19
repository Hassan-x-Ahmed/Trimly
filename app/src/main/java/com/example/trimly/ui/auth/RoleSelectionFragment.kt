// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.android.material.card.MaterialCardView

class RoleSelectionFragment : Fragment(R.layout.fragment_role_selection) {

    // Shared across fragments inside AuthActivity
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.card_client)?.setOnClickListener {
            authViewModel.selectedRole = "client"
            findNavController().navigate(R.id.signupFragment)
        }

        view.findViewById<MaterialCardView>(R.id.card_barber)?.setOnClickListener {
            authViewModel.selectedRole = "barber"
            findNavController().navigate(R.id.signupFragment)
        }

        view.findViewById<MaterialCardView>(R.id.card_admin)?.setOnClickListener {
            authViewModel.selectedRole = "admin"
            findNavController().navigate(R.id.signupFragment)
        }
    }
}