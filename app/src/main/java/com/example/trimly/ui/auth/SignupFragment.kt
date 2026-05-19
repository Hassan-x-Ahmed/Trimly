// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val role = authViewModel.selectedRole   // <-- Get role from shared ViewModel

        val etName = view.findViewById<EditText>(R.id.et_signup_name)
        val etEmail = view.findViewById<EditText>(R.id.et_signup_email)
        val etPassword = view.findViewById<EditText>(R.id.et_signup_password)
        val btnSignup = view.findViewById<Button>(R.id.btn_signup)
        val tvGoToLogin = view.findViewById<TextView>(R.id.tv_go_to_login)

        tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent double-taps
            btnSignup.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    btnSignup.isEnabled = true   // re-enable

                    if (task.isSuccessful) {
                        val userId = auth.currentUser!!.uid
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "role" to role,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )

                        db.collection("users").document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                when (role) {
                                    "client" -> findNavController().navigate(R.id.clientSetupFragment)
                                    "barber" -> findNavController().navigate(R.id.barberSetupFragment)
                                    "admin" -> findNavController().navigate(R.id.adminSetupFragment)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(requireContext(), "Signup failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}