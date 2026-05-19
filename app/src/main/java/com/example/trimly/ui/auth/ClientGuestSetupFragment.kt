// sid: 15932
package com.example.trimly.ui.auth

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClientGuestSetupFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // UI Variables
    private lateinit var etName: EditText
    private lateinit var btnFinish: Button

    // Tracking User Selections
    private var selectedAvatarIndex = 1
    private val selectedServices = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_client_guest_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.et_guest_name)
        btnFinish = view.findViewById(R.id.btn_finish_guest)

        // 1. Back Button
        view.findViewById<ImageView>(R.id.back_btn).setOnClickListener {
            findNavController().navigateUp()
        }

        // 2. Setup Interactive Avatar Selection
        setupAvatarSelection(view)

        // 3. Setup Interactive Service Chips
        setupServiceChips(view)

        // 4. Handle Final Account Creation
        btnFinish.setOnClickListener {
            val typedName = etName.text.toString().trim()

            if (typedName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a name for your bookings.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnFinish.isEnabled = false
            btnFinish.text = "Creating Profile..."

            createGuestProfileInFirebase(typedName)
        }
    }

    private fun setupAvatarSelection(view: View) {
        val avatars = listOf(
            view.findViewById<ImageView>(R.id.avatar_1),
            view.findViewById<ImageView>(R.id.avatar_2),
            view.findViewById<ImageView>(R.id.avatar_3),
            view.findViewById<ImageView>(R.id.avatar_4)
        )

        avatars.forEachIndexed { index, imageView ->
            imageView?.setOnClickListener {
                selectedAvatarIndex = index + 1

                // Reset all avatars to dark green
                avatars.forEach { it?.setBackgroundColor(Color.parseColor("#CC1A2422")) }
                avatars.forEach { it?.setColorFilter(Color.parseColor("#D4AF37")) } // Gold icon

                // Highlight the selected one in Gold
                imageView.setBackgroundColor(Color.parseColor("#D4AF37"))
                imageView.setColorFilter(Color.parseColor("#080E0D")) // Dark icon
            }
        }
    }

    private fun setupServiceChips(view: View) {
        val chipStyling = view.findViewById<Button>(R.id.chip_styling)
        val chipColoring = view.findViewById<Button>(R.id.chip_coloring)
        val chipHaircuts = view.findViewById<Button>(R.id.chip_haircuts)

        val toggleChip = { btn: Button, serviceName: String ->
            if (selectedServices.contains(serviceName)) {
                // Deselect
                selectedServices.remove(serviceName)
                btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CC1A2422"))
                btn.setTextColor(Color.WHITE)
            } else {
                // Select
                selectedServices.add(serviceName)
                btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D4AF37"))
                btn.setTextColor(Color.parseColor("#080E0D"))
            }
        }

        chipStyling?.setOnClickListener { toggleChip(chipStyling, "Men's Styling") }
        chipColoring?.setOnClickListener { toggleChip(chipColoring, "Coloring") }
        chipHaircuts?.setOnClickListener { toggleChip(chipHaircuts, "Haircuts") }
    }

    private fun createGuestProfileInFirebase(name: String) {
        // Generate a fast mock account so we have a real UID
        val guestEmail = "guest_${System.currentTimeMillis()}@trimly.com"
        val guestPass = "GuestPass123!"

        auth.createUserWithEmailAndPassword(guestEmail, guestPass)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                // Package all their manual selections
                val userProfile = hashMapOf(
                    "email" to guestEmail,
                    "fullName" to name,
                    "role" to "client",
                    "isGuest" to true,
                    "avatarId" to selectedAvatarIndex,
                    "preferredServices" to selectedServices,
                    "createdAt" to System.currentTimeMillis()
                )

                // Save to Firestore
                db.collection("users").document(userId)
                    .set(userProfile)
                    .addOnSuccessListener {
                        // Success! Teleport to the map
                        try {
                            findNavController().navigate(R.id.action_guestSetup_to_clientLocation)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Routing Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Database Error.", Toast.LENGTH_SHORT).show()
                        btnFinish.isEnabled = true
                        btnFinish.text = "Finish & Book"
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Auth Error: ${e.message}", Toast.LENGTH_SHORT).show()
                btnFinish.isEnabled = true
                btnFinish.text = "Finish & Book"
            }
    }
}