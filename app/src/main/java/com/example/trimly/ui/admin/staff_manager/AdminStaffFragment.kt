// sid: 15932
package com.example.trimly.ui.admin.staff_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.model.User
import com.example.trimly.ui.admin.AdminViewModel
import com.google.android.material.button.MaterialButton

data class BarberStaff(
    val name: String,
    val details: String,
    val extraInfo: String,
    val isPending: Boolean
)

class AdminStaffFragment : Fragment(R.layout.fragment_staff_manager) {

    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var pendingAdapter: PendingAdapter
    private lateinit var activeAdapter: ActiveAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvPending = view.findViewById<RecyclerView>(R.id.rv_pending_barbers)
        val rvActive = view.findViewById<RecyclerView>(R.id.rv_active_barbers)

        pendingAdapter = PendingAdapter(emptyList())
        activeAdapter = ActiveAdapter(emptyList())

        rvPending.layoutManager = LinearLayoutManager(requireContext())
        rvActive.layoutManager = LinearLayoutManager(requireContext())

        rvPending.adapter = pendingAdapter
        rvActive.adapter = activeAdapter

        // OBSERVE REAL-TIME FIREBASE DATA
        adminViewModel.barbersList.observe(viewLifecycleOwner) { firebaseBarbers ->
            if (firebaseBarbers.isEmpty()) {
                activeAdapter.updateData(emptyList())
                return@observe
            }

            val realActiveList = firebaseBarbers.map { barber ->
                BarberStaff(
                    name = barber.name ?: "Unknown Barber", // Falls back gracefully
                    details = "Chair Assigned",
                    extraInfo = "Active",
                    isPending = false
                )
            }
            activeAdapter.updateData(realActiveList)
        }

        view.findViewById<TextView>(R.id.btn_add_barber_mockup)?.setOnClickListener {
            showAddBarberDialog()
        }

        // Dummy pending data to keep the UI looking complete
        pendingAdapter.updateData(listOf(
            BarberStaff("Bilal Ahmed", "5 Years Experience", "Applied on 13 May 2026", true)
        ))
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
            btnPositive.setTextColor(android.graphics.Color.parseColor("#6C5CE7"))

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

    inner class PendingAdapter(private var barbers: List<BarberStaff>) : RecyclerView.Adapter<PendingAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_pending_name)
            val tvExp: TextView = view.findViewById(R.id.tv_pending_exp)
            val tvDate: TextView = view.findViewById(R.id.tv_pending_date)
            val btnApprove: MaterialButton = view.findViewById(R.id.btn_approve)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_pending_barber, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val barber = barbers[position]
            holder.tvName.text = barber.name
            holder.tvExp.text = barber.details
            holder.tvDate.text = barber.extraInfo
            holder.btnApprove.setOnClickListener {
                Toast.makeText(requireContext(), "Approved ${barber.name}!", Toast.LENGTH_SHORT).show()
            }
        }
        override fun getItemCount(): Int = barbers.size
        fun updateData(newBarbers: List<BarberStaff>) {
            barbers = newBarbers
            notifyDataSetChanged()
        }
    }

    inner class ActiveAdapter(private var barbers: List<BarberStaff>) : RecyclerView.Adapter<ActiveAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_active_name)
            val tvChair: TextView = view.findViewById(R.id.tv_active_chair)
            val tvShift: TextView = view.findViewById(R.id.tv_shift_badge)
            val switchActive: Switch = view.findViewById(R.id.switch_active)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_active_barber, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val barber = barbers[position]
            holder.tvName.text = barber.name
            holder.tvChair.text = barber.details
            holder.tvShift.text = barber.extraInfo

            if (barber.extraInfo.contains("Evening")) {
                holder.tvShift.setTextColor(android.graphics.Color.parseColor("#F57F17"))
                holder.tvShift.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFF3CD"))
            } else {
                holder.tvShift.setTextColor(android.graphics.Color.parseColor("#6C5CE7"))
                holder.tvShift.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F0EFFF"))
            }
        }
        override fun getItemCount(): Int = barbers.size
        fun updateData(newBarbers: List<BarberStaff>) {
            barbers = newBarbers
            notifyDataSetChanged()
        }
    }
}