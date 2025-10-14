package com.example.patas_y_colas

import android.app.Application
import com.example.patas_y_colas.data.local.PetDatabase
import com.example.patas_y_colas.notifications.createNotificationChannel
import com.example.patas_y_colas.repository.PetRepository

class PetApplication : Application() {
    // Usando 'by lazy', la base de datos y el repositorio solo se crearán
    // la primera vez que se necesiten.
    val database by lazy { PetDatabase.getDatabase(this) }
    val repository by lazy { PetRepository(database.petDao()) }

    override fun onCreate() {
        super.onCreate()
        // Creamos el canal de notificaciones cuando la app se inicia.
        // Esto es seguro porque se ejecuta una sola vez.
        createNotificationChannel(this)
    }
}

