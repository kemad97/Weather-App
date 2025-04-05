package com.example.weatherapp.model.remote

import com.example.weatherapp.model.Response

interface RemoteRepository {
    suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response
}