// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class AdminHoursFragment : Fragment() {

    // 1. Connect to the Shared Memory Bank
    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_hours, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.back_btn).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<Button>(R.id.btn_next_admin_step3).setOnClickListener {

            // 2. Save hours directly to the Memory Bank
            val standardHours = "9:00 AM - 8:00 PM (Mon-Sat)"
            sharedViewModel.salonHours = standardHours

            // 3. Navigate cleanly
            findNavController().navigate(R.id.adminServicesFragment)
        }
    }
}