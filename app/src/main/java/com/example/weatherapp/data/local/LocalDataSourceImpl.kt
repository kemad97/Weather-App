package com.example.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl( private val favAlertsDao: FavoritesAndAlertsDao) : LocalDataSource {
    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        return favAlertsDao.getAllFavorites()
    }

    override suspend fun insertFavorite(favorite: FavoriteWeather) {
        favAlertsDao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteWeather) {
        favAlertsDao.deleteFavorite(favorite)
    }

    override fun getAllAlerts(): Flow<List<WeatherAlert>> {
    return favAlertsDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: WeatherAlert) {
    favAlertsDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: WeatherAlert) {
favAlertsDao.deleteAlert(alert)
    }


}