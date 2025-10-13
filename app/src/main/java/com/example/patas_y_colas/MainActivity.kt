package com.example.patas_y_colas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.patas_y_colas.navigation.MainNavigation
import com.example.patas_y_colas.ui.theme.Patas_y_ColasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Patas_y_ColasTheme {
                // MainNavigation ahora se encarga de todo.
                MainNavigation()
            }
        }
    }
}

