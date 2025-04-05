package com.example.weatherapp.model.repo

import com.example.weatherapp.model.local.AlertEntity
import com.example.weatherapp.model.local.FavoriteEntity
import com.example.weatherapp.model.Response
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<Response>
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    suspend fun insertFavorite(favorite: FavoriteEntity)
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    fun getAllAlerts(): Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity)
    suspend fun deleteAlert(alert: AlertEntity)
}