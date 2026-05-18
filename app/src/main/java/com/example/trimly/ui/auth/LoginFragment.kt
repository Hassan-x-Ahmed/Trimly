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

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Navigation to Signup Screen
        val goToSignupText = view.findViewById<TextView>(R.id.tv_go_to_signup)
        goToSignupText.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        // 2. MOCK LOGIN: Click the Login Button to open the app
        val loginButton = view.findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            // Create an "Intent" to launch the ClientActivity
            val intent = Intent(requireActivity(), ClientActivity::class.java)
            startActivity(intent)

            // Finish the AuthActivity so the user can't press the "Back" button to return to the login screen
            requireActivity().finish()
        }
    }
}