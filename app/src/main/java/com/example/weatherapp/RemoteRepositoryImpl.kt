package com.example.weatherapp

class RemoteRepositoryImpl (private val apiService: WeatherApiService ) : RemoteRepository  {
    override suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response {
        return  apiService.getForecast(lat, lon, apiKey)
    }

}