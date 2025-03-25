package com.example.weatherapp.data.remote

import com.example.weatherapp.model.ApiResponse

class RemoteRepositoryImpl (private val apiService: WeatherApiService) : RemoteRepository {
    override suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): ApiResponse {
        return  apiService.getForecast(lat, lon, apiKey)
    }

}