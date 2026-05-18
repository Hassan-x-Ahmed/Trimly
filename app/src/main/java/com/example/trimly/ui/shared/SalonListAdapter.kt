package com.example.trimly.ui.shared.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.model.Salon

class SalonListAdapter(private val salonList: List<Salon>) :
    RecyclerView.Adapter<SalonListAdapter.SalonViewHolder>() {

    class SalonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.tv_salon_name)
        val locationText: TextView = view.findViewById(R.id.tv_salon_location)
        // We will bind the image and rating later when we have real data!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_salon_card, parent, false)
        return SalonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalonViewHolder, position: Int) {
        val salon = salonList[position]
        holder.nameText.text = salon.name
        holder.locationText.text = salon.address
    }

    override fun getItemCount(): Int {
        return salonList.size
    }
}