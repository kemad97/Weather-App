@file:Suppress("NAME_SHADOWING")

package com.example.weatherapp.home

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
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.viewmodel.LocationMethod
import com.example.weatherapp.viewmodel.Settings
import com.example.weatherapp.viewmodel.TemperatureUnit
import com.example.weatherapp.viewmodel.WindSpeedUnit
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline


    private val _weatherData = MutableStateFlow<ResultState<ApiResponse>>(ResultState.Loading)
    val weatherData: StateFlow<ResultState<ApiResponse>> = _weatherData

    private val _settings = MutableStateFlow<Settings?>(null)
    val settings: StateFlow<Settings?> = _settings

    private var currentTempUnit = TemperatureUnit.CELSIUS
    private var currentWindUnit = WindSpeedUnit.METER_PER_SEC


    init {

        observeLocationAndFetchWeather()
        observeSettings()

    }


//    private fun checkConnectivity() {
//        viewModelScope.launch {
//            try {
//                repository.fetchWeather(0.0, 0.0, BuildConfig.WEATHER_API_KEY)
//                _isOnline.value = true
//            } catch (e: Exception) {
//                _isOnline.value = false
//            }
//        }
//    }

     fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { newSettings ->

            currentTempUnit = _settings.value?.temperatureUnit ?: TemperatureUnit.CELSIUS
            currentWindUnit = _settings.value?.windSpeedUnit ?: WindSpeedUnit.METER_PER_SEC

                _settings.value = newSettings

                val newTemperatureUnit= newSettings.temperatureUnit
                val newWindSpeedUni= newSettings.windSpeedUnit

                if (newSettings.locationMethod == LocationMethod.GPS) {
                    locationTracker.getLocationUpdates()
                }
                if(newTemperatureUnit != currentTempUnit || newWindSpeedUni != currentWindUnit) {
                    Log.i("homeview", "convert data inside settings")
                    convertWeatherData()

                }
            }
        }
    }


    @SuppressLint("DefaultLocale")
     fun convertWeatherData() {
        val currentWeather = (_weatherData.value as? ResultState.Success)?.data ?: return
        val currentSettings = _settings.value ?: return

        val convertedResponse = currentWeather.copy(
            list = currentWeather.list?.map { item ->
                item?.copy(
                    main = item.main?.copy(
                        temp = item.main.temp?.toString()?.toDoubleOrNull()?.let { temp ->
                            convertTemperature(temp, currentSettings.temperatureUnit)
                        },
                        tempMin = item.main.tempMin?.toString()?.toDoubleOrNull()?.let { temp ->
                            convertTemperature(temp, currentSettings.temperatureUnit)
                        },
                        tempMax = item.main.tempMax?.toString()?.toDoubleOrNull()?.let { temp ->
                            convertTemperature(temp, currentSettings.temperatureUnit)
                        },
                        feelsLike = item.main.feelsLike?.toString()?.toDoubleOrNull()?.let { temp ->
                            convertTemperature(temp, currentSettings.temperatureUnit)
                        }
                    ),
                    wind = item.wind?.copy(
                        speed = item.wind.speed?.toString()?.toDoubleOrNull()?.let { speed ->
                            convertWindSpeed(speed, currentSettings.windSpeedUnit)
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

                    try {
                        _weatherData.value = ResultState.Loading
                        val apiKey = BuildConfig.WEATHER_API_KEY

                        repository.fetchWeather(lat, lon, apiKey).collectLatest { response ->

                            val currentSettings = _settings.value
                            _weatherData.value = ResultState.Success(response)

                            if (currentSettings != null) {
                                Log.i("homeview", "convert data inside fetch weather")

                                convertWeatherData()
                            }

                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Failed to fetch weather data: ${e.message}")
                        _weatherData.value = ResultState.Error(e)
                    }
                }
            }
        }
    }

//     fun fetchWeather(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            try {
//                _weatherData.value = ResultState.Loading
//                val apiKey = BuildConfig.WEATHER_API_KEY
//
//                repository.fetchWeather(lat, lon, apiKey).collectLatest { response ->
//
//                    val currentSettings = _settings.value
//                    _weatherData.value = ResultState.Success(response)
//
//                    if (currentSettings != null) {
//                        convertWeatherData()
//                    }
//
//                }
//            } catch (e: Exception) {
//                Log.e("API_ERROR", "Failed to fetch weather data: ${e.message}")
//                _weatherData.value = ResultState.Error(e)
//            }
//        }
//    }


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

