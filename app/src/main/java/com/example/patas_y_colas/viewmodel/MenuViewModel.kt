package com.example.patas_y_colas.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.model.VaccineRecord
import com.example.patas_y_colas.notifications.NotificationScheduler
import com.example.patas_y_colas.repository.PetRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuViewModel(
    private val repository: PetRepository,
    private val application: Application
) : ViewModel() {

    val allPets: StateFlow<List<Pet>> = repository.allPets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(pet: Pet) = viewModelScope.launch {
        repository.insert(pet)
        NotificationScheduler.scheduleNotifications(application, pet)

        // Enviamos una notificación de prueba para la última vacuna añadida
        pet.vaccinesJson?.let { json ->
            val vaccines: List<VaccineRecord> = Gson().fromJson(json, object : TypeToken<List<VaccineRecord>>() {}.type)
            vaccines.lastOrNull()?.takeIf { it.vaccineName.isNotBlank() }?.let {
                NotificationScheduler.sendTestNotification(application, pet.name, it.vaccineName)
            }
        }
    }

    fun update(pet: Pet) = viewModelScope.launch {
        repository.update(pet)
        NotificationScheduler.scheduleNotifications(application, pet)

        // Enviamos una notificación de prueba para la última vacuna añadida
        pet.vaccinesJson?.let { json ->
            val vaccines: List<VaccineRecord> = Gson().fromJson(json, object : TypeToken<List<VaccineRecord>>() {}.type)
            vaccines.lastOrNull()?.takeIf { it.vaccineName.isNotBlank() }?.let {
                NotificationScheduler.sendTestNotification(application, pet.name, it.vaccineName)
            }
        }
    }

    fun delete(pet: Pet) = viewModelScope.launch {
        NotificationScheduler.cancelNotificationsForPet(application, pet)
        repository.delete(pet)
    }
}

class MenuViewModelFactory(
    private val repository: PetRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

