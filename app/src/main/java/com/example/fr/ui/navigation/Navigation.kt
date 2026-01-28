package com.example.fr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fr.ui.screens.*

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(navController = navController)
        }
        composable("home/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "User"
            HomeScreen(name = name, navController = navController)
        }
        composable("reports") {
            ReportsScreen(navController = navController)
        }
        composable("news") {
            NewsScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("edit_profile") {
            EditProfileScreen(navController = navController)
        }
        composable("about") {
            AboutScreen(navController = navController)
        }
    }
}