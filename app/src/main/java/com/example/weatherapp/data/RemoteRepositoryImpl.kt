package com.example.weatherapp.data

import com.example.weatherapp.model.Response

class RemoteRepositoryImpl (private val apiService: WeatherApiService) : RemoteRepository {
    override suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response {
        return  apiService.getForecast(lat, lon, apiKey)
    }

}