package com.example.weatherapp.data

import com.example.weatherapp.data.local.AlertEntity
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<ApiResponse>
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    suspend fun insertFavorite(favorite: FavoriteEntity)
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    fun getAllAlerts(): Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity)
    suspend fun deleteAlert(alert: AlertEntity)
}