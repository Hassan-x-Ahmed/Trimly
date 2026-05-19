// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class BarberSetupFragment : Fragment() {

    // 1. Connect to the Shared Memory Bank
    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barber_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_next_barber).setOnClickListener {
            // Assuming these are your XML IDs!
            val name = view.findViewById<EditText>(R.id.et_barber_name)?.text.toString().trim()
            val experience = view.findViewById<EditText>(R.id.et_experience)?.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Save directly to the Memory Bank
            sharedViewModel.barberName = name
            sharedViewModel.barberExperience = experience

            // 3. Navigate cleanly to Step 2
            findNavController().navigate(R.id.barberPortfolioFragment)
        }
    }
}