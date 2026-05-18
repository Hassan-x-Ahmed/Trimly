// sid: 15932
package com.example.trimly.ui.barber.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.example.trimly.ui.auth.AuthActivity

class BarberProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barber_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_edit_profile)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening personal details editor…", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btn_manage_services)?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening service catalog…", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btn_logout)?.setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}