package com.example.patas_y_colas.data.remote

// Para enviar en el login
data class LoginRequest(
    val email: String,
    val password: String
)

// Para enviar en el registro
data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)

// Lo que recibimos del backend
data class AuthResponse(
    val token: String
)