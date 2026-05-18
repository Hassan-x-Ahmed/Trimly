// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class ClientGoogleAuthFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_google_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulate a 1.5 second loading delay, then jump to the Location screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if fragment is still attached to prevent crashes
            if (isAdded) {
                findNavController().navigate(R.id.action_googleAuth_to_clientLocation)
            }
        }, 1500)
    }
}