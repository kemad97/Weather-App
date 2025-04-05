package com.example.weatherapp.model.remote

import com.example.weatherapp.model.ApiResponse

interface RemoteRepository {
    suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): ApiResponse
}