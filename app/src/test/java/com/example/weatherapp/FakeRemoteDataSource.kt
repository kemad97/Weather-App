package com.example.weatherapp

import com.example.weatherapp.model.remote.RemoteRepository
import com.example.weatherapp.model.Response
import com.example.weatherapp.model.City

class FakeRemoteDataSource : RemoteRepository {
    override suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response {
        return Response(city = City(name = "Cairo")
        )
    }

    }