// sid: 15932
package com.example.trimly.ui.admin.staff_manager

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
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminStaffFragment : Fragment(R.layout.fragment_staff_manager) {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var pendingContainer: LinearLayout
    private lateinit var activeContainer: LinearLayout
    private lateinit var tvNoActiveStaff: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pendingContainer = view.findViewById(R.id.pending_staff_container) ?: return
        activeContainer = view.findViewById(R.id.active_staff_container) ?: return
        tvNoActiveStaff = view.findViewById(R.id.tv_no_active_staff) ?: return

        // 1. Load Data
        loadMockPendingApplicant()
        loadActiveStaff()

        // 2. Action Buttons
        view.findViewById<Button>(R.id.btn_add_staff)?.setOnClickListener {
            Toast.makeText(requireContext(), "Generating invite code for new stylist…", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btn_manage_closures)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening master salon calendar override…", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Reaches into Firebase to find all registered Barbers and build their roster cards
     */
    private fun loadActiveStaff() {
        db.collection("users").whereEqualTo("role", "barber").get()
            .addOnSuccessListener { documents ->
                activeContainer.removeAllViews()

                if (documents.isEmpty) {
                    tvNoActiveStaff.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                tvNoActiveStaff.visibility = View.GONE

                var chairNum = 1
                for (doc in documents) {
                    val name = doc.getString("fullName") ?: "Stylist"
                    val title = doc.getString("title") ?: "Professional Barber"

                    // Build the row!
                    val staffRow = createActiveStaffRow(chairNum, name, title)
                    activeContainer.addView(staffRow)
                    chairNum++
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load staff list.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Programmatically builds the dark green active staff row with the toggle switch
     */
    private fun createActiveStaffRow(chairNum: Int, name: String, title: String): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val rowOuter = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#CC1A2422"))
            setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, (12 * density).toInt())
            }
        }

        val rowInner = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        // Chair Badge (C1, C2, etc.)
        val tvChairBadge = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams((28 * density).toInt(), (28 * density).toInt())
            text = "C$chairNum"
            setTextColor(Color.parseColor("#2E7D32"))
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#E8F5E9"))
        }

        // Name and Info
        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (12 * density).toInt()
            }
        }

        val tvName = TextView(context).apply {
            text = "$name ($title)"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvShift = TextView(context).apply {
            text = "Shift: 01:00 PM - 09:00 PM"
            setTextColor(Color.parseColor("#999999"))
            textSize = 12f
        }

        textLayout.addView(tvName); textLayout.addView(tvShift)

        // The Active Switch
        val statusSwitch = Switch(context).apply {
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                val status = if (isChecked) "Clocked In" else "Clocked Out"
                Toast.makeText(context, "$name is now $status", Toast.LENGTH_SHORT).show()

                // Dim the row if they clock out
                rowOuter.alpha = if (isChecked) 1.0f else 0.5f
            }
        }

        rowInner.addView(tvChairBadge)
        rowInner.addView(textLayout)
        rowInner.addView(statusSwitch)
        rowOuter.addView(rowInner)

        return rowOuter
    }

    /**
     * Programmatically injects one mock "Pending" application for demo purposes
     */
    private fun loadMockPendingApplicant() {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FFF8E1")) // Yellow
            setPadding((20 * density).toInt(), (20 * density).toInt(), (20 * density).toInt(), (20 * density).toInt())
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins((24 * density).toInt(), 0, (24 * density).toInt(), (24 * density).toInt())
            }
        }

        val header = TextView(context).apply {
            text = "PENDING APPLICATION"
            setTextColor(Color.parseColor("#F57F17"))
            textSize = 10f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val infoRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, (12 * density).toInt(), 0, 0)
        }

        val imgIcon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams((48 * density).toInt(), (48 * density).toInt())
            setImageResource(android.R.drawable.ic_menu_camera)
            setBackgroundColor(Color.parseColor("#FFECB3"))
            setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
            setColorFilter(Color.parseColor("#F57F17"))
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (16 * density).toInt()
            }
        }

        val tvName = TextView(context).apply {
            text = "Applicant: Nabeel Khan"
            setTextColor(Color.parseColor("#1A1A1A"))
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvDetails = TextView(context).apply {
            text = "Experience: 4 Years  •  Portfolio: 12 Cuts"
            setTextColor(Color.parseColor("#757575"))
            textSize = 12f
        }

        textLayout.addView(tvName); textLayout.addView(tvDetails)
        infoRow.addView(imgIcon); infoRow.addView(textLayout)

        // Action Buttons Row
        val buttonRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, (16 * density).toInt(), 0, 0)
        }

        val btnDecline = Button(context).apply {
            text = "Decline"
            setTextColor(Color.parseColor("#D32F2F"))
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEBEE"))
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (38 * density).toInt()).apply {
                marginEnd = (12 * density).toInt()
            }
            setOnClickListener {
                pendingContainer.removeAllViews() // Hide the card
                Toast.makeText(context, "Application declined.", Toast.LENGTH_SHORT).show()
            }
        }

        val btnApprove = Button(context).apply {
            text = "Approve & Assign"
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#2E7D32"))
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (38 * density).toInt())
            setOnClickListener {
                pendingContainer.removeAllViews() // Hide the card
                Toast.makeText(context, "Nabeel approved and assigned!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonRow.addView(btnDecline); buttonRow.addView(btnApprove)

        card.addView(header)
        card.addView(infoRow)
        card.addView(buttonRow)

        pendingContainer.addView(card)
    }
}