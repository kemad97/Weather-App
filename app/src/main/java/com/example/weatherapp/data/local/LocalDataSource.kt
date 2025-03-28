package com.example.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllFavorites(): Flow<List<FavoriteWeather>>
    suspend fun insertFavorite(favorite: FavoriteWeather)
    suspend fun deleteFavorite(favorite: FavoriteWeather)

    // Alerts
    fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert)
    suspend fun deleteAlert(alert: WeatherAlert)
}