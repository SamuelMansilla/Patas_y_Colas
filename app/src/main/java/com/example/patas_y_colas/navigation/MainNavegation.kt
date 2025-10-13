package com.example.patas_y_colas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.patas_y_colas.ui.screens.MenuScreen
import com.example.patas_y_colas.ui.theme.screens.PortadaScreen

@Composable
fun MainNavigation() { // No recibe ningún parámetro
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "portada") {
        composable("portada") {
            PortadaScreen(
                onContinueClick = { navController.navigate("menu") }
            )
        }
        composable("menu") {
            // Llama a MenuScreen, que ahora es autosuficiente
            // y crea su propio ViewModel internamente.
            MenuScreen()
        }
    }
}

