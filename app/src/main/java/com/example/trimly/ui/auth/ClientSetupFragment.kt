// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class ClientSetupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Route 1: The Fast Track
        view.findViewById<Button>(R.id.btn_google).setOnClickListener {
            findNavController().navigate(R.id.action_clientSetup_to_googleAuth)
        }

        // Route 2: The Manual Track
        view.findViewById<Button>(R.id.btn_guest).setOnClickListener {
            findNavController().navigate(R.id.action_clientSetup_to_guestSetup)
        }
    }
}