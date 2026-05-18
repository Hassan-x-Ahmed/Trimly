// sid: 15932
package com.example.trimly.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Notice the : ViewModel() inheritance here!
class AuthViewModel : ViewModel() {

    private val _userRole = MutableLiveData<String>("Client")
    val userRole: LiveData<String> = _userRole

    fun setRole(role: String) {
        _userRole.value = role
    }
}