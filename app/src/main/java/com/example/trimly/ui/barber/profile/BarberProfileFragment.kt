// sid: 15932
package com.example.trimly.ui.barber.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.example.trimly.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BarberProfileFragment : Fragment(R.layout.fragment_barber_profile) {

    // 1. Just the standard Database engines (No Storage needed!)
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 2. UI Variables
    private lateinit var tvName: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvBio: TextView
    private lateinit var imgAvatar: ImageView
    private lateinit var btnEditProfile: LinearLayout
    private lateinit var btnManageServices: LinearLayout
    private lateinit var btnLogout: LinearLayout
    private lateinit var btnAddPortfolio: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tv_barber_name) ?: return
        tvTitle = view.findViewById(R.id.tv_barber_title) ?: return
        tvBio = view.findViewById(R.id.tv_barber_bio) ?: return
        imgAvatar = view.findViewById(R.id.img_avatar) ?: return
        btnEditProfile = view.findViewById(R.id.btn_edit_profile) ?: return
        btnManageServices = view.findViewById(R.id.btn_manage_services) ?: return
        btnLogout = view.findViewById(R.id.btn_logout) ?: return
        btnAddPortfolio = view.findViewById(R.id.btn_add_portfolio) ?: return

        loadProfileData()

        btnEditProfile.setOnClickListener { showEditProfilePopup() }

        // Wire up the new Services Editor!
        btnManageServices.setOnClickListener { showAddServicePopup() }

        // Disabled the photo buttons for now since we skipped Storage
        imgAvatar.setOnClickListener {
            Toast.makeText(requireContext(), "Photo uploads skipped for now.", Toast.LENGTH_SHORT).show()
        }
        btnAddPortfolio.setOnClickListener {
            Toast.makeText(requireContext(), "Portfolio skipped for now.", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    tvName.text = document.getString("fullName") ?: "New Barber"
                    tvTitle.text = document.getString("title") ?: "Stylist"
                    tvBio.text = document.getString("bio") ?: "Tap Edit Personal Details to add your bio."
                }
            }
    }

    private fun showEditProfilePopup() {
        val context = requireContext()
        val userId = auth.currentUser?.uid ?: return

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etName = EditText(context).apply { hint = "Full Name"; setText(tvName.text.toString()) }
        val etTitle = EditText(context).apply { hint = "Title"; setText(tvTitle.text.toString()) }
        val etBio = EditText(context).apply { hint = "Bio"; setText(tvBio.text.toString()) }

        layout.addView(etName); layout.addView(etTitle); layout.addView(etBio)

        AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val updates = hashMapOf<String, Any>(
                    "fullName" to etName.text.toString().trim(),
                    "title" to etTitle.text.toString().trim(),
                    "bio" to etBio.text.toString().trim()
                )
                db.collection("users").document(userId).update(updates).addOnSuccessListener { loadProfileData() }
            }
            .setNegativeButton("Cancel", null).show()
    }

    /**
     * NEW: Allows the Barber to add a Service (e.g. Skin Fade - 1500) to their database
     */
    private fun showAddServicePopup() {
        val context = requireContext()
        val userId = auth.currentUser?.uid ?: return

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etServiceName = EditText(context).apply { hint = "Service Name (e.g., Skin Fade)" }
        val etPrice = EditText(context).apply { hint = "Price (e.g., 1500)" }
        val etDuration = EditText(context).apply { hint = "Duration (e.g., 45 mins)" }

        layout.addView(etServiceName); layout.addView(etPrice); layout.addView(etDuration)

        AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Add New Service")
            .setView(layout)
            .setPositiveButton("Add to Profile") { _, _ ->
                val sName = etServiceName.text.toString().trim()
                val sPrice = etPrice.text.toString().trim()
                val sDuration = etDuration.text.toString().trim()

                if (sName.isNotEmpty() && sPrice.isNotEmpty()) {
                    // Create a mini-map for this specific service
                    val newService = hashMapOf(
                        "name" to sName,
                        "price" to sPrice,
                        "duration" to sDuration
                    )

                    // Add it to a "services" list inside the Barber's profile using ArrayUnion
                    db.collection("users").document(userId)
                        .update("services", com.google.firebase.firestore.FieldValue.arrayUnion(newService))
                        .addOnSuccessListener {
                            Toast.makeText(context, "$sName added successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error adding service.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null).show()
    }
}