package com.example.weatherapp.data.remote

import com.example.weatherapp.model.ApiResponse

interface RemoteRepository{
suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): ApiResponse
}