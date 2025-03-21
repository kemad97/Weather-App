@file:Suppress("NAME_SHADOWING")

package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.model.Response
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch

class WeatherViewModel (private val repository: WeatherRepository,    private val locationViewModel: LocationViewModel
) : ViewModel() {

    private val _weatherData = MutableStateFlow<Response?>(null)
    val weatherData: StateFlow<Response?> = _weatherData

    init {
        //fetchWeather()
        observeLocationAndFetchWeather()

    }

     fun observeLocationAndFetchWeather() {
        viewModelScope.launch {
            locationViewModel.myLocation.collect { location ->
                location?.let { (lat, lon) ->
                    fetchWeather(lat, lon)
                }
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
              //  val lat = 30.0444
            //    val lon = 31.2357
                val apiKey = "557286fc08f4438364702631194d8280"

                repository.fetchWeather(lat, lon, apiKey).collectLatest {
                    response ->
                    _weatherData.value = response
                    Log.d("WeatherData", "Received: $response + ${response.city?.name}")

                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch weather data: ${e.message}")
            }
        }
    }
}



class WeatherViewModelFactory(private val repository: WeatherRepository ,
                              private val locationViewModel: LocationViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository, locationViewModel) as T
        }
        throw IllegalArgumentException("err WeatherViewModelFactory class")
    }
}
