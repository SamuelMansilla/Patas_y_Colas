package com.example.patas_y_colas.viewmodel

import androidx.lifecycle.ViewModel
// ... (elimina ViewModelProvider.Factory)
import androidx.lifecycle.viewModelScope
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// MODIFICADO: Reescrito para Hilt
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: PetRepository
) : ViewModel() {

    val allPets: Flow<List<Pet>> = repository.allPets

    init {
        // Carga inicial de datos desde la API
        refreshPets()
    }

    fun refreshPets() {
        viewModelScope.launch {
            repository.refreshPetsFromApi()
        }
    }

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            repository.insertPet(pet)
        }
    }

    fun updatePet(pet: Pet) {
        viewModelScope.launch {
            repository.updatePet(pet)
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            repository.deletePet(pet)
        }
    }
}