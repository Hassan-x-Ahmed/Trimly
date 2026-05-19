// sid: 15932
package com.example.trimly.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminActivity
import com.example.trimly.ui.barber.BarberActivity
import com.example.trimly.ui.client.ClientActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Set the Shell (which automatically loads the Welcome screen first)
        setContentView(R.layout.activity_auth)

        // 2. Check if a user is already logged in
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // 3. They are logged in — read their role and teleport
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role")
                        teleportToIsland(role)
                    } else {
                        // Document missing — force logout to restart clean
                        auth.signOut()
                        // Allow the welcome screen to show naturally
                    }
                }
                .addOnFailureListener {
                    // Network error — show a brief message and let them use the welcome screen
                    Toast.makeText(this, "Could not sync session. Please log in.", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
        }
        // If currentUser == null, the welcome screen appears automatically
    }

    /**
     * Sends the user to the correct dashboard and destroys the auth screens.
     */
    private fun teleportToIsland(role: String?) {
        val destinationClass = when (role) {
            "admin" -> AdminActivity::class.java
            "barber" -> BarberActivity::class.java
            else -> ClientActivity::class.java   // default client, no MainActivity!
        }

        val intent = Intent(this, destinationClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}