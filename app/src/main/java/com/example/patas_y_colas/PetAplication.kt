package com.example.patas_y_colas

import android.app.Application
import com.example.patas_y_colas.notifications.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp // <-- ¡IMPORTA ESTO!

@HiltAndroidApp // <-- ¡AÑADE ESTA ANOTACIÓN!
class PetApplication : Application() {

    // BORRA las líneas 'database' y 'repository'
    // Hilt se encarga de ellas gracias a AppModule.kt

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }
}