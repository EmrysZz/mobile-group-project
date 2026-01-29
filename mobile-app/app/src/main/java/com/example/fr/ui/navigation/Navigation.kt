package com.example.fr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fr.ui.screens.*
import com.example.fr.ui.viewmodel.AuthViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("map") {
            MapScreen(navController = navController)
        }

        composable("addShelter") {
            AddShelterScreen(navController = navController)
        }

        composable("report") {
            ReportScreen(navController = navController)
        }

        composable("reports") {
            ReportsListScreen(navController = navController)
        }
    }
}
