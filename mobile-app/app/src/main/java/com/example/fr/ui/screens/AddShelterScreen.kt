package com.example.fr.ui.screens

import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fr.data.api.DeviceIdManager
import com.example.fr.ui.viewmodel.MapViewModel
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
fun AddShelterScreen(
    navController: NavController,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val deviceIdManager = remember { DeviceIdManager(context) }
    val locationHelper = remember { LocationHelper(context) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Form state
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("") }

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
                    addresses?.firstOrNull()?.let { addr ->
                        val latLng = LatLng(addr.latitude, addr.longitude)
                        withContext(Dispatchers.Main) {
                            selectedLocation = latLng
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                            )
                            showSearchBar = false
                            searchQuery = ""
                            // Auto-fill address if available
                            if (address.isBlank()) {
                                address = addr.getAddressLine(0) ?: ""
                            }
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

    // Initialize device ID and get current location
    LaunchedEffect(Unit) {
        viewModel.setDeviceId(deviceIdManager.getDeviceId())

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

    // Handle success
    LaunchedEffect(uiState.shelterSubmitSuccess) {
        if (uiState.shelterSubmitSuccess) {
            snackbarHostState.showSnackbar("Shelter added successfully!")
            viewModel.clearShelterSubmitSuccess()
            navController.popBackStack()
        }
    }

    // Show error as snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Shelter") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Shelter Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Shelter Name *") },
                    placeholder = { Text("e.g., Community Center, School Hall") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    placeholder = { Text("Describe the shelter facilities, amenities available...") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    placeholder = { Text("Full address of the shelter") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    placeholder = { Text("Emergency contact number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Capacity
                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it.filter { char -> char.isDigit() } },
                    label = { Text("Capacity (number of people)") },
                    placeholder = { Text("e.g., 100") },
                    leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location section
                Text(
                    text = "Shelter Location *",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Tap on the map to select the shelter location",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Map for location selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
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
                                    title = "Shelter Location",
                                    snippet = "Selected location"
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
                                        singleLine = true
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
                                        contentDescription = "My location",
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Selected Location",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                Button(
                    onClick = {
                        selectedLocation?.let { location ->
                            viewModel.submitShelter(
                                name = name,
                                description = description,
                                address = address.ifBlank { null },
                                phone = phone.ifBlank { null },
                                latitude = location.latitude,
                                longitude = location.longitude,
                                capacity = capacityText.toIntOrNull()
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && description.isNotBlank() && selectedLocation != null && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Shelter")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Info text
                Text(
                    text = "* Required fields",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
