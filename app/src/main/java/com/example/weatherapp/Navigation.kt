package com.example.weatherapp

import FavoriteDetailScreen
import android.content.Intent
import android.provider.Settings
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
import androidx.navigation.toRoute
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.ui.screens.AlertsScreen
import com.example.weatherapp.ui.screens.FavoritesScreen
import com.example.weatherapp.ui.screens.HomeScreen
import com.example.weatherapp.ui.screens.MapScreen
import com.example.weatherapp.ui.screens.SettingsScreen
import com.example.weatherapp.viewmodel.FavoriteViewModel
import com.example.weatherapp.viewmodel.FavoriteViewModelFactory
import com.example.weatherapp.viewmodel.WeatherViewModel

sealed class Screen(
    val route: String,
    var icon: Int,
    val label: String
) {
    data object Home : Screen("home",R.drawable.ic_home, "Home")
    data object Favorites : Screen("favorites", R.drawable.ic_fav, "Favorites")
    data object Alerts : Screen("alerts", R.drawable.ic_alerts, "Alerts")
    data object Settings : Screen("settings", R.drawable.ic_settings, "Settings")
    data object Map : Screen("map", 0, "")

    data object FavoriteDetail : Screen("favorite_detail/{lat}/{lon}", 0, "") {
        fun createRoute(lat: Double, lon: Double) = "favorite_detail/$lat/$lon"
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(weatherViewModel: WeatherViewModel, repository: WeatherRepository)
{
    val navController = rememberNavController()
    val context = LocalContext.current
    val isLocationEnabled by LocationTracker(context).isLocationEnabled.collectAsState()
    var showDialog by remember { mutableStateOf(true) }


    if (!isLocationEnabled && showDialog) {
        AlertDialog(
            onDismissRequest = {
              //  showDialog = false
            },
            title = { Text("Location Required") },
            text = { Text("Please enable location services to get weather information.") },
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
                                contentDescription = screen.label) },

                        label = { Text(screen.label) },
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
                HomeScreen(weatherViewModel)
            }
            composable(Screen.Favorites.route) {
                val favoriteViewModel = viewModel<FavoriteViewModel>(
                    factory = FavoriteViewModelFactory(repository)
                )
                FavoritesScreen(
                    viewModel = favoriteViewModel,
                    onNavigateToMap = { navController.navigate(Screen.Map.route) },
                    onNavigateToDetail = { lat, lon ->
                        navController.navigate(Screen.FavoriteDetail.createRoute(lat, lon))}

                )
            }

            composable(Screen.Map.route) {
                val favoriteViewModel = viewModel<FavoriteViewModel>(
                    factory = FavoriteViewModelFactory(repository)
                )
                MapScreen(
                    viewModel = favoriteViewModel,
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
                    factory = FavoriteViewModelFactory(repository)
                )
                FavoriteDetailScreen(
                    lat = lat,
                    lon = lon,
                    viewModel = favoriteViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.Alerts.route) {
                AlertsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }



        }
    }
}





