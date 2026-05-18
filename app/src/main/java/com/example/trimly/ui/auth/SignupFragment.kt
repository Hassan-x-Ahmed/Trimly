package com.example.trimly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.example.trimly.ui.client.ClientActivity

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Navigation back to Login Screen
        val goToLoginText = view.findViewById<TextView>(R.id.tv_go_to_login)
        goToLoginText.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }

        // 2. MOCK SIGNUP: Click the Sign Up Button to open the app
        val signupButton = view.findViewById<Button>(R.id.btn_signup)
        signupButton.setOnClickListener {
            // Create an "Intent" to launch the ClientActivity
            val intent = Intent(requireActivity(), ClientActivity::class.java)
            startActivity(intent)

            // Finish the AuthActivity
            requireActivity().finish()
        }
    }
}