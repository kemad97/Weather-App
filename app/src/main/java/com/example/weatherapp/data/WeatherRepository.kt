package com.example.weatherapp.data

import android.content.SharedPreferences
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.AlertEntity
import com.example.weatherapp.data.remote.RemoteRepository
import com.example.weatherapp.data.remote.RemoteRepositoryImpl
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.model.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class WeatherRepository(
    private val localDataSource: LocalDataSource,
    private val sharedPreferences: SharedPreferences,
    private val remoteRepository: RemoteRepository = RemoteRepositoryImpl(
        WeatherApi.retrofitService, sharedPreferences)
):IWeatherRepository {

    override fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<ApiResponse> = flow {
        val response = remoteRepository.getWeatherForecast(lat, lon, apiKey)
        emit(response)
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return localDataSource.getAllFavorites()
    }

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        localDataSource.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        localDataSource.deleteFavorite(favorite)
    }


    override fun getAllAlerts(): Flow<List<AlertEntity>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertEntity) {
        localDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertEntity) {
        localDataSource.deleteAlert(alert)
    }

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(localDataSource: LocalDataSource ,sharedPreferences: SharedPreferences): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(localDataSource, sharedPreferences  ).also { instance = it }
            }
        }
    }
}