package com.example.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl( private val favAlertsDao: FavoritesAndAlertsDao) : LocalDataSource {
    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favAlertsDao.getAllFavorites()
    }

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        favAlertsDao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favAlertsDao.deleteFavorite(favorite)
    }

    override fun getAllAlerts(): Flow<List<AlertEntity>> {
    return favAlertsDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertEntity) {
    favAlertsDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertEntity) {
favAlertsDao.deleteAlert(alert)
    }


}