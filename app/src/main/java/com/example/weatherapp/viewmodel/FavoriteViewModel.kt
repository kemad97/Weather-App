package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.LocationTracker
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteEntity>> = _favorites

    private val _selectedWeather = MutableStateFlow<ResultState<ApiResponse>>(ResultState.Empty)
    val selectedWeather: StateFlow<ResultState<ApiResponse>> = _selectedWeather


    init {
        getFavorites()
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
                repository.fetchWeather(lat, lon, BuildConfig.WEATHER_API_KEY).collect {
                    _selectedWeather.value = ResultState.Success(it)
                }
            } catch (e: Exception) {
                _selectedWeather.value = ResultState.Error(e)
            }
        }
    }
}

class FavoriteViewModelFactory(private val repository: WeatherRepository  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("err FavViewModelFactory class")
    }


}
