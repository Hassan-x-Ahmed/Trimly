// sid: 15932
package com.example.trimly.ui.client.checkout

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.example.trimly.ui.client.bookings.BookingSuccessFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Variables for the data passed from the Salon profile
    private var barberId: String = "unknown_barber"
    private var serviceName: String = "Executive Skin Fade"
    private var price: String = "3500"
    private var salonName: String = "Élysian Noir"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Grab arguments passed from the previous screen (if any exist)
        arguments?.let {
            barberId = it.getString("BARBER_ID", barberId)
            serviceName = it.getString("SERVICE_NAME", serviceName)
            price = it.getString("PRICE", price)
            salonName = it.getString("SALON_NAME", salonName)
        }

        // 2. Populate the UI with the dynamic data
        view.findViewById<TextView>(R.id.tv_checkout_service_name)?.text = serviceName
        view.findViewById<TextView>(R.id.tv_checkout_salon_info)?.text = "$salonName  •  45 mins"
        view.findViewById<TextView>(R.id.tv_checkout_service_price)?.text = "Rs. $price"
        view.findViewById<TextView>(R.id.tv_checkout_total_price)?.text = "Rs. $price"

        // 3. Back Anchor: Return smoothly to the Salon Details
        view.findViewById<ImageView>(R.id.btn_back_details)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // 4. Complete Checkout & Push to Cloud
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm_reservation)
        btnConfirm?.setOnClickListener {

            // Disable button to prevent double-booking
            btnConfirm.isEnabled = false
            btnConfirm.text = "Processing..."

            createBookingTicketInCloud()
        }
    }

    private fun createBookingTicketInCloud() {
        val clientId = auth.currentUser?.uid

        if (clientId == null) {
            Toast.makeText(requireContext(), "Error: Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the actual database object
        val appointmentData = hashMapOf(
            "clientId" to clientId,
            "barberId" to barberId,
            "barberName" to salonName,
            "service" to serviceName,
            "price" to price,
            "time" to "Tomorrow, 6:30 PM", // Keeping time static based on UI selection for now
            "status" to "PENDING" // This triggers it to show up on the Barber's "Requests" tab!
        )

        // Push it to the 'appointments' collection
        db.collection("appointments").add(appointmentData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Secured! Notifying salon...", Toast.LENGTH_SHORT).show()

                // Navigate directly to the destination ID
                findNavController().navigate(R.id.bookingSuccessFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Booking failed: ${e.message}", Toast.LENGTH_LONG).show()
                view?.findViewById<Button>(R.id.btn_confirm_reservation)?.isEnabled = true
                view?.findViewById<Button>(R.id.btn_confirm_reservation)?.text = "Confirm Booking"
            }
    }
}