package com.example.weatherapp.ui.screens

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.weatherapp.R
import com.example.weatherapp.Screen.Alerts.icon
import com.example.weatherapp.viewmodel.FavoriteViewModel
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

    LaunchedEffect(Unit) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Display selected location
            if (selectedLocation != null) {
                Text(
                    text = cityName,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Map
            Box(modifier = Modifier.weight(1f)) {
                MapViewContainer(
                    context = context,
                    onLocationSelected = { point ->
                        selectedLocation = point
                    }
                )

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
}

@Composable
fun MapViewContainer(
    context: Context,
    onLocationSelected: (GeoPoint) -> Unit
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
            }
        },
        update = { view ->
            // Update logic
        }
    )

    // Clean up
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
}

private fun getCityName(context: Context, lat: Double, lon: Double): String {
    val geocoder = android.location.Geocoder(context)
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