package com.example.fr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fr.ui.screens.AuthScreen
import com.example.fr.ui.screens.HomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(navController = navController)
        }
        composable("home/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "User"
            HomeScreen(name = name)
        }
    }
}