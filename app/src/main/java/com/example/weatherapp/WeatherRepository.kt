package com.example.weatherapp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository  {
    private val remoteRepository: RemoteRepository = RemoteRepositoryImpl(WeatherApi.retrofitService)

    fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<Response> = flow {
        val response = remoteRepository.getWeatherForecast(lat, lon, apiKey)
        emit(response)
    }

    companion object{
        @Volatile
        private var instance : WeatherRepository? = null
        fun getInstance() = instance ?: synchronized(this){
            instance ?: WeatherRepository().also { instance = it }
        }

    }
}