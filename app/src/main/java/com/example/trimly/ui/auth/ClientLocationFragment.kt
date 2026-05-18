// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class ClientLocationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define the navigation action
        val navigateToSuccess = {
            findNavController().navigate(R.id.action_location_to_success)
        }

        // Both Confirm and Skip will now take the user to the Success screen
        view.findViewById<Button>(R.id.btn_confirm).setOnClickListener { navigateToSuccess() }
        view.findViewById<TextView>(R.id.btn_skip).setOnClickListener { navigateToSuccess() }
    }
}