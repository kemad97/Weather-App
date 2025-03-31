package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.WeatherDatabase
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.HomeViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = WeatherDatabase.getInstance(applicationContext)
        val localDataSource = LocalDataSourceImpl(database.favoritesAndAlertsDao())
        val repository = WeatherRepository.getInstance(localDataSource)
        val locationTracker = LocationTracker.getInstance(this)
        val viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(repository, locationTracker)
        )[HomeViewModel::class.java]
        setContent {
            WeatherAppTheme {
            MainScreen(viewModel, repository)
        }
        }

        }
    }
