package com.example.patas_y_colas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.repository.PetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuViewModel(private val repository: PetRepository) : ViewModel() {

    // Expone la lista de mascotas de la base de datos como un StateFlow.
    // La UI observará este flujo para actualizarse automáticamente.
    val allPets: StateFlow<List<Pet>> = repository.allPets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Funciones para insertar, actualizar y eliminar mascotas.
    // Se ejecutan en un hilo secundario gracias a viewModelScope.
    fun insert(pet: Pet) = viewModelScope.launch {
        repository.insert(pet)
    }

    fun update(pet: Pet) = viewModelScope.launch {
        repository.update(pet)
    }

    fun delete(pet: Pet) = viewModelScope.launch {
        repository.delete(pet)
    }
}

/**
 * Factory para poder crear una instancia de MenuViewModel con el PetRepository
 * como dependencia.
 */
class MenuViewModelFactory(private val repository: PetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
