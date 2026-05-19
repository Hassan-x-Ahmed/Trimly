// sid: 15932
package com.example.trimly.ui.client.salon_details

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.firestore.FirebaseFirestore

class SalonDetailsFragment : Fragment(R.layout.fragment_salon_details) {

    private val db = FirebaseFirestore.getInstance()
    private var salonId: String? = null
    private var loadedSalonName: String = "Premium Salon"

    // UI Elements
    private lateinit var tvSalonName: TextView
    private lateinit var tvSalonAddress: TextView
    private lateinit var servicesContainer: LinearLayout
    private lateinit var tvTotalPrice: TextView

    // Calculator State
    private var currentTotal: Int = 0
    private val selectedServicesNames = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Grab the Salon ID passed from the Search or Home Fragment
        salonId = arguments?.getString("SALON_ID")

        tvSalonName = view.findViewById(R.id.tv_salon_name_header) ?: return
        tvSalonAddress = view.findViewById(R.id.tv_salon_address_header) ?: return
        servicesContainer = view.findViewById(R.id.salon_services_container) ?: return
        tvTotalPrice = view.findViewById(R.id.tv_total_price) ?: return

        // 2. Load the data
        if (salonId != null) {
            fetchSalonDetails(salonId!!)
        } else {
            Toast.makeText(requireContext(), "Error finding salon.", Toast.LENGTH_SHORT).show()
        }

        // 3. Back Anchor
        view.findViewById<ImageView>(R.id.btn_back_discovery)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // 4. Booking Anchor -> Pass Data to Checkout
        view.findViewById<Button>(R.id.btn_proceed_checkout)?.setOnClickListener {
            if (currentTotal == 0) {
                Toast.makeText(requireContext(), "Please select at least one service.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Combine selected service names (e.g., "Skin Fade + Beard Spa")
            val combinedServiceNames = selectedServicesNames.joinToString(" + ")

            // Bundle the data for CheckoutFragment
            val bundle = Bundle().apply {
                putString("BARBER_ID", salonId) // Repurposing BARBER_ID for Salon
                putString("SALON_NAME", loadedSalonName)
                putString("SERVICE_NAME", combinedServiceNames)
                putString("PRICE", currentTotal.toString())
            }

            try {
                findNavController().navigate(R.id.action_details_to_checkout, bundle)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Navigation Error: Make sure action_details_to_checkout exists in nav_graph!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchSalonDetails(id: String) {
        db.collection("salons").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    loadedSalonName = document.getString("name") ?: "Premium Salon"
                    tvSalonName.text = loadedSalonName
                    tvSalonAddress.text = document.getString("address") ?: "Location unavailable"

                    // Extract the services array
                    val services = document.get("services") as? List<HashMap<String, String>>

                    servicesContainer.removeAllViews()

                    if (!services.isNullOrEmpty()) {
                        for (service in services) {
                            val name = service["name"] ?: "Service"
                            val desc = service["desc"] ?: "Premium grooming service."
                            val duration = service["duration"] ?: "30 mins"
                            val priceStr = service["price"] ?: "0"

                            // Clean the price string into a pure integer for the calculator
                            val priceInt = priceStr.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

                            val serviceCard = createServiceCard(name, desc, duration, priceInt)
                            servicesContainer.addView(serviceCard)
                        }
                    } else {
                        // Empty State
                        val tvNoServices = TextView(requireContext()).apply {
                            text = "No services listed yet."
                            setTextColor(Color.parseColor("#666666"))
                            setPadding(0, 20, 0, 40)
                        }
                        servicesContainer.addView(tvNoServices)
                    }
                }
            }
    }

    /**
     * Programmatically builds the beautiful service card with the live toggle switch
     */
    private fun createServiceCard(name: String, desc: String, duration: String, priceInt: Int): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val card = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (12 * density).toInt())
            }
            radius = 16f * density
            setCardBackgroundColor(Color.parseColor("#CC1A2422"))
            cardElevation = 0f
        }

        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
        }

        // Left side texts
        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvName = TextView(context).apply {
            text = name
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvDesc = TextView(context).apply {
            text = desc
            setTextColor(Color.parseColor("#999999"))
            textSize = 11f
            setPadding(0, (4 * density).toInt(), 0, 0)
        }

        val tvDetails = TextView(context).apply {
            text = "$duration  •  Rs. $priceInt"
            setTextColor(Color.parseColor("#D4AF37"))
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (8 * density).toInt(), 0, 0)
        }

        textLayout.addView(tvName); textLayout.addView(tvDesc); textLayout.addView(tvDetails)

        // The Magic Switch!
        val serviceSwitch = Switch(context).apply {
            isChecked = false

            // Live Calculator Logic
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentTotal += priceInt
                    selectedServicesNames.add(name)
                } else {
                    currentTotal -= priceInt
                    selectedServicesNames.remove(name)
                }

                // Update the sticky bottom bar
                tvTotalPrice.text = "Rs. $currentTotal"
            }
        }

        innerLayout.addView(textLayout)
        innerLayout.addView(serviceSwitch)
        card.addView(innerLayout)

        return card
    }
}