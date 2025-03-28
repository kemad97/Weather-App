package com.example.weatherapp.data

import com.example.weatherapp.data.local.FavoriteWeather
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.WeatherAlert
import com.example.weatherapp.data.remote.RemoteRepository
import com.example.weatherapp.data.remote.RemoteRepositoryImpl
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class WeatherRepository(
    private val localDataSource: LocalDataSource,
    private val remoteRepository: RemoteRepository = RemoteRepositoryImpl(WeatherApi.retrofitService)
) {

    fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<ApiResponse> = flow {
        val response = remoteRepository.getWeatherForecast(lat, lon, apiKey)
        emit(response)
    }

    fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        return localDataSource.getAllFavorites()
    }

    suspend fun insertFavorite(favorite: FavoriteWeather) {
        localDataSource.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(favorite: FavoriteWeather) {
        localDataSource.deleteFavorite(favorite)
    }


    fun getAllAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.getAllAlerts()
    }

    suspend fun insertAlert(alert: WeatherAlert) {
        localDataSource.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: WeatherAlert) {
        localDataSource.deleteAlert(alert)
    }

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(localDataSource: LocalDataSource): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(localDataSource).also { instance = it }
            }
        }
    }
}