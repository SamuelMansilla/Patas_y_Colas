package com.example.patas_y_colas.data.local

import androidx.room.TypeConverter
import com.example.patas_y_colas.model.VaccineRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// AÑADIDO: Convertidores para que Room guarde la List<VaccineRecord> como JSON
class Converters {
    @TypeConverter
    fun fromVaccineRecordList(value: List<VaccineRecord>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<VaccineRecord>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVaccineRecordList(value: String?): List<VaccineRecord>? {
        if (value == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<VaccineRecord>>() {}.type
        return gson.fromJson(value, type)
    }
}