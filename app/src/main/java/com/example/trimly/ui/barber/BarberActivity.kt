// sid: 15932
package com.example.trimly.ui.barber

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.trimly.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BarberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barber)

        // 1. Safely locate the NavHostFragment container using its exact UI ID
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.barber_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 2. Bind the bottom navigation bar View (NOT the graph file) to the controller
        val bottomNav = findViewById<BottomNavigationView>(R.id.barber_bottom_nav)
        bottomNav.setupWithNavController(navController)
    }
}