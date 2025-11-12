package com.example.patas_y_colas.repository

import com.example.patas_y_colas.data.local.PetDao
import com.example.patas_y_colas.data.remote.ApiService
import com.example.patas_y_colas.model.Pet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// MODIFICADO: Reescrito para Hilt y ApiService
@Singleton
class PetRepository @Inject constructor(
    private val petDao: PetDao,
    private val apiService: ApiService
) {

    // Room sigue siendo la "fuente de verdad" para la UI
    val allPets: Flow<List<Pet>> = petDao.getAllPets()

    // Trae mascotas de la API y las guarda en Room
    suspend fun refreshPetsFromApi() {
        try {
            val petsFromApi = apiService.getAllPets()
            petDao.deleteAll()
            petsFromApi.forEach { petDao.insertPet(it) }
        } catch (e: Exception) {
            // Manejar error de red
            e.printStackTrace()
        }
    }

    // Inserta en la API y luego refresca la lista local
    suspend fun insertPet(pet: Pet) {
        try {
            apiService.createPet(pet)
            // Si la API tiene éxito, actualizamos la lista local
            refreshPetsFromApi()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ... (puedes modificar updatePet y deletePet de forma similar)
    suspend fun updatePet(pet: Pet) {
        // Lógica de API para actualizar...
        // ...
        // Luego actualiza Room
        petDao.updatePet(pet)
    }

    suspend fun deletePet(pet: Pet) {
        // Lógica de API para borrar...
        // ...
        // Luego borra de Room
        petDao.deletePet(pet)
    }

    fun getPetById(petId: Int): Flow<Pet> {
        return petDao.getPetById(petId)
    }
}