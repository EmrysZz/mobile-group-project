package com.example.fr.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fr.model.User
import com.example.fr.viewmodel.AuthViewModel

@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var isLogin by remember { mutableStateOf(true) }
    var showLoginError by remember { mutableStateOf(false) }
    var loginErrorMessage by remember { mutableStateOf("") }

    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
        if (isLogin) {
            LoginScreen(
                onLogin = { email, password -> 
                    val user = authViewModel.login(email, password)
                    if (user != null) {
                        // Fix for logout issue: Clear back stack so 'Back' doesn't return to login
                        navController.navigate("home/${user.name}") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        loginErrorMessage = "Invalid email or password. Please register if you haven't."
                        showLoginError = true
                    }
                },
                onNavigateToRegister = { isLogin = false }
            )
        } else {
            RegisterScreen(
                onRegister = { name, email, password ->
                    val newUser = User(name, email, password)
                    val success = authViewModel.register(newUser)
                    if (success) {
                        // Auto login or switch to login
                        isLogin = true
                    } else {
                        // Handle registration error (e.g. email exists)
                        loginErrorMessage = "Registration failed. Email might already exist."
                        showLoginError = true
                    }
                },
                onNavigateToLogin = { isLogin = true }
            )
        }

        if (showLoginError) {
            AlertDialog(
                onDismissRequest = { showLoginError = false },
                title = { Text("Error") },
                text = { Text(loginErrorMessage) },
                confirmButton = {
                    TextButton(onClick = { showLoginError = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}