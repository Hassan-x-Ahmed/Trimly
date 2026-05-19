// sid: 15932
package com.example.trimly.ui.admin.staff_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminViewModel
class AdminStaffFragment : Fragment(R.layout.fragment_staff_manager) {

    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var adapter: BarberAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvStaff = view.findViewById<RecyclerView>(R.id.rv_staff_list)
        val tvEmptyState = view.findViewById<TextView>(R.id.tv_empty_state)
        val btnAddBarber = view.findViewById<Button>(R.id.btn_add_barber)

        // Setup the list
        adapter = BarberAdapter(emptyList())
        rvStaff.layoutManager = LinearLayoutManager(requireContext())
        rvStaff.adapter = adapter

        // Watch the ViewModel for changes
        adminViewModel.barbersList.observe(viewLifecycleOwner) { barbers ->
            if (barbers.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvStaff.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvStaff.visibility = View.VISIBLE
                // adapter.updateData(barbers)
            }
        }

        // Show the Custom XML Dialog
        btnAddBarber.setOnClickListener {
            showAddBarberDialog()
        }
    }

    private fun showAddBarberDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_barber, null)
        val emailInput = dialogView.findViewById<EditText>(R.id.et_barber_email)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Invite", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnPositive.setTextColor(resources.getColor(android.R.color.holo_orange_light, null))

            btnPositive.setOnClickListener {
                val email = emailInput.text.toString().trim()
                if (email.isEmpty()) {
                    emailInput.error = "Email required"
                    return@setOnClickListener
                }

                btnPositive.isEnabled = false
                btnPositive.text = "Searching..."

                adminViewModel.addBarberByEmail(email) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        dialog.dismiss()
                    } else {
                        btnPositive.isEnabled = true
                        btnPositive.text = "Invite"
                    }
                }
            }
        }
        dialog.show()
    }

    // --- Inner Adapter Class ---
    inner class BarberAdapter(private var barbers: List<Any>) :
        RecyclerView.Adapter<BarberAdapter.BarberViewHolder>() {

        inner class BarberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_barber_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
            // UPDATED: Now inflating the new admin-specific layout name!
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_barber_card, parent, false)
            return BarberViewHolder(view)
        }

        override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
            holder.tvName.text = "Example Barber"
        }

        override fun getItemCount(): Int = barbers.size

        fun updateData(newBarbers: List<Any>) {
            barbers = newBarbers
            notifyDataSetChanged()
        }
    }
}