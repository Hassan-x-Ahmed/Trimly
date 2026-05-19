// sid: 15932
package com.example.trimly.ui.client.home

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SearchResultsFragment : Fragment(R.layout.fragment_search_results) {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var etSearch: EditText
    private lateinit var resultsContainer: LinearLayout

    // We store all SALONS here to make live-searching lightning fast
    private var allSalons: List<DocumentSnapshot> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.et_search) ?: return
        resultsContainer = view.findViewById(R.id.results_container) ?: return

        // 1. Close Button Logic
        view.findViewById<View>(R.id.btn_close_search).setOnClickListener {
            findNavController().popBackStack()
        }

        // 2. Download all Salons immediately
        loadAllSalons()

        // 3. The "Live Search" Listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Every time a letter is typed, filter the list!
                filterResults(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadAllSalons() {
        // Querying a "salons" collection instead of users
        db.collection("salons").get()
            .addOnSuccessListener { documents ->
                allSalons = documents.documents
                filterResults("") // Show all salons initially
            }
            .addOnFailureListener {
                // Failsafe empty state
                val tvError = TextView(requireContext()).apply {
                    text = "Failed to load salons from server."
                    setTextColor(Color.parseColor("#888888"))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setPadding(0, 50, 0, 0)
                }
                resultsContainer.addView(tvError)
            }
    }

    private fun filterResults(query: String) {
        resultsContainer.removeAllViews()
        val lowerCaseQuery = query.lowercase()

        // Filter logic: Check if the Salon's NAME or ADDRESS matches the search
        val filteredSalons = allSalons.filter { doc ->
            val name = doc.getString("name")?.lowercase() ?: ""
            val address = doc.getString("address")?.lowercase() ?: ""
            name.contains(lowerCaseQuery) || address.contains(lowerCaseQuery)
        }

        // Empty State
        if (filteredSalons.isEmpty()) {
            val tvNoResults = TextView(requireContext()).apply {
                text = "No salons found matching '$query'"
                setTextColor(Color.parseColor("#888888"))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, 50, 0, 0)
            }
            resultsContainer.addView(tvNoResults)
            return
        }

        // Generate the UI cards for the matches
        for (doc in filteredSalons) {
            val salonId = doc.id
            val name = doc.getString("name") ?: "Premium Salon"
            val address = doc.getString("address") ?: "Location unknown"

            resultsContainer.addView(createSearchResultCard(name, address, salonId))
        }
    }

    /**
     * Builds a wide, luxury dark card tailored for Salons (Shows address instead of title)
     */
    private fun createSearchResultCard(name: String, address: String, salonId: String): View {
        val context = requireContext()
        val density = resources.displayMetrics.density

        val card = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (16 * density).toInt())
            }
            radius = 20f * density
            setCardBackgroundColor(Color.parseColor("#1A2422"))
            cardElevation = 0f
            setContentPadding((20 * density).toInt(), (20 * density).toInt(), (20 * density).toInt(), (20 * density).toInt())
        }

        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvName = TextView(context).apply {
            text = name
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvAddress = TextView(context).apply {
            text = address
            setTextColor(Color.parseColor("#999999"))
            textSize = 13f
            setPadding(0, (4 * density).toInt(), 0, 0)
        }

        textLayout.addView(tvName)
        textLayout.addView(tvAddress)

        // The little gold navigation arrow
        val btnView = ImageView(context).apply {
            setImageResource(android.R.drawable.ic_menu_directions)
            setColorFilter(Color.parseColor("#D4AF37"))
        }

        innerLayout.addView(textLayout)
        innerLayout.addView(btnView)
        card.addView(innerLayout)

        // Click listener to pass the SALON_ID to the Salon Detail page
        card.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("SALON_ID", salonId)

            try {
                // Uncomment this when your Salon Detail Fragment is ready!
                // findNavController().navigate(R.id.action_search_to_salon_detail, bundle)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return card
    }
}