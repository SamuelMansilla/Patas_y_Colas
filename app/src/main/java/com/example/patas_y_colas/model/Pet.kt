package com.example.patas_y_colas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val species: String,
    val breed: String,
    val age: String,
    val weight: String,
    val imageUri: String? = null,
    val vaccinesJson: String? = null // Guardaremos la lista de vacunas como un JSON
)

