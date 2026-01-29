package com.example.fr.ui.screens

import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fr.data.api.TokenManager
import com.example.fr.ui.viewmodel.ReportViewModel
import com.example.fr.util.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = viewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val locationHelper = remember { LocationHelper(context) }
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf(tokenManager.getUser()?.name ?: "") }
    var incidentType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Location state
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember { mutableStateOf(locationHelper.hasLocationPermission()) }

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    // Default location - Kuala Lumpur, Malaysia
    val defaultLocation = LatLng(3.1390, 101.6869)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    val incidentTypes = listOf("Flood", "Road Closure", "Rescue Needed", "Power Outage", "Other")

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.any { it }
        if (hasLocationPermission) {
            scope.launch {
                val location = locationHelper.getCurrentLocation()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    selectedLocation = latLng
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                    )
                }
            }
        }
    }

    // Search function
    fun searchLocation(query: String) {
        if (query.isBlank()) return
        scope.launch {
            isSearching = true
            try {
                withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(query, 1)
                    addresses?.firstOrNull()?.let { address ->
                        val latLng = LatLng(address.latitude, address.longitude)
                        withContext(Dispatchers.Main) {
                            selectedLocation = latLng
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                            )
                            showSearchBar = false
                            searchQuery = ""
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error silently
            } finally {
                isSearching = false
            }
        }
    }

    // Get current location on launch
    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            val location = locationHelper.getCurrentLocation()
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                selectedLocation = latLng
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                )
            }
        } else {
            permissionLauncher.launch(LocationHelper.LOCATION_PERMISSIONS)
        }
    }

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            viewModel.clearSubmitSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Incident") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tap on map or use search to select incident location",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Map for location selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = false
                        ),
                        onMapClick = { latLng ->
                            selectedLocation = latLng
                        }
                    ) {
                        selectedLocation?.let { location ->
                            Marker(
                                state = MarkerState(position = location),
                                title = "Incident Location",
                                snippet = "Tap elsewhere to change"
                            )
                        }
                    }

                    // Search bar overlay
                    if (showSearchBar) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 4.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search location...") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                                )
                                IconButton(
                                    onClick = { searchLocation(searchQuery) },
                                    enabled = !isSearching && searchQuery.isNotBlank()
                                ) {
                                    if (isSearching) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    } else {
                                        Icon(Icons.Default.Search, contentDescription = "Search")
                                    }
                                }
                                IconButton(onClick = { showSearchBar = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }
                        }
                    }

                    // Control buttons
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Search button
                        FloatingActionButton(
                            onClick = { showSearchBar = !showSearchBar },
                            modifier = Modifier.size(40.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search location",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // My location button
                        if (hasLocationPermission) {
                            FloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        val location = locationHelper.getCurrentLocation()
                                        location?.let {
                                            val latLng = LatLng(it.latitude, it.longitude)
                                            selectedLocation = latLng
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.size(40.dp),
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Icon(
                                    Icons.Default.MyLocation,
                                    contentDescription = "Use current location",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (!hasLocationPermission) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            TextButton(
                                onClick = { permissionLauncher.launch(LocationHelper.LOCATION_PERMISSIONS) }
                            ) {
                                Icon(
                                    Icons.Default.LocationOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Enable Location")
                            }
                        }
                    }
                }
            }

            // Show selected coordinates
            selectedLocation?.let { location ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Selected Location",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Form fields
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                enabled = !uiState.isLoading
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = incidentType,
                    onValueChange = { },
                    label = { Text("Incident Type") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) },
                    enabled = !uiState.isLoading
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    incidentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                incidentType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                enabled = !uiState.isLoading
            )

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.clearError()
                    selectedLocation?.let { location ->
                        viewModel.submitReport(
                            userName = userName,
                            incidentType = incidentType,
                            description = description,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading && userName.isNotBlank() &&
                        incidentType.isNotBlank() && description.isNotBlank() &&
                        selectedLocation != null
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Report")
                }
            }
        }
    }
}
