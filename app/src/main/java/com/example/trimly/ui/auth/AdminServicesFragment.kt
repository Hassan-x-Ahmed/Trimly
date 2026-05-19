// sid: 15932
package com.example.trimly.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminServicesFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 1. Connect to the Shared Memory Bank one last time
    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.back_btn)?.setOnClickListener {
            findNavController().navigateUp()
        }

        val btnFinish = view.findViewById<Button>(R.id.btn_finish_admin)
        btnFinish?.setOnClickListener {

            btnFinish.isEnabled = false
            btnFinish.text = "Creating Salon Server..."

            val servicesList = mutableListOf<HashMap<String, String>>()

            val switch1 = view.findViewById<Switch>(R.id.switch_service_1)
            val price1 = view.findViewById<EditText>(R.id.et_price_1)
            if (switch1?.isChecked == true) {
                servicesList.add(hashMapOf(
                    "name" to "Executive Skin Fade",
                    "price" to price1?.text.toString().trim(),
                    "duration" to "45 mins"
                ))
            }

            val switch2 = view.findViewById<Switch>(R.id.switch_service_2)
            val price2 = view.findViewById<EditText>(R.id.et_price_2)
            if (switch2?.isChecked == true) {
                servicesList.add(hashMapOf(
                    "name" to "Beard Sculpting & Spa",
                    "price" to price2?.text.toString().trim(),
                    "duration" to "30 mins"
                ))
            }

            createSalonInDatabase(servicesList, btnFinish)
        }
    }

    private fun createSalonInDatabase(servicesList: List<HashMap<String, String>>, btnFinish: Button) {
        val adminId = auth.currentUser?.uid
        if (adminId == null) {
            Toast.makeText(requireContext(), "Auth Error: Not logged in.", Toast.LENGTH_SHORT).show()
            btnFinish.isEnabled = true
            btnFinish.text = "FINISH SETUP & LAUNCH SALON"
            return
        }

        // 2. Master Salon Payload using the Memory Bank!
        val salonData = hashMapOf(
            "ownerId" to adminId,
            "name" to sharedViewModel.salonName,
            "address" to sharedViewModel.salonAddress,
            "phone" to sharedViewModel.salonPhone,
            "hours" to sharedViewModel.salonHours,
            "services" to servicesList,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("salons").add(salonData)
            .addOnSuccessListener { documentReference ->
                val newSalonId = documentReference.id

                db.collection("users").document(adminId)
                    .update("salonId", newSalonId)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Salon Launched!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.setupSuccessFragment)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error linking Admin profile.", Toast.LENGTH_SHORT).show()
                        btnFinish.isEnabled = true
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Database Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnFinish.isEnabled = true
                btnFinish.text = "FINISH SETUP & LAUNCH SALON"
            }
    }
}