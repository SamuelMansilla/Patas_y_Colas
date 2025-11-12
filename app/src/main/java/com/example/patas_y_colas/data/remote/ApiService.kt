package com.example.patas_y_colas.data.remote

import com.example.patas_y_colas.model.Pet
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // --- Auth ---
    // (Estas rutas no necesitan token)
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // --- Pets ---
    // (El token se añadirá automáticamente con el Interceptor)
    @GET("/api/pets")
    suspend fun getAllPets(): List<Pet>

    @POST("/api/pets")
    suspend fun createPet(@Body pet: Pet): Pet

    // ... aquí puedes añadir getPetById, deletePet, etc.
}