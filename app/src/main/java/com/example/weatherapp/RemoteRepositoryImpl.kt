package com.example.weatherapp

class RemoteRepositoryImpl (private val apiService: WeatherApiService ) : RemoteRepository  {
    override suspend fun getWeather(lat: Double, lon: Double, apiKey: String): Response {
        return  apiService.getWeather(lat, lon, apiKey)
    }

}