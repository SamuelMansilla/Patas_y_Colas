package com.example.patas_y_colas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.patas_y_colas.data.remote.ApiService
import com.example.patas_y_colas.data.remote.LoginRequest
import com.example.patas_y_colas.data.remote.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Función para ser llamada desde la UI
    fun login(email: String, pass: String, onLoginSuccess: () -> Unit, onLoginError: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, pass)
                val response = apiService.login(request)
                tokenManager.saveToken(response.token)
                onLoginSuccess() // Navega a la pantalla de menú
            } catch (e: Exception) {
                e.printStackTrace()
                onLoginError() // Muestra un error
            }
        }
    }

    // ... puedes añadir una función de registro (register) aquí ...
}