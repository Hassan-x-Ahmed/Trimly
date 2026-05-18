// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // IMPORTANT IMPORT
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.android.material.card.MaterialCardView

class RoleSelectionFragment : Fragment() {

    // 1. Connect to the shared ViewModel "brain"
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_role_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardClient = view.findViewById<MaterialCardView>(R.id.card_client)
        val cardBarber = view.findViewById<MaterialCardView>(R.id.card_barber)
        val cardAdmin = view.findViewById<MaterialCardView>(R.id.card_admin)

        // 2. Client Selection
        cardClient.setOnClickListener {
            authViewModel.setRole("Client") // Save the choice!
            findNavController().navigate(R.id.action_roleSelection_to_client_setup)
        }

        // 3. Barber Selection
        cardBarber.setOnClickListener {
            authViewModel.setRole("Barber") // Save the choice!
            findNavController().navigate(R.id.to_barber_setup)
        }

        // 4. Admin Selection
        cardAdmin.setOnClickListener {
            authViewModel.setRole("Admin") // Save the choice!
            findNavController().navigate(R.id.to_admin_setup)
        }
    }
}