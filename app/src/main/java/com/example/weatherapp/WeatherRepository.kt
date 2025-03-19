package com.example.weatherapp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository ( ) {
    private val remoteRepository: RemoteRepository = RemoteRepositoryImpl(WeatherApi.retrofitService)

    fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<Response> = flow {
        val response = remoteRepository.getWeather(lat, lon, apiKey)
        emit(response)
    }
}