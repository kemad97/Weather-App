package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val repository = WeatherRepository.getInstance()
        val locationTracker = LocationTracker.getInstance(this)
        val viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(repository, locationTracker)
        )[WeatherViewModel::class.java]
        setContent {
            MainScreen(viewModel)
        }

        }
    }
