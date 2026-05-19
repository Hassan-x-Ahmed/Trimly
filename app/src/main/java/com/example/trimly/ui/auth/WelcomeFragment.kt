// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R

class WelcomeFragment : Fragment(R.layout.welcome_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logo = view.findViewById<ImageView>(R.id.img_logo)
        val title = view.findViewById<TextView>(R.id.tv_app_title)

        val btnSignup = view.findViewById<Button>(R.id.btn_to_signup)
        val btnLogin = view.findViewById<Button>(R.id.btn_to_login)

        // Load animations safely
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        // Apply animations only if views exist
        logo?.startAnimation(fadeIn)
        title?.startAnimation(fadeIn)

        btnSignup?.startAnimation(slideUp)
        btnLogin?.startAnimation(slideUp)

        // Navigation
        btnSignup?.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_roleSelection)
        }
        btnLogin?.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_login)
        }
    }
}