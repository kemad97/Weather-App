package com.example.weatherapp.favorites

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.local.FavoriteEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoriteViewModel,
    onNavigateToMap: () -> Unit,
    onNavigateToDetail: (Double, Double) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val selectedWeather by viewModel.selectedWeather.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<FavoriteEntity?>(null) }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToMap) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_location)
                )
            }
        },
      topBar = { TopAppBar(
          title = {Text(stringResource(R.string.favorite_locations))  }
      )
          },

    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()
            ) {

                if (favorites.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.no_favorites_added_yet))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(favorites) { favorite ->
                            FavoriteItem(
                                favorite = favorite,
                                onDelete = { showDeleteDialog = favorite },
                                onClick = { onNavigateToDetail(favorite.lat, favorite.lon) }
                            )
                        }
                    }
                }
            }

        }
    }
    showDeleteDialog?.let { favorite ->
        AlertDialog(
            containerColor = Color.DarkGray,
                    onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.delete_location)) },
            text = { Text(
                stringResource(
                    R.string.are_you_sure_you_want_to_delete_from_favorites,
                    favorite.cityName
                )) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeFavorite(favorite)
                        showDeleteDialog = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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
                    contentDescription = stringResource(R.string.delete_favorite)
                )
            }
        }
    }
}

