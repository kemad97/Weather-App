package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.data.SettingsRepositoryImpl
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteEntity>> = _favorites

    private val _selectedWeather = MutableStateFlow<ResultState<ApiResponse>>(ResultState.Empty)
    val selectedWeather: StateFlow<ResultState<ApiResponse>> = _selectedWeather

    private val _settings = MutableStateFlow<Settings?>(null)
    val settings: StateFlow<Settings?> = _settings

    private var originalResponse: ApiResponse? = null

    private var currentTempUnit = TemperatureUnit.CELSIUS
    private var currentWindUnit = WindSpeedUnit.METER_PER_SEC


    init {
        getFavorites()
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

    private fun convertWeatherData() {
        val currentWeather = (_selectedWeather.value as? ResultState.Success)?.data ?: return
        val currentSettings = _settings.value ?: return

        val convertedResponse = currentWeather.copy(
            list = currentWeather.list?.map { item ->
                item?.copy(
                    main = item.main?.copy(
                        temp = item.main.temp?.let { temp ->
                            convertTemperature(temp as Double, currentSettings.temperatureUnit)
                        }
                    ),
                    wind = item.wind?.copy(
                        speed = item.wind.speed?.let { speed ->
                            convertWindSpeed(speed as Double, currentSettings.windSpeedUnit)
                        }
                    )
                )
            }
        )
        _selectedWeather.value = ResultState.Success(convertedResponse)
    }


    private fun getFavorites() {
        viewModelScope.launch {
            repository.getAllFavorites().collect {
                _favorites.value = it
            }
        }
    }

    fun addFavorite(cityName: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            val favorite = FavoriteEntity(cityName = cityName, lat = lat, lon = lon)
            repository.insertFavorite(favorite)
        }
    }

    fun removeFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.deleteFavorite(favorite)
        }
    }

    fun getWeatherForLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _selectedWeather.value = ResultState.Loading
            try {
                repository.fetchWeather(lat, lon, BuildConfig.WEATHER_API_KEY)
                    .catch { e ->
                        Log.e("FavoriteViewModel", "Error fetching weather: ${e.message}")
                        _selectedWeather.value = ResultState.Error(e)
                    }
                    .collectLatest { response ->
                        Log.d("FavoriteViewModel", "Received weather data: $response")
                        originalResponse = response
                        _selectedWeather.value = ResultState.Success(response)
                        convertWeatherData() // Convert using current settings
                    }
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception in getWeatherForLocation: ${e.message}")
                _selectedWeather.value = ResultState.Error(e)
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

class FavoriteViewModelFactory(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repository, settingsRepository) as T
        }
        throw IllegalArgumentException("err FavViewModelFactory class")
    }


}
