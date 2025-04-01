@file:Suppress("NAME_SHADOWING")

package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.LocationTracker
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.data.SettingsRepositoryImpl
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _weatherData = MutableStateFlow<ResultState<ApiResponse>>(ResultState.Loading)
    val weatherData: StateFlow<ResultState<ApiResponse>> = _weatherData

    private val _settings = MutableStateFlow<Settings?>(null)
    val settings: StateFlow<Settings?> = _settings

    private var currentTempUnit = TemperatureUnit.CELSIUS
    private var currentWindUnit = WindSpeedUnit.METER_PER_SEC


    init {
        _weatherData.value = ResultState.Loading
        observeLocationAndFetchWeather()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { newSettings ->

                currentTempUnit = _settings.value?.temperatureUnit ?: TemperatureUnit.CELSIUS
                currentWindUnit = _settings.value?.windSpeedUnit ?: WindSpeedUnit.METER_PER_SEC

                _settings.value = Settings(
                    useGPS = newSettings.useGPS,
                    temperatureUnit = newSettings.temperatureUnit,
                    windSpeedUnit = newSettings.windSpeedUnit,
                    language = newSettings.language
                )
                convertWeatherData()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun convertWeatherData() {
        val currentWeather = (_weatherData.value as? ResultState.Success)?.data ?: return
        val currentSettings = _settings.value ?: return

        val convertedResponse = currentWeather.copy(
            list = currentWeather.list?.map { item ->
                item?.copy(
                    main = item.main?.copy(
                        temp = item.main.temp?.let { temp ->
                            String.format(
                                "%.1f",
                                convertTemperature(temp as Double, currentSettings.temperatureUnit)
                            )
                                .toDouble()
                        }
                    ),
                    wind = item.wind?.copy(
                        speed = item.wind.speed?.let { speed ->
                            String.format(
                                "%.1f",
                                convertWindSpeed(speed as Double, currentSettings.windSpeedUnit)
                            )
                                .toDouble()
                        }
                    )
                )
            }
        )
        _weatherData.value = ResultState.Success(convertedResponse)
    }

    fun observeLocationAndFetchWeather() {
        viewModelScope.launch {
            locationTracker.myLocation.collect { location ->
                location?.let { (lat, lon) ->
                    fetchWeather(lat, lon)
                }
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {

                _weatherData.value = ResultState.Loading
                val apiKey = BuildConfig.WEATHER_API_KEY

                repository.fetchWeather(lat, lon, apiKey).collectLatest { response ->
                    _weatherData.value = ResultState.Success(response)
                    Log.d("WeatherData", " API Response: $response")
                    Log.d("WeatherData", "Received: $response + ${response.city?.name}")

                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch weather data: ${e.message}")
                _weatherData.value = ResultState.Error(e)
            }
        }
    }


    private fun convertTemperature(value: Double, targetUnit: TemperatureUnit): Double {
        val celsius = when (currentTempUnit) {
            TemperatureUnit.CELSIUS -> value
            TemperatureUnit.FAHRENHEIT -> (value - 32) * 5 / 9
            TemperatureUnit.KELVIN -> value - 273.15
        }

        val result = when (targetUnit) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * 9 / 5) + 32
            TemperatureUnit.KELVIN -> celsius + 273.15
        }

        return String.format("%.1f", result).toDouble()
    }

    private fun convertWindSpeed(value: Double, targetUnit: WindSpeedUnit): Double {
        val mps = when (currentWindUnit) {
            WindSpeedUnit.METER_PER_SEC -> value
            WindSpeedUnit.MILES_PER_HOUR -> value / 2.237
        }

        val result = when (targetUnit) {
            WindSpeedUnit.METER_PER_SEC -> mps
            WindSpeedUnit.MILES_PER_HOUR -> mps * 2.237
        }

        return String.format("%.1f", result).toDouble()
    }


}

class HomeViewModelFactory(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, locationTracker, settingsRepository) as T
        }
        throw IllegalArgumentException("err WeatherViewModelFactory class")
    }
}

