// sid: 15932
package com.example.trimly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminActivity
import com.example.trimly.ui.barber.BarberActivity
import com.example.trimly.ui.client.ClientActivity

class SetupSuccessFragment : Fragment() {

    // Safely grabbing the shared ViewModel
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Explicitly inflate the success layout
        return inflater.inflate(R.layout.fragment_setup_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TIP: If this line still shows red, open fragment_setup_success.xml,
        // check what ID you gave your continue button, and paste that exact ID here!
        val btnFinish = view.findViewById<Button>(R.id.btn_finish_setup)

        btnFinish?.setOnClickListener {
            // Safely read the current role, defaulting to "Client" if null
            val currentRole = authViewModel.userRole.value ?: "Client"

            // Route to the dedicated isolated island based on the role
            val targetActivity = when (currentRole) {
                "Admin" -> AdminActivity::class.java
                "Barber" -> BarberActivity::class.java
                else -> ClientActivity::class.java
            }

            // Launch the container island and clear the onboarding history
            val intent = Intent(requireContext(), targetActivity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }
}