package com.example.patas_y_colas

import android.app.Application
import com.example.patas_y_colas.data.local.PetDatabase
import com.example.patas_y_colas.repository.PetRepository

/**
 * Clase de aplicación personalizada para inicializar componentes que deben existir
 * durante todo el ciclo de vida de la app.
 */
class PetApplication : Application() {
    // Usando 'by lazy', la base de datos y el repositorio solo se crearán
    // la primera vez que se necesiten, no al iniciar la app.
    val database by lazy { PetDatabase.getDatabase(this) }
    val repository by lazy { PetRepository(database.petDao()) }
}
