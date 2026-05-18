// sid: 15932
package com.example.trimly.ui.client.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(R.layout.fragment_home), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the map fragment and trigger the sync
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 1. Apply your Premium Midnight Style (using a JSON raw resource)
        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark))
        } catch (e: Exception) { /* Handle style error */ }

        // 2. Focus on Karachi (The Trimly Hub)
        val karachi = LatLng(24.8607, 67.0011)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(karachi, 13f))

        // 3. Add a marker for Élysian Noir
        map.addMarker(MarkerOptions().position(LatLng(24.8138, 67.0336)).title("Élysian Noir Salon"))
    }
}