package com.example.weatherapp.ui.screens
import android.location.Address
import android.location.Geocoder
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.LocationTracker
import com.example.weatherapp.R
import com.example.weatherapp.viewmodel.LocationMethod
import com.example.weatherapp.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenSettings(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var cityName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Address>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var currentMapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            cityName = getCityName(context, location.latitude, location.longitude)
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            isSearching = true
            delay(500)
            val geocoder = Geocoder(context)
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(searchQuery, 5)
                searchResults = addresses?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                searchResults = emptyList()
            }
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MapViewContainer(
                context = context,
                onLocationSelected = { point ->
                    selectedLocation = point
                },
                onMapViewCreated = { mapView ->
                    currentMapView = mapView
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            label = { Text("Search location") },
                            trailingIcon = {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            singleLine = true
                        )

                        if (searchResults.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    searchResults.forEach { address ->
                                        TextButton(
                                            onClick = {
                                                currentMapView?.let { mapView ->
                                                    val point = GeoPoint(
                                                        address.latitude,
                                                        address.longitude
                                                    )
                                                    selectedLocation = point
                                                    mapView.controller.animateTo(point)
                                                    mapView.controller.setZoom(15.0)

                                                    mapView.overlays.removeAll { it is org.osmdroid.views.overlay.Marker }
                                                    val marker = org.osmdroid.views.overlay.Marker(mapView).apply {
                                                        position = point
                                                        setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
                                                        icon = context.resources.getDrawable(R.drawable.ic_location, null)
                                                        title = address.getAddressLine(0)
                                                    }
                                                    mapView.overlays.add(marker)
                                                    mapView.invalidate()

                                                    searchQuery = address.getAddressLine(0) ?: ""
                                                    searchResults = emptyList()
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = address.getAddressLine(0) ?: "Unknown location",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                textAlign = TextAlign.Start
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (selectedLocation != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = cityName,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    selectedLocation?.let { location ->
                        LocationTracker.getInstance(context).myLocation.value =
                            Pair(location.latitude, location.longitude)
                        settingsViewModel.updateLocationMethod(LocationMethod.MAP)
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(Icons.Default.Check, "Set location")
            }
        }
    }
}
