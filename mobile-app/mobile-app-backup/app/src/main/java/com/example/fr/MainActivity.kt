package com.example.fr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fr.ui.navigation.Navigation
import com.example.fr.ui.theme.FrTheme

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Global Exception Handler
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runOnUiThread {
                Toast.makeText(this, "Application Error: ${throwable.message}", Toast.LENGTH_LONG).show()
            }
            oldHandler?.uncaughtException(thread, throwable)
        }

        setContent {
            FrTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                
                // Network Monitor
                DisposableEffect(Unit) {
                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val networkCallback = object : ConnectivityManager.NetworkCallback() {
                        override fun onLost(network: Network) {
                            scope.launch {
                                snackbarHostState.showSnackbar("No Internet Connection")
                            }
                        }
                    }
                    val request = NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()
                    connectivityManager.registerNetworkCallback(request, networkCallback)

                    onDispose {
                        connectivityManager.unregisterNetworkCallback(networkCallback)
                    }
                }

                // Pass snackbarHostState to Navigation/Screens if needed, 
                // but for now relying on local Snackbars or Toasts for simplicity in this swift implementation.
                // ideally we wrap Navigation in a Scaffold with this SnackbarHost.
                
                androidx.compose.material3.Scaffold(
                    snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.padding(innerPadding)) {
                        Navigation()
                    }
                }
            }
        }
    }
}