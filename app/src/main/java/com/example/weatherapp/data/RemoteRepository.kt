package com.example.weatherapp.data

import com.example.weatherapp.model.ApiResponse

interface RemoteRepository{
suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): ApiResponse
}