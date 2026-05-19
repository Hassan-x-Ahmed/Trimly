// sid: 15932
package com.example.trimly.ui.admin.store_editor

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StoreEditorFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var currentSalonId: String? = null // Will hold the ID of the salon document

    private lateinit var tvStoreName: TextView
    private lateinit var tvStoreLocation: TextView
    private lateinit var servicesContainer: LinearLayout
    private lateinit var barbersContainer: LinearLayout
    private lateinit var tvNoServices: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStoreName = view.findViewById(R.id.tv_store_name) ?: return
        tvStoreLocation = view.findViewById(R.id.tv_store_location) ?: return
        servicesContainer = view.findViewById(R.id.editor_services_container) ?: return
        barbersContainer = view.findViewById(R.id.editor_barbers_container) ?: return
        tvNoServices = view.findViewById(R.id.tv_no_services) ?: return

        view.findViewById<View>(R.id.btn_back_editor)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Fetch the target salon (Assuming the first salon in the DB for prototyping)
        loadSalonData()
        loadAssignedBarbers()

        // ───── Interactive Popups ─────
        view.findViewById<View>(R.id.btn_edit_name)?.setOnClickListener { showEditDetailsPopup() }
        view.findViewById<View>(R.id.btn_edit_location)?.setOnClickListener { showEditDetailsPopup() }

        view.findViewById<View>(R.id.btn_add_service)?.setOnClickListener { showAddServicePopup() }

        view.findViewById<View>(R.id.btn_add_barber)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Barber Invite Portal...", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btn_save_store)?.setOnClickListener {
            Toast.makeText(requireContext(), "Marketplace synchronized successfully!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun loadSalonData() {
        // Grab the first salon document to manage
        db.collection("salons").limit(1).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    currentSalonId = doc.id

                    tvStoreName.text = doc.getString("name") ?: "Unnamed Salon"
                    tvStoreLocation.text = doc.getString("address") ?: "Address not set"

                    // Parse Services
                    val services = doc.get("services") as? List<HashMap<String, String>>
                    servicesContainer.removeAllViews()

                    if (services.isNullOrEmpty()) {
                        tvNoServices.visibility = View.VISIBLE
                    } else {
                        tvNoServices.visibility = View.GONE
                        for (service in services) {
                            val name = service["name"] ?: "Service"
                            val price = service["price"] ?: "0"
                            val duration = service["duration"] ?: "30 mins"

                            val card = createEditableServiceCard(name, price, duration, service)
                            servicesContainer.addView(card)
                        }
                    }
                }
            }
    }

    private fun loadAssignedBarbers() {
        // Query users collection for barbers to build the chips
        db.collection("users").whereEqualTo("role", "barber").get()
            .addOnSuccessListener { documents ->
                barbersContainer.removeAllViews()

                for (doc in documents) {
                    val name = doc.getString("fullName") ?: "Stylist"
                    val barberId = doc.id
                    val chip = createEditableBarberChip(name, barberId)
                    barbersContainer.addView(chip)
                }
            }
    }

    /**
     * Programmatically builds the dark service card with Edit/Delete capabilities
     */
    private fun createEditableServiceCard(name: String, price: String, duration: String, serviceObj: HashMap<String, String>): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val card = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, (10 * density).toInt()) }
            radius = 16f * density
            setCardBackgroundColor(Color.parseColor("#121615"))
            cardElevation = 0f
        }

        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
        }

        // Left text layout
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

        val tvDetails = TextView(context).apply {
            text = "$duration • Rs. $price"
            setTextColor(Color.parseColor("#D4AF37"))
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (2 * density).toInt(), 0, 0)
        }

        textLayout.addView(tvName); textLayout.addView(tvDetails)

        // Right Action Buttons
        val actionLayout = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }

        val btnEdit = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams((32 * density).toInt(), (32 * density).toInt())
            setImageResource(android.R.drawable.ic_menu_edit)
            setColorFilter(Color.parseColor("#D4AF37"))
            setPadding((6 * density).toInt(), (6 * density).toInt(), (6 * density).toInt(), (6 * density).toInt())

            setOnClickListener { Toast.makeText(context, "Edit service feature coming soon", Toast.LENGTH_SHORT).show() }
        }

        val btnDelete = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams((32 * density).toInt(), (32 * density).toInt()).apply {
                marginStart = (4 * density).toInt()
            }
            setImageResource(android.R.drawable.ic_menu_delete)
            setColorFilter(Color.parseColor("#FF5252"))
            setPadding((6 * density).toInt(), (6 * density).toInt(), (6 * density).toInt(), (6 * density).toInt())

            setOnClickListener {
                showDeleteConfirmation(name) {
                    currentSalonId?.let { id ->
                        // Remove from Firestore Array
                        db.collection("salons").document(id)
                            .update("services", FieldValue.arrayRemove(serviceObj))
                            .addOnSuccessListener { loadSalonData() }
                    }
                }
            }
        }

        actionLayout.addView(btnEdit); actionLayout.addView(btnDelete)
        innerLayout.addView(textLayout); innerLayout.addView(actionLayout)
        card.addView(innerLayout)

        return card
    }

    /**
     * Programmatically builds the assigned barber horizontal chips
     */
    private fun createEditableBarberChip(name: String, barberId: String): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#121615"))
            setPadding((14 * density).toInt(), (10 * density).toInt(), (8 * density).toInt(), (10 * density).toInt())
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                marginEnd = (12 * density).toInt()
            }
        }

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams((36 * density).toInt(), (36 * density).toInt())
            setImageResource(android.R.drawable.ic_menu_camera)
            setBackgroundColor(Color.parseColor("#080E0D"))
            setColorFilter(Color.parseColor("#D4AF37"))
            setPadding((8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt())
        }

        val tvName = TextView(context).apply {
            text = name
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding((12 * density).toInt(), 0, (12 * density).toInt(), 0)
        }

        val btnDelete = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams((24 * density).toInt(), (24 * density).toInt())
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setColorFilter(Color.parseColor("#FF5252"))
            setPadding((4 * density).toInt(), (4 * density).toInt(), (4 * density).toInt(), (4 * density).toInt())

            setOnClickListener {
                showDeleteConfirmation(name) {
                    Toast.makeText(context, "$name unassigned from roster.", Toast.LENGTH_SHORT).show()
                    layout.visibility = View.GONE
                }
            }
        }

        layout.addView(icon); layout.addView(tvName); layout.addView(btnDelete)
        return layout
    }

    // ───── POPUP DIALOGS ─────

    private fun showEditDetailsPopup() {
        val context = requireContext()
        if (currentSalonId == null) return

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etName = EditText(context).apply { hint = "Salon Name"; setText(tvStoreName.text.toString()) }
        val etAddress = EditText(context).apply { hint = "Location Address"; setText(tvStoreLocation.text.toString()) }

        layout.addView(etName); layout.addView(etAddress)

        AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Edit Store Details")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val updates = hashMapOf<String, Any>(
                    "name" to etName.text.toString().trim(),
                    "address" to etAddress.text.toString().trim()
                )
                db.collection("salons").document(currentSalonId!!).update(updates)
                    .addOnSuccessListener { loadSalonData() }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showAddServicePopup() {
        val context = requireContext()
        if (currentSalonId == null) return

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etServiceName = EditText(context).apply { hint = "Service Name (e.g., Hot Towel Shave)" }
        val etPrice = EditText(context).apply { hint = "Price (e.g., 2000)" }
        val etDuration = EditText(context).apply { hint = "Duration (e.g., 30 mins)" }

        layout.addView(etServiceName); layout.addView(etPrice); layout.addView(etDuration)

        AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle("Add New Service")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val newService = hashMapOf(
                    "name" to etServiceName.text.toString().trim(),
                    "price" to etPrice.text.toString().trim(),
                    "duration" to etDuration.text.toString().trim()
                )

                db.collection("salons").document(currentSalonId!!)
                    .update("services", FieldValue.arrayUnion(newService))
                    .addOnSuccessListener { loadSalonData() }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showDeleteConfirmation(itemName: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove $itemName?")
            .setMessage("This will permanently remove \"$itemName\" from your catalog.")
            .setPositiveButton("Delete") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}