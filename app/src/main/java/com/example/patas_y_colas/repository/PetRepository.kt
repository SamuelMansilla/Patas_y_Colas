package com.example.patas_y_colas.repository

import com.example.patas_y_colas.data.local.PetDao
import com.example.patas_y_colas.model.Pet
import kotlinx.coroutines.flow.Flow

class PetRepository(private val petDao: PetDao) {

    val allPets: Flow<List<Pet>> = petDao.getAllPets()

    suspend fun insert(pet: Pet) {
        petDao.insertPet(pet)
    }

    suspend fun update(pet: Pet) {
        petDao.updatePet(pet)
    }

    suspend fun delete(pet: Pet) {
        petDao.deletePet(pet)
    }
}
