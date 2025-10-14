package com.example.patas_y_colas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VaccineRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int = 0, // Para asociar la vacuna a una mascota
    val vaccineName: String,
    val date: String
)
