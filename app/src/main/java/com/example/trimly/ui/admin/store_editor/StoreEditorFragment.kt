// sid: 15932
package com.example.trimly.ui.admin.store_editor

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R

class StoreEditorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ───── Hero & Info Edits ─────
        view.findViewById<View>(R.id.btn_edit_hero).setOnClickListener {
            Toast.makeText(requireContext(), "Upload new hero image", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_edit_name).setOnClickListener {
            Toast.makeText(requireContext(), "Edit salon name", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_edit_location).setOnClickListener {
            Toast.makeText(requireContext(), "Change address / map pin", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_edit_hours).setOnClickListener {
            Toast.makeText(requireContext(), "Adjust operating hours", Toast.LENGTH_SHORT).show()
        }

        // ───── Service 1 ─────
        view.findViewById<View>(R.id.btn_edit_service1).setOnClickListener {
            Toast.makeText(requireContext(), "Edit Executive Skin Fade", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_delete_service1).setOnClickListener {
            showDeleteConfirmation("Executive Skin Fade") {
                view.findViewById<View>(R.id.card_service_1)?.visibility = View.GONE
                Toast.makeText(requireContext(), "Service removed", Toast.LENGTH_SHORT).show()
            }
        }

        // ───── Service 2 ─────
        view.findViewById<View>(R.id.btn_edit_service2).setOnClickListener {
            Toast.makeText(requireContext(), "Edit Beard Sculpting & Spa", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_delete_service2).setOnClickListener {
            showDeleteConfirmation("Beard Sculpting & Spa") {
                view.findViewById<View>(R.id.card_service_2)?.visibility = View.GONE
                Toast.makeText(requireContext(), "Service removed", Toast.LENGTH_SHORT).show()
            }
        }

        // ───── Add Service ─────
        view.findViewById<View>(R.id.btn_add_service).setOnClickListener {
            Toast.makeText(requireContext(), "Add new service", Toast.LENGTH_SHORT).show()
        }

        // ───── Barber 1 ─────
        view.findViewById<View>(R.id.btn_edit_barber1).setOnClickListener {
            Toast.makeText(requireContext(), "Edit Ali Ahmed's profile", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_delete_barber1).setOnClickListener {
            showDeleteConfirmation("Ali Ahmed") {
                view.findViewById<View>(R.id.container_barber_1)?.visibility = View.GONE
                Toast.makeText(requireContext(), "Barber removed", Toast.LENGTH_SHORT).show()
            }
        }

        // ───── Barber 2 ─────
        view.findViewById<View>(R.id.btn_edit_barber2).setOnClickListener {
            Toast.makeText(requireContext(), "Edit Zain Siddiqui's profile", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btn_delete_barber2).setOnClickListener {
            showDeleteConfirmation("Zain Siddiqui") {
                view.findViewById<View>(R.id.container_barber_2)?.visibility = View.GONE
                Toast.makeText(requireContext(), "Barber removed", Toast.LENGTH_SHORT).show()
            }
        }

        // ───── Add Barber ─────
        view.findViewById<View>(R.id.btn_add_barber).setOnClickListener {
            Toast.makeText(requireContext(), "Invite new barber", Toast.LENGTH_SHORT).show()
        }

        // ───── Save All ─────
        view.findViewById<View>(R.id.btn_save_store).setOnClickListener {
            Toast.makeText(requireContext(), "All changes saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shows a simple confirmation dialog before performing the delete action.
     */
    private fun showDeleteConfirmation(itemName: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete $itemName?")
            .setMessage("This will permanently remove \"$itemName\" from your store. You can undo by re‑adding it before saving.")
            .setPositiveButton("Delete") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}