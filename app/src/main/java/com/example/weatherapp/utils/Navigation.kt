package com.example.weatherapp.utils

import com.example.weatherapp.favorites.FavoriteDetailScreen
import com.example.weatherapp.settings.SettingsScreen
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.R
import com.example.weatherapp.model.local.SettingsRepository
import com.example.weatherapp.model.repo.WeatherRepository
import com.example.weatherapp.alerts.AlertsScreen
import com.example.weatherapp.favorites.FavoritesScreen
import com.example.weatherapp.home.HomeScreen
import com.example.weatherapp.ui.screens.MapScreen
import com.example.weatherapp.ui.screens.MapScreenSettings
import com.example.weatherapp.alerts.AlertsViewModel
import com.example.weatherapp.favorites.FavoriteViewModel
import com.example.weatherapp.favorites.FavoriteViewModelFactory
import com.example.weatherapp.home.HomeViewModel
import com.example.weatherapp.settings.SettingsViewModel
import com.example.weatherapp.settings.SettingsViewModelFactory

sealed class Screen(
    val route: String,
    var icon: Int,
    val label: Int
) {
    data object Home : Screen("home", R.drawable.ic_home, R.string.home)
    data object Favorites : Screen("favorites", R.drawable.ic_fav, R.string.favorites)
    data object Alerts : Screen("alerts", R.drawable.ic_alerts, R.string.alerts)
    data object Settings : Screen("settings", R.drawable.ic_settings, R.string.settings)
    data object Map : Screen("map", 0, 0)
    data object MapSettings : Screen("map/settings", 0, 0)

    data object FavoriteDetail : Screen("favorite_detail/{lat}/{lon}", 0, 0) {
        fun createRoute(lat: Double, lon: Double) = "favorite_detail/$lat/$lon"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    repository: WeatherRepository,
    settingsRepository: SettingsRepository
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val isLocationEnabled by LocationTracker(context).isLocationEnabled.collectAsState()
    var showDialog by remember { mutableStateOf(true) }


    if (!isLocationEnabled && showDialog) {
        AlertDialog(
            onDismissRequest = {
                //  showDialog = false
            },
            title = { Text(stringResource(R.string.location_required)) },
            text = { Text(stringResource(R.string.please_enable_location_services_to_get_weather_information)) },
            confirmButton = {
                Button(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        showDialog = false
                    }
                ) {
                    Text("Enable Location")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Skip")
                }
            }
        )
    }
    val screens = listOf(
        Screen.Home,
        Screen.Favorites,
        Screen.Alerts,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(64.dp),
                tonalElevation = 4.dp

            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = stringResource(id = screen.label)
                            )
                        },

                        label = { Text(stringResource(id = screen.label)) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when navigating back
                                restoreState = true
                            }
                        },


                        )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(homeViewModel)
            }
            composable(Screen.Favorites.route) {

                val favoriteViewModel = viewModel<FavoriteViewModel>(
                    factory = FavoriteViewModelFactory(repository, settingsRepository)
                )
                FavoritesScreen(
                    viewModel = favoriteViewModel,
                    onNavigateToMap = { navController.navigate(Screen.Map.route) },
                    onNavigateToDetail = { lat, lon ->
                        navController.navigate(Screen.FavoriteDetail.createRoute(lat, lon))
                    }

                )
            }

            composable(Screen.Map.route) {
                val favoriteViewModel = viewModel<FavoriteViewModel>(
                    factory = FavoriteViewModelFactory(repository, settingsRepository)
                )
                MapScreen(
                    viewModel = favoriteViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.MapSettings.route) {
                val settingsViewModel = viewModel<SettingsViewModel>(
                    factory = SettingsViewModelFactory(settingsRepository)
                )
                MapScreenSettings(
                    settingsViewModel = settingsViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(
                route = Screen.FavoriteDetail.route,
                arguments = listOf(
                    navArgument("lat") { type = NavType.FloatType },
                    navArgument("lon") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
                val favoriteViewModel = viewModel<FavoriteViewModel>(
                    factory = FavoriteViewModelFactory(repository, settingsRepository)
                )
                FavoriteDetailScreen(
                    lat = lat,
                    lon = lon,
                    viewModel = favoriteViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.Alerts.route) {
                val alertsViewModel = viewModel<AlertsViewModel>(
                    factory = AlertsViewModel.AlertsViewModelFactory(repository)
                )
                AlertsScreen(viewModel = alertsViewModel)
            }
            composable(Screen.Settings.route) {
                val settingsViewModel = viewModel<SettingsViewModel>(
                    factory = SettingsViewModelFactory(settingsRepository)
                )
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToMap = { navController.navigate(Screen.MapSettings.route) }
                )
            }


        }
    }
}





