// sid: 15932
package com.example.trimly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminActivity
import com.example.trimly.ui.barber.BarberActivity
import com.example.trimly.ui.client.ClientActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val goToSignupText = view.findViewById<TextView>(R.id.tv_go_to_signup)

        // Navigate to Signup / Role Selection
        goToSignupText.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        // Real Firebase Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Login success – fetch role from Firestore
                        val userId = auth.currentUser!!.uid
                        db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role")
                                teleportToDashboard(role)
                            }
                            .addOnFailureListener {
                                // Firestore read failed – fallback to client
                                teleportToDashboard("client")
                            }
                    } else {
                        // Login failed
                        Toast.makeText(
                            requireContext(),
                            "Login failed: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun teleportToDashboard(role: String?) {
        val destination = when (role) {
            "admin" -> AdminActivity::class.java
            "barber" -> BarberActivity::class.java
            else -> ClientActivity::class.java    // default client
        }

        startActivity(Intent(requireActivity(), destination))
        requireActivity().finish()   // removes auth screen from backstack
    }
}