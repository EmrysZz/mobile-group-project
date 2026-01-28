package com.example.fr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fr.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapView(
    mapViewModel: MapViewModel = viewModel(),
    cameraPositionState: CameraPositionState,
    moveToUserLocation: Boolean = true,
    onMapLongClick: (LatLng) -> Unit = {},
    onMarkerClick: (com.example.fr.model.LocationData) -> Boolean = { false }
) {
    val locations by mapViewModel.locations.collectAsState()
    val context = LocalContext.current

    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                isLocationPermissionGranted = true
                if (moveToUserLocation) {
                    getCurrentLocation(context) { latLng ->
                        cameraPositionState.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(latLng, 15f)
                    }
                }
            } else {
                // Handle permission denial
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
            if (moveToUserLocation) {
                getCurrentLocation(context) { latLng ->
                    cameraPositionState.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(latLng, 15f)
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = com.google.maps.android.compose.MapProperties(isMyLocationEnabled = isLocationPermissionGranted),
        uiSettings = com.google.maps.android.compose.MapUiSettings(myLocationButtonEnabled = true),
        onMapLoaded = {
            // Permission check already handled in LaunchedEffect, but keeping this for robustness if needed
        },
        onMapLongClick = onMapLongClick
    ) {
        locations.forEach { location ->
            val hue = when (location.type) {
                "Flood" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE
                "Shelter" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
                "Blocked" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE
                else -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
            }
            Marker(
                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                title = location.type,
                snippet = "${location.description} (Reported by: ${location.reportedBy})",
                icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(hue),
                onClick = { 
                    onMarkerClick(location)
                }
            )
        }
    }
}

private fun getCurrentLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    onLocationReceived(LatLng(it.latitude, it.longitude))
                }
            }
    } catch (e: SecurityException) {
        // Handle exception
    }
}