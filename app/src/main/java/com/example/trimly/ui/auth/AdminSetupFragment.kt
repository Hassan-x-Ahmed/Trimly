// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class AdminSetupFragment : Fragment() {

    // 1. Connect to the Shared Memory Bank
    private val sharedViewModel: AuthViewModel by activityViewModels()

    private lateinit var etSalonName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhone: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSalonName = view.findViewById(R.id.et_salon_name)
        etAddress = view.findViewById(R.id.et_address)
        etPhone = view.findViewById(R.id.et_phone)

        view.findViewById<ImageView>(R.id.back_btn).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.btn_next_admin).setOnClickListener {
            val name = etSalonName.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all salon details.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Save directly to the Memory Bank!
            sharedViewModel.salonName = name
            sharedViewModel.salonAddress = address
            sharedViewModel.salonPhone = phone

            // 3. Navigate cleanly without any luggage
            findNavController().navigate(R.id.adminHoursFragment)
        }
    }
}