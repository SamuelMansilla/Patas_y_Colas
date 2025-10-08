package com.example.patas_y_colas.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "portada") {
        composable("portada") {
            PortadaScreen(
                onContinueClick = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen()
        }
    }
}
