package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.viewmodel.FavoriteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoriteViewModel,
    onNavigateToMap: () -> Unit,
    onNavigateToDetail: (Double, Double) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val selectedWeather by viewModel.selectedWeather.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToMap) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add location"
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Favorite Locations",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(favorites) { favorite ->
                        FavoriteItem(
                            favorite = favorite,
                            onDelete = { viewModel.removeFavorite(favorite) },
                            onClick = { onNavigateToDetail(favorite.lat, favorite.lon) }
                        )
                    }
                }
            }

            when (selectedWeather) {
                is ResultState.Loading -> LoadingIndicator()
                is ResultState.Success -> {
                    val weatherData = (selectedWeather as ResultState.Success).data
                    // Display weather details in a dialog or expanded card
                }
                is ResultState.Error -> {
                    val error = (selectedWeather as ResultState.Error).exception.message
                    // Show error message
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteItem(
    favorite: FavoriteEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = favorite.cityName,
                    style = MaterialTheme.typography.titleMedium
                )
//                Text(
//                    text = "Lat: ${favorite.lat}, Lon: ${favorite.lon}",
//                    style = MaterialTheme.typography.bodyMedium
//                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete favorite"
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}