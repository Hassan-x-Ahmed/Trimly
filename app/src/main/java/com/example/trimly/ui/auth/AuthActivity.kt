package com.example.trimly.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trimly.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This line tells the Activity to display your container layout
        setContentView(R.layout.activity_auth)
    }
}