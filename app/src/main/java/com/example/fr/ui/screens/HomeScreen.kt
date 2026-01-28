package com.example.fr.ui.screens

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fr.MapView
import com.example.fr.R
import com.example.fr.ui.theme.RescuePrimary
import com.example.fr.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(name: String, mapViewModel: MapViewModel = viewModel(), navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.1390, 101.6869), 10f)
    }

    // Standard observation of navigation results
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = navBackStackEntry?.savedStateHandle
    
    val targetLat by savedStateHandle?.getStateFlow<Double?>("lat", null)?.collectAsState() ?: mutableStateOf(null)
    val targetLng by savedStateHandle?.getStateFlow<Double?>("lng", null)?.collectAsState() ?: mutableStateOf(null)

    LaunchedEffect(targetLat, targetLng) {
        if (targetLat != null && targetLng != null) {
            delay(400)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(targetLat!!, targetLng!!), 15f)
            savedStateHandle?.remove<Double>("lat")
            savedStateHandle?.remove<Double>("lng")
        }
    }

    var showReportDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var selectedMarkerLocation by remember { mutableStateOf<com.example.fr.model.LocationData?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
             TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_flood_rescue_icon),
                            contentDescription = "FloodRescue Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("FloodRescue", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Settings")
                    }
                    DropdownMenu(
                        expanded = showSettingsMenu,
                        onDismissRequest = { showSettingsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Profile") },
                            onClick = {
                                showSettingsMenu = false
                                navController.navigate("profile")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About Us") },
                            onClick = { 
                                showSettingsMenu = false
                                navController.navigate("about") 
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = { 
                                showSettingsMenu = false
                                navController.navigate("auth") 
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceAround,
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     TextButton(onClick = { /* Stay on Map */ }) {
                         Icon(Icons.Default.Map, contentDescription = "Maps", tint = RescuePrimary)
                         Spacer(modifier = Modifier.padding(4.dp))
                         Text("Maps", color = RescuePrimary)
                     }
                     
                     TextButton(onClick = { navController.navigate("reports") }) {
                         Icon(Icons.Default.ListAlt, contentDescription = "Reports", tint = MaterialTheme.colorScheme.onSurface)
                         Spacer(modifier = Modifier.padding(4.dp))
                         Text("Reports", color = MaterialTheme.colorScheme.onSurface)
                     }

                     TextButton(onClick = { navController.navigate("news") }) {
                         Icon(Icons.Default.Newspaper, contentDescription = "News", tint = MaterialTheme.colorScheme.onSurface)
                         Spacer(modifier = Modifier.padding(4.dp))
                         Text("News", color = MaterialTheme.colorScheme.onSurface)
                     }
                 }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            MapView(
                mapViewModel = mapViewModel, 
                cameraPositionState = cameraPositionState,
                moveToUserLocation = targetLat == null,
                onMapLongClick = { latLng ->
                    selectedLocation = latLng
                    showReportDialog = true
                },
                onMarkerClick = { locationData ->
                    selectedMarkerLocation = locationData
                    true
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Welcome, $name!",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.padding(8.dp))
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) Text("Search location...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                innerTextField()
                            }
                        )
                        Button(
                            onClick = { searchLocation(context, searchQuery, cameraPositionState) },
                            colors = ButtonDefaults.buttonColors(containerColor = RescuePrimary)
                        ) {
                            Text("Go")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val filters by mapViewModel.selectedFilters.collectAsState()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val filterTypes = listOf("Flood", "Shelter", "Blocked")
                    filterTypes.forEach { type ->
                        FilterChip(
                            selected = filters.contains(type),
                            onClick = { mapViewModel.toggleFilter(type) },
                            label = { Text(type, color = if(filters.contains(type)) Color.White else MaterialTheme.colorScheme.onSurface) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RescuePrimary,
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                selectedLabelColor = Color.White
                            ),
                            leadingIcon = if (filters.contains(type)) {
                                { Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White) }
                            } else null
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { 
                    selectedLocation = null 
                    showReportDialog = true 
                },
                containerColor = RescuePrimary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Report")
            }
        }
    }

    if (showReportDialog) {
        val targetLocation = selectedLocation ?: cameraPositionState.position.target
        ReportIncidentDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { type, description ->
                mapViewModel.reportIncident(type, description, targetLocation.latitude, targetLocation.longitude, name)
                showReportDialog = false
            }
        )
    }

    if (selectedMarkerLocation != null) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onDismissRequest = { selectedMarkerLocation = null },
            title = { Text(selectedMarkerLocation!!.type, color = RescuePrimary) },
            text = {
                Column {
                    Text("Description: ${selectedMarkerLocation!!.description}")
                    Text("Reported by: ${selectedMarkerLocation!!.reportedBy}")
                    Text("Time: ${selectedMarkerLocation!!.reportedTime}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Verification Score: ${selectedMarkerLocation!!.verificationCount}", style = MaterialTheme.typography.titleMedium, color = RescuePrimary)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    mapViewModel.verifyLocation(selectedMarkerLocation!!, true)
                    selectedMarkerLocation = null
                }) {
                    Text("Confirm (+1)", color = RescuePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mapViewModel.verifyLocation(selectedMarkerLocation!!, false)
                    selectedMarkerLocation = null
                }) {
                    Text("Reject (-1)", color = Color.Red)
                }
            }
        )
    }
}

@Composable
fun ReportIncidentDialog(onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var type by remember { mutableStateOf("Flood") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val types = listOf("Flood", "Shelter", "Blocked")

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        onDismissRequest = onDismiss,
        title = { Text("Report Incident") },
        text = {
            Column {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = RescuePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        trailingIcon = {
                             IconButton(onClick = { expanded = true }) {
                                 Icon(Icons.Default.ArrowDropDown, contentDescription = "Drop", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                             }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded, 
                        onDismissRequest = { expanded = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        types.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = { type = t; expanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = RescuePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(type, description) },
                colors = ButtonDefaults.buttonColors(containerColor = RescuePrimary)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Suppress("DEPRECATION")
private fun searchLocation(
    context: Context,
    searchQuery: String,
    cameraPositionState: com.google.maps.android.compose.CameraPositionState
) {
    try {
        val geocoder = Geocoder(context)
        val addressList = geocoder.getFromLocationName(searchQuery, 1)
        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0]
            val latLng = LatLng(address.latitude, address.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    } catch (e: IOException) {
    }
}