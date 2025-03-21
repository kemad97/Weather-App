package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.viewmodel.LocationViewModel
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val repository = WeatherRepository.getInstance()
        val locationViewModel = LocationViewModel(this)
        val viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(repository, locationViewModel)
        )[WeatherViewModel::class.java]
        setContent {
                HomeScreen(viewModel)
        }

        }
    }
