// sid: 15932
package com.example.trimly.ui.client.search_results

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.model.Salon
import com.example.trimly.ui.shared.adapters.SalonListAdapter

class SearchResultsFragment : Fragment(R.layout.fragment_search_results) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close the search and go back to home
        view.findViewById<View>(R.id.btn_close_search).setOnClickListener {
            findNavController().popBackStack()
        }

        val mockSalons = listOf(
            Salon(name = "Élysian Noir Salon", address = "Clifton, Karachi • 1.2 km"),
            Salon(name = "Urban Barber Hub", address = "DHA Phase 6, Karachi • 3.5 km"),
            Salon(name = "Classic Shears", address = "Tariq Road, Karachi • 5.0 km")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_search_results)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

       // val adapter = SalonListAdapter(mockSalons) { selectedSalon ->
            // When a salon is clicked, navigate to salon detail fragment
            // You can pass the salon ID via Safe Args later
           // findNavController().navigate(R.id.action_search_to_salon_detail)
       // }
        //recyclerView.adapter = adapter
    }
}

