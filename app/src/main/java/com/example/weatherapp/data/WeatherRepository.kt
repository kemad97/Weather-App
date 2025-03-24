package com.example.weatherapp.data

import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository  {
    private val remoteRepository: RemoteRepository = RemoteRepositoryImpl(WeatherApi.retrofitService)

    fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<ApiResponse> = flow {
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