package com.example.weatherapp.ui.screens

import android.content.Context
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.weatherapp.R
import com.example.weatherapp.favorites.FavoriteViewModel
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: FavoriteViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var cityName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Address>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var currentMapView by remember { mutableStateOf<MapView?>(null) }
    var searchActive by remember { mutableStateOf(false) }  // search panel visibility

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            isSearching = true
            delay(500) // Debounce delay
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

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            cityName = getCityName(context, location.latitude, location.longitude)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Favorite Location") },
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
            // Map in background
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
                // Search Bar
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    query = searchQuery,
                    onQueryChange = { newQuery ->
                        searchQuery = newQuery
                    },
                    onSearch = {
                        // Close the search panel
                        searchActive = false
                    },
                    active = searchActive, //  visibility of the search panel
                    onActiveChange = { newState ->
                        searchActive = newState
                    },
                    placeholder = { Text("Search location") }
                ) {
                    // Search results
                    if (searchResults.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
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
                                            mapView?.controller?.animateTo(point)
                                            mapView?.controller?.setZoom(15.0)

                                            // Clear existing markers
                                            mapView?.overlays?.clear()

                                            // Add new marker
                                            val marker = Marker(mapView).apply {
                                                position = point
                                                setAnchor(
                                                    Marker.ANCHOR_CENTER,
                                                    Marker.ANCHOR_BOTTOM
                                                )
                                                icon = context.resources.getDrawable(
                                                    R.drawable.ic_location,
                                                    null
                                                )
                                                title = address.getAddressLine(0)
                                            }
                                            mapView?.overlays?.add(marker)
                                            mapView?.invalidate()

                                            searchQuery = address.getAddressLine(0) ?: ""
                                            searchResults = emptyList()

                                            // Close the search panel
                                            searchActive = false
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

                if (selectedLocation != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
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

            // Save FAB
            FloatingActionButton(
                onClick = {
                    selectedLocation?.let { location ->
                        viewModel.addFavorite(
                            cityName = cityName.ifEmpty { "Custom Location" },
                            lat = location.latitude,
                            lon = location.longitude
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(Icons.Default.Check, "Save location")
            }
        }
    }
}


@Composable
fun MapViewContainer(
    context: Context,
    onLocationSelected: (GeoPoint) -> Unit,
    onMapViewCreated: (MapView) -> Unit
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(5.0)
                controller.setCenter(GeoPoint(30.0, 31.0)) // Default

                // Add map click listener
                val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        overlays.removeAll { it is Marker }
                        val marker = Marker(this@apply).apply {
                            position = p
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = context.resources.getDrawable(R.drawable.ic_location, null)
                            title = "Selected Location"
                        }
                        overlays.add(marker)
                        onLocationSelected(p)
                        invalidate()
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint): Boolean = false
                })
                overlays.add(mapEventsOverlay)
                mapView = this
                onMapViewCreated(this)
            }
        }
    )

    // Clean up
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
}




fun getCityName(context: Context, lat: Double, lon: Double): String {
    val geocoder = Geocoder(context)
    return try {
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        addresses?.firstOrNull()?.let { address ->
            address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
        } ?: "Unknown Location"
    } catch (e: Exception) {
        "Unknown Location"
    }
}

