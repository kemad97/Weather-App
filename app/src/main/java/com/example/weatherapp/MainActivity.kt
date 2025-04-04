package com.example.weatherapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.data.SettingsRepositoryImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.home.HomeViewModel
import com.example.weatherapp.home.HomeViewModelFactory
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = WeatherDatabase.getInstance(applicationContext)
        val localDataSource = LocalDataSourceImpl(database.favoritesAndAlertsDao())
        val sharedPreferences = getSharedPreferences("weather_settings", MODE_PRIVATE)
        val repository = WeatherRepository.getInstance(localDataSource, sharedPreferences)
        val locationTracker = LocationTracker.getInstance(this)

        val settingsRepository = SettingsRepositoryImpl(sharedPreferences)


        val viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                repository,
                locationTracker,
                settingsRepository
            )
        )[HomeViewModel::class.java]



        setContent {
            WeatherAppTheme {
                MainScreen(viewModel, repository, settingsRepository)
            }
        }
    }


    }
