package com.example.patas_y_colas.di

import android.content.Context
import com.example.patas_y_colas.data.local.PetDatabase
import com.example.patas_y_colas.data.remote.ApiService
import com.example.patas_y_colas.data.remote.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // URL para pruebas locales (Emulador de Android)
    const val BASE_URL_LOCAL = "http://10.0.2.2:8080/"

    // URL de producción (Cuando lo subas a Render)
    // REEMPLAZA "tu-backend-movil.onrender.com" con tu URL real de Render
    const val BASE_URL_PROD = "https://tu-backend-movil.onrender.com/"

    @Singleton
    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Intercepta la llamada y añade el token
                val token = runBlocking { tokenManager.getToken().first() }
                val request = chain.request().newBuilder()
                    .also {
                        if (token != null && !chain.request().url.pathSegments.contains("auth")) {
                            it.addHeader("Authorization", "Bearer $token")
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            // ¡IMPORTANTE! Usa LOCAL para probar, cambia a PROD para producción
            .baseUrl(BASE_URL_LOCAL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // --- Proveedores de Room (los que ya tenías) ---
    @Singleton
    @Provides
    fun providePetDatabase(@ApplicationContext context: Context) =
        PetDatabase.Companion.getDatabase(context)

    @Singleton
    @Provides
    fun providePetDao(database: PetDatabase) = database.petDao()
}