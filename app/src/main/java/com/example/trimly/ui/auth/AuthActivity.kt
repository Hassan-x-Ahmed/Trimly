// sid: 15932
package com.example.trimly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trimly.MainActivity
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminActivity
import com.example.trimly.ui.barber.BarberActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {

    // Initialize our Firebase Engines
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Check if the user is already logged in BEFORE drawing the screen
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // 2. They are logged in! Fetch their exact role from Firestore
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    teleportToIsland(role)
                }
                .addOnFailureListener {
                    // If the database fails (no internet), show the login UI so they aren't stuck
                    setupLoginUI()
                }
        } else {
            // 3. No user is logged in. Draw the Role Selection UI!
            setupLoginUI()
        }
    }

    /**
     * This function draws the screen and wires up your clickable Role cards.
     * Note: We are loading the fragment layout here because that is where your cards are designed!
     */
    private fun setupLoginUI() {
        setContentView(R.layout.fragment_role_selection)

        findViewById<MaterialCardView>(R.id.card_client)?.setOnClickListener { registerAndAssignRole("client") }
        findViewById<MaterialCardView>(R.id.card_barber)?.setOnClickListener { registerAndAssignRole("barber") }
        findViewById<MaterialCardView>(R.id.card_admin)?.setOnClickListener { registerAndAssignRole("admin") }
    }

    /**
     * This function handles the Firebase Registration and Firestore Role Stamping
     */
    private fun registerAndAssignRole(selectedRole: String) {
        Toast.makeText(this, "Connecting to Secure Servers...", Toast.LENGTH_SHORT).show()

        val testEmail = "test_${selectedRole}_${System.currentTimeMillis()}@trimly.com"
        val testPassword = "Password123!"

        auth.createUserWithEmailAndPassword(testEmail, testPassword)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                val userProfile = hashMapOf(
                    "email" to testEmail,
                    "role" to selectedRole,
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("users").document(userId)
                    .set(userProfile)
                    .addOnSuccessListener {
                        teleportToIsland(selectedRole)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Auth Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * The Teleporter: Sends the user to the correct dashboard and locks the back button
     */
    private fun teleportToIsland(role: String?) {
        val destinationClass = when (role) {
            "admin" -> AdminActivity::class.java
            "barber" -> BarberActivity::class.java
            else -> MainActivity::class.java // Client
        }

        val intent = Intent(this, destinationClass)
        // This flag destroys the login screen so the user can't press 'back' to return to it
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}