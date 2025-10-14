package com.example.patas_y_colas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.patas_y_colas.model.Pet
import com.example.patas_y_colas.model.VaccineRecord

@Database(entities = [Pet::class, VaccineRecord::class], version = 3, exportSchema = false) // <-- CAMBIA A VERSIÓN 3 Y AÑADE VACCINERECORD
@TypeConverters(Converters::class) // <-- AÑADE ESTA LÍNEA
abstract class PetDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao

    companion object {
        @Volatile
        private var INSTANCE: PetDatabase? = null

        fun getDatabase(context: Context): PetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PetDatabase::class.java,
                    "pet_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

