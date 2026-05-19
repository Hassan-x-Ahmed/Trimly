// sid: 15932
package com.example.trimly.ui.admin.store_editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminViewModel
import com.example.trimly.ui.admin.SalonService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class ServiceManagerBottomSheet : BottomSheetDialogFragment() {

    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var adapter: ServiceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_service_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvServices = view.findViewById<RecyclerView>(R.id.rv_manage_services)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btn_add_new_service)

        // Setup Adapter
        rvServices.layoutManager = LinearLayoutManager(requireContext())
        adapter = ServiceAdapter(
            onEdit = { service -> showAddEditDialog(service) },
            onDelete = { service -> deleteServicePrompt(service) }
        )
        rvServices.adapter = adapter

        // Real-time Observer
        adminViewModel.servicesList.observe(viewLifecycleOwner) { services ->
            adapter.updateData(services)
        }

        btnAdd.setOnClickListener {
            showAddEditDialog(null) // null means "Create New"
        }
    }

    private fun showAddEditDialog(existingService: SalonService?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_manage_service, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val etName = dialogView.findViewById<EditText>(R.id.et_service_name)
        val etPrice = dialogView.findViewById<EditText>(R.id.et_service_price)

        if (existingService != null) {
            tvTitle.text = "Edit Service"
            etName.setText(existingService.name)
            etPrice.setText(existingService.price.toString())
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnSave.setTextColor(android.graphics.Color.parseColor("#6C5CE7"))

            btnSave.setOnClickListener {
                val name = etName.text.toString().trim()
                val priceStr = etPrice.text.toString().trim()

                if (name.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val price = priceStr.toIntOrNull() ?: 0
                val serviceToSave = SalonService(
                    id = existingService?.id ?: "", // Empty ID triggers a Create in Firebase
                    name = name,
                    price = price
                )

                adminViewModel.addOrUpdateService(serviceToSave) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun deleteServicePrompt(service: SalonService) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete ${service.name}?")
            .setMessage("Are you sure you want to remove this service?")
            .setPositiveButton("Delete") { _, _ ->
                adminViewModel.deleteService(service.id) { success ->
                    if (success) Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // INNER ADAPTER CLASS
    inner class ServiceAdapter(
        private val onEdit: (SalonService) -> Unit,
        private val onDelete: (SalonService) -> Unit
    ) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

        private var services = listOf<SalonService>()

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_row_service_name)
            val tvPrice: TextView = view.findViewById(R.id.tv_row_service_price)
            val btnEdit: ImageView = view.findViewById(R.id.btn_row_edit)
            val btnDelete: ImageView = view.findViewById(R.id.btn_row_delete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manage_service, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val service = services[position]
            holder.tvName.text = service.name
            holder.tvPrice.text = "PKR ${service.price}"
            holder.btnEdit.setOnClickListener { onEdit(service) }
            holder.btnDelete.setOnClickListener { onDelete(service) }
        }

        override fun getItemCount() = services.size

        fun updateData(newServices: List<SalonService>) {
            services = newServices
            notifyDataSetChanged()
        }
    }
}