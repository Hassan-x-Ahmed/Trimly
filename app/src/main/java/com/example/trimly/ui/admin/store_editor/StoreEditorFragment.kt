// sid: 15932
package com.example.trimly.ui.admin.store_editor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.trimly.R
import com.example.trimly.ui.admin.AdminViewModel
import com.example.trimly.ui.auth.AuthActivity
import com.google.android.material.card.MaterialCardView

class StoreEditorFragment : Fragment(R.layout.fragment_store_profile) {

    private val adminViewModel: AdminViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tv_profile_name)
        val tvAddress = view.findViewById<TextView>(R.id.tv_profile_address)
        val tvPhone = view.findViewById<TextView>(R.id.tv_profile_phone)
        val tvHours = view.findViewById<TextView>(R.id.tv_profile_hours)

        adminViewModel.salonName.observe(viewLifecycleOwner) { tvName.text = it.ifEmpty { "Not set" } }
        adminViewModel.salonAddress.observe(viewLifecycleOwner) { tvAddress.text = it.ifEmpty { "Not set" } }
        adminViewModel.salonPhone.observe(viewLifecycleOwner) { tvPhone.text = it.ifEmpty { "Not set" } }
        adminViewModel.salonHours.observe(viewLifecycleOwner) { tvHours.text = it.ifEmpty { "Not set" } }

        val openDialogListener = View.OnClickListener { showEditStoreDialog() }
        view.findViewById<ImageView>(R.id.btn_edit_name).setOnClickListener(openDialogListener)
        view.findViewById<ImageView>(R.id.btn_edit_address).setOnClickListener(openDialogListener)
        view.findViewById<ImageView>(R.id.btn_edit_phone).setOnClickListener(openDialogListener)
        view.findViewById<ImageView>(R.id.btn_edit_hours).setOnClickListener(openDialogListener)

        // LAUNCH THE SERVICES BOTTOM SHEET
        view.findViewById<TextView>(R.id.btn_manage_services)?.setOnClickListener {
            val bottomSheet = ServiceManagerBottomSheet()
            bottomSheet.show(parentFragmentManager, "ServiceManager")
        }

        view.findViewById<MaterialCardView>(R.id.btn_logout).setOnClickListener {
            adminViewModel.logout()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun showEditStoreDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_store, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_edit_name)
        val etAddress = dialogView.findViewById<EditText>(R.id.et_edit_address)
        val etPhone = dialogView.findViewById<EditText>(R.id.et_edit_phone)
        val etHours = dialogView.findViewById<EditText>(R.id.et_edit_hours)

        etName.setText(adminViewModel.salonName.value)
        etAddress.setText(adminViewModel.salonAddress.value)
        etPhone.setText(adminViewModel.salonPhone.value)
        etHours.setText(adminViewModel.salonHours.value)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnSave.setTextColor(android.graphics.Color.parseColor("#6C5CE7"))

            btnSave.setOnClickListener {
                val newName = etName.text.toString().trim()
                if (newName.isEmpty()) {
                    etName.error = "Name required"
                    return@setOnClickListener
                }

                adminViewModel.salonName.value = newName
                adminViewModel.salonAddress.value = etAddress.text.toString().trim()
                adminViewModel.salonPhone.value = etPhone.text.toString().trim()
                adminViewModel.salonHours.value = etHours.text.toString().trim()

                Toast.makeText(requireContext(), "Store profile updated!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}