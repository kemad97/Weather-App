package com.example.weatherapp

interface RemoteRepository{
suspend fun getWeather(lat: Double, lon: Double, apiKey: String): Response
}