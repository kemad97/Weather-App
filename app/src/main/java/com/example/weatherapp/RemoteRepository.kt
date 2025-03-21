package com.example.weatherapp

interface RemoteRepository{
suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response
}