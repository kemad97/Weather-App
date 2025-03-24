@file:Suppress("NAME_SHADOWING")

package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch

class WeatherViewModel (private val repository: WeatherRepository,    private val locationViewModel: LocationViewModel
) : ViewModel() {

    /*private val _weatherData = MutableStateFlow<ApiResponse?>(null)
    val weatherData: StateFlow<ApiResponse?> = _weatherData
    */
    private val _weatherData= MutableStateFlow<ResultState <ApiResponse>>(ResultState.Empty)
    val weatherData : StateFlow<ResultState<ApiResponse>> = _weatherData

    init {
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
                _weatherData.value=ResultState.Loading
                val apiKey = BuildConfig.WEATHER_API_KEY

                repository.fetchWeather(lat, lon, apiKey).collectLatest {
                    response ->
                    _weatherData.value = ResultState.Success(response)
                    Log.d("WeatherData", "Received: $response + ${response.city?.name}")

                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch weather data: ${e.message}")
                _weatherData.value=ResultState.Error(e)
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
